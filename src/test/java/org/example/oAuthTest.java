package org.example;

import io.restassured.path.json.JsonPath;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

public class oAuthTest {

    @Test
    public void oauthTest() {
        // Authorization Server
        // Grant type: client credentials
        // Post
        String response = given()
                .formParam("client_id", "692183103107-p0m7ent2hk7suguv4vq22hjcfhcr43pj.apps.googleusercontent.com")
                .formParam("client_secret", "erZOWM9g3UtwNRj340YYaK_W")
                .formParam("grant_type", "client_credentials")
                .formParam("scope", "trust")
                .when()
                        .log().all()
                        .post("https://rahulshettyacademy.com/oauthapi/oauth2/resourceOwner/token")
                        .asString();

        JsonPath js = new JsonPath(response);
        String token = js.get("access_token");
        System.out.println(token);

        // Get
        String getResponse = given()
                .queryParam("access_token", token)
                .when()
                        .get("https://rahulshettyacademy.com/oauthapi/getCourseDetails")
                        .asString();

        System.out.println(getResponse);
    }
}
