package com.sky.test;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HttpClientTest {

    private static final String URL = "http://localhost:8080/user/shop/status";

    @Test
    public void testGet() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(URL);
            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                int status = response.getStatusLine().getStatusCode();
                System.out.println("当前状态是: " + status);

                // 使用 EntityUtils.toString 获取响应体的字符串表示
                String responseBody = EntityUtils.toString(response.getEntity());
                System.out.println("服务器返回的数据: " + responseBody);

                // 在这里使用 responseBody 进行进一步的处理
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 可以添加错误处理或记录日志等
        }
    }
}
