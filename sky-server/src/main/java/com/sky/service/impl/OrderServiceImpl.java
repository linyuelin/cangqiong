package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.entity.User;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.AddressBookService;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.webSocket.WebSocketServer;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	@Autowired
	private WeChatPayUtil weChatPayUtil;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private WebSocketServer webSocketServer;

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
		return orderSubmitVO;
	}

	/**
	 * 订单支付
	 *
	 * @param ordersPaymentDTO
	 * @return
	 */
	public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
		// 当前登录用户id
		Long userId = BaseContext.getCurrentId();
		User user = userMapper.getById(userId);

		// 调用微信支付接口，生成预支付交易单
		JSONObject jsonObject = weChatPayUtil.pay(ordersPaymentDTO.getOrderNumber(), // 商户订单号
				new BigDecimal(0.01), // 支付金额，单位 元
				"苍穹外卖订单", // 商品描述
				user.getOpenid() // 微信用户的openid
		);

		if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
			throw new OrderBusinessException("该订单已支付");
		}

		OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
		vo.setPackageStr(jsonObject.getString("package"));

		return vo;
	}

	/**
	 * 支付成功，修改订单状态
	 *
	 * @param outTradeNo
	 */
	public void paySuccess(String outTradeNo) {

		// 根据订单号查询订单
		Orders ordersDB = orderMapper.getByNumber(outTradeNo);

		// 根据订单id更新订单的状态、支付方式、支付状态、结账时间
		Orders orders = Orders.builder().id(ordersDB.getId()).status(Orders.TO_BE_CONFIRMED).payStatus(Orders.PAID)
				.checkoutTime(LocalDateTime.now()).build();

		orderMapper.update(orders);
		
		//webSocketによって、クライアントへメッセージを送る
		Map map =new HashMap();
		map.put("type", 1);
		map.put("orderId", ordersDB.getId());
		map.put("content", "オーダーナンバー　:" +outTradeNo);
		String json =JSON.toJSONString(map);
		webSocketServer.sendToAllClient(json);
		
		
	}

}
