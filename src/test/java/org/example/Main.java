package org.example;

import files.Payload;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class Main {
    public static void main(String[] args) {
        RestAssured.baseURI = "https://rahulshettyacademy.com";

        // POST: Add a place on Google Maps API
        String response = given()
                //.log().all()
                .queryParam("key", "qaclick123")
                .header("Content-Type", "application/json")
                .body(Payload.Addplace())
                .when()
                    .post("maps/api/place/add/json")
                .then().
                    //log().all().
                    assertThat()
                        .statusCode(200)
                        .body("scope", equalTo("APP"))
                        .header("server", "Apache/2.4.52 (Ubuntu)")
                        .extract().response().asString();

        //System.out.println(response);
        JsonPath js = new JsonPath(response); // parse the JSON
        String placeId = js.getString("place_id");
        //System.out.println(placeId);

        // PUT: Update address
        String updatedAddress = "70 Summer walk, USA";
        given()
                //.log().all()
                .queryParam("key", "qaclick123")
                .header("Content-Type", "application/json")
                .body("{\n" +
                        "\"place_id\":\""+placeId+"\",\n" +
                        "\"address\":\""+updatedAddress+"\",\n" +
                        "\"key\":\"qaclick123\"\n" +
                        "}\n")
                .when()
                    .put("maps/api/place/update/json")
                .then()
                    //.log().all()
                    .assertThat()
                    .statusCode(200)
                    .body("msg", equalTo("Address successfully updated"));

        // GET: Retrieve address
        String getResponse = given()
                .queryParam("key", "qaclick123")
                //.header("Content-Type", "application/json")
                .queryParam("place_id", placeId)
                .when()
                    .get("maps/api/place/get/json")
                .then()
                    //.log().all()
                    .assertThat()
                    .statusCode(200)
                    .body("address", equalTo(updatedAddress))
                    .extract().response().asString();

        //System.out.println(getResponse);
        JsonPath js1 = new JsonPath(getResponse);
        String actualAddress = js1.getString("address");

        Assert.assertEquals(actualAddress, updatedAddress);
    }
}