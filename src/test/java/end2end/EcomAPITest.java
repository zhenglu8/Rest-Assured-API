package end2end;

import end2end.pojo.*;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;

public class EcomAPITest {
    @Test
    public void test() {

        // Login
        RequestSpecification request = new RequestSpecBuilder()
                .setBaseUri("https://rahulshettyacademy.com")
                .setContentType(ContentType.JSON)
                .build();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUserEmail("postman@gmail.com");
        loginRequest.setUserPassword("Hello123@");

        LoginResponse loginResponse = given()
                .log().all()
                .spec(request)
                .body(loginRequest)
                .when()
                        .post("api/ecom/auth/login")
                .then()
                        .log().all()
                        .extract()
                        .response()
                        .as(LoginResponse.class);
        System.out.println(loginResponse.getToken());
        String sessionToken = loginResponse.getToken();

        System.out.println(loginResponse.getUserId());
        String userId = loginResponse.getUserId();

        System.out.println(loginResponse.getMessage());

        // Create product
        RequestSpecification addProductRequest = new RequestSpecBuilder()
                .setBaseUri("https://rahulshettyacademy.com")
                .addHeader("authorization", sessionToken)
                .build();

        AddProductResponse addProductResponse = given()
                .log().all()
                .spec(addProductRequest)
                .param("productName", "Laptop")
                .param("productAddedBy", userId)
                .param("productCategory", "fashion")
                .param("productSubCategory", "shirts")
                .param("productPrice", "11500")
                .param("productDescription", "Lenova")
                .param("productFor", "men")
                .multiPart("productImage", new File("/Users/zhenglu/Downloads/ToniKroos.jpg"))
                .post("api/ecom/product/add-product")
                .then()
                        .extract()
                        .response()
                        .as(AddProductResponse.class);

        String productId = addProductResponse.getProductId();
        System.out.println(productId);

        System.out.println(addProductResponse.getMessage());

        // Create order
        RequestSpecification createOrderRequest = new RequestSpecBuilder()
                .setBaseUri("https://rahulshettyacademy.com")
                .addHeader("authorization", sessionToken)
                .setContentType(ContentType.JSON)
                .build();

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setCountry("USA");
        orderDetail.setProductOrderedId(productId);

        List<OrderDetail> orderDetailList = new ArrayList<>();
        orderDetailList.add(orderDetail);

        OrdersRequest ordersRequest = new OrdersRequest();
        ordersRequest.setOrders(orderDetailList);

        OrdersResponse ordersResponse = given()
                .spec(createOrderRequest)
                .body(ordersRequest)
                .when()
                        .post("api/ecom/order/create-order")
                .then()
                        .extract()
                        .response()
                        .as(OrdersResponse.class);

        List<String> ordersResults = ordersResponse.getOrders();
        List<String> productsResults = ordersResponse.getProductOrderId();
        for(int i=0; i<ordersResults.size(); i++) {
            System.out.println(ordersResults.get(i));
            System.out.println(productsResults.get(i));
        }
        System.out.println(ordersResponse.getMessage());

        // Delete product
        RequestSpecification deleteRequest = new RequestSpecBuilder()
                .setBaseUri("https://rahulshettyacademy.com")
                .addHeader("authorization", sessionToken)
                .setContentType(ContentType.JSON)
                .build();

        String deleteResponse = given()
                .spec(deleteRequest)
                .pathParams("productId", productId)
                .when()
                        .delete("/api/ecom/product/delete-product/{productId}")
                .then()
                .extract()
                .response()
                .asString();

        JsonPath js = new JsonPath(deleteResponse);
        String deleteMsg = js.get("message");
        String deleteExpectMsg = "Product Deleted Successfully";
        Assert.assertEquals(deleteMsg, deleteExpectMsg);
    }
}
