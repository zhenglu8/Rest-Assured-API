package org.example;

import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.Test;
import pojo.Api;
import pojo.GetCourse;
import pojo.WebAutomation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

        // Deserialization
        GetCourse getCourse = given()
                .queryParam("access_token", token)
                .when()
                .get("https://rahulshettyacademy.com/oauthapi/getCourseDetails")
                .as(GetCourse.class);
        System.out.println(getCourse.getLinkedIn());
        System.out.println(getCourse.getCourses().getApi().get(1).getCourseTitle());

        List<Api> apiCourses = getCourse.getCourses().getApi();
        for(int i=0; i<apiCourses.size(); i++) {
            if(apiCourses.get(i).getCourseTitle().equalsIgnoreCase("SoapUI Webservices testing")) {
                System.out.println(apiCourses.get(i).getPrice());
            }
        }

        List<WebAutomation> webautomationCourses = getCourse.getCourses().getWebAutomation();
        String[] courseTitles = {"Selenium Webdriver Java", "Cypress", "Protractor"};
        for(int i=0; i<webautomationCourses.size(); i++) {
            Assert.assertEquals(webautomationCourses.get(i).getCourseTitle(), courseTitles[i]);
        }
        List<String> actualTitles = new ArrayList<String>();
        for(int i=0; i< webautomationCourses.size(); i++) {
            actualTitles.add(webautomationCourses.get(i).getCourseTitle());
        }

        Assert.assertTrue(actualTitles.equals(Arrays.asList(courseTitles)));
        Assert.assertEquals(actualTitles, Arrays.asList(courseTitles));
    }
}
