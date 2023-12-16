package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.AddressBookService;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

	@Autowired
	private AddressBookMapper addressBookMapper;

	@Autowired
	private ShoppingCartMapper shopCartMapper;

	@Autowired
	private OrderMapper orderMapper;

	@Autowired
	private OrderDetailMapper orderDetailMapper;

	/**
	 * オーダーをコミットする
	 * 
	 * @param ordersSubmitDTO
	 */
	@Transactional
	public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {

		// アドレスの有無
		AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
		if (addressBook == null) {
			throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
		}
		// カート空いているかどうか
		ShoppingCart shoppingCart = new ShoppingCart();
		shoppingCart.setUserId(BaseContext.getCurrentId());
		List<ShoppingCart> list = shopCartMapper.list(shoppingCart);

		if (list == null || list.size() == 0) {
			throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
		}

		// orderテーブルに入れる
		Orders orders = new Orders();
		BeanUtils.copyProperties(ordersSubmitDTO, orders);
		orders.setOrderTime(LocalDateTime.now());
		orders.setPayStatus(Orders.UN_PAID);
		orders.setStatus(Orders.PENDING_PAYMENT);
		orders.setNumber(String.valueOf(System.currentTimeMillis()));
		orders.setPhone(addressBook.getPhone());
		orders.setConsignee(addressBook.getConsignee());
		orders.setUserId(BaseContext.getCurrentId());

		orderMapper.insert(orders);

		ArrayList<OrderDetail> arrayList = new ArrayList<>();

		// orderdetailテーブルに入れる
		for (ShoppingCart shoppingCar : list) {
			OrderDetail orderDetail = new OrderDetail();
			BeanUtils.copyProperties(shoppingCar, orderDetail);
			orderDetail.setOrderId(orders.getId());
			arrayList.add(orderDetail);

		}

		orderDetailMapper.insertBatch(arrayList);

		// 該当するユーザーのカートを空にする
		shopCartMapper.cleanShoppingCart(BaseContext.getCurrentId());

		// データをVOに封じこんで、リターンする。
		OrderSubmitVO orderSubmitVO = OrderSubmitVO.builder().id(orders.getId()).orderTime(orders.getOrderTime())
				.orderNumber(orders.getNumber()).orderAmount(orders.getAmount()).build();
		return orderSubmitVO ;
	}

}
