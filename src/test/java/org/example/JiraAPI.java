package org.example;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.*;

public class JiraAPI {


    // Create Bug on Jira
    // Then add attachment
    @Test
    public void createBug() {

        RestAssured.baseURI = "https://my-domain-name.atlassian.net/";

        String createBugResponse = given()
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic XXX")
                .body("{\n" +
                        "    \"fields\": {\n" +
                        "       \"project\":\n" +
                        "       {\n" +
                        "          \"key\": \"TEST\"\n" +
                        "       },\n" +
                        "       \"summary\": \"Button not working\",\n" +
                        "       \"description\": \"Creating of an issue using project keys and issue type names using the REST API\",\n" +
                        "       \"issuetype\": {\n" +
                        "          \"name\": \"Bug\"\n" +
                        "       }\n" +
                        "   }\n" +
                        "}\n")
                .log().all()
                .post("rest/api/3/issue")
                .then()
                        .log().all()
                        .assertThat()
                        .statusCode(201)
                        .extract()
                        .response()
                        .asString();

        JsonPath js = new JsonPath(createBugResponse);
        String actualMsg = js.get("Message");
        String expectedMsg = "Expected Message";
        Assert.assertEquals(actualMsg, expectedMsg);

        String issueID = js.get("id");

        given()
                .header("Authorization", "Basic XXX")
                .header("X-Atlassian-Token", "no-check")
                .multiPart("file", new File("Path"))
                .pathParams("key",issueID)
                .log().all()
                .post("rest/api/3/issue/{key}/attachments")
                .then()
                        .log().all()
                        .assertThat()
                        .statusCode(200)
                        .extract()
                        .response()
                        .asString();
    }


}
