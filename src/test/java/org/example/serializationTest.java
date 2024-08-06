package org.example;

import org.testng.annotations.Test;
import pojo.AddPlace;
import pojo.Location;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;

public class serializationTest {

    @Test
    public void serialtest() {
        AddPlace addPlace = new AddPlace();

        addPlace.setName("Daniel");
        List<String> list = new ArrayList<String>();
        addPlace.setTypes(list);
        Location location = new Location();
        addPlace.setLocation(location);

        given()
                .queryParam("key", "qaclick123")
                .body(addPlace)
                .when()
                        .post("maps/api/place/add/json")
                .then()
                        .assertThat()
                        .statusCode(200)
                        .extract()
                        .response()
                        .asString();
    }
}
