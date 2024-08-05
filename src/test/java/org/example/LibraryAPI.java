package org.example;

import files.Payload;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.*;

public class LibraryAPI {

    @DataProvider(name="BooksData")
    public Object[][] getData() {
        return new Object[][] {
                {"objfwty", "949"},
                {"okjw", "987"},
                {"yuhji", "435"}
        };
    }

    @Test(dataProvider = "BooksData")
    public void addBook(String aisle, String isbn) {
        RestAssured.baseURI="http://216.10.245.166";

        //String addAisle = "asfde";
        //String addIsbn = "646";

        String response = given()
                .header("Content-Type", "application/json")
                .body(Payload.addBook(aisle, isbn))
                .when()
                        .post("/Library/Addbook.php")
                .then()
                        .log().all()
                        .assertThat()
                        .statusCode(200)
                        .extract().response().asString();

        JsonPath js = new JsonPath(response);
        String bookID = js.getString("ID");
        Assert.assertEquals(bookID, isbn+aisle);

    }
}
