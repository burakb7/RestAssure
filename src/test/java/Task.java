import POJOClasses.ToDo;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class Task {
    /**
     * Task 1
     * create a get request to https://jsonplaceholder.typicode.com/todos/2
     * expect status 200
     * Convert Into POJO
     */

    @Test
    void task1() {
        ToDo todo = given()
                .pathParam("APIName", "todos")
                .pathParam("id", "2")
                .when()
                .get("https://jsonplaceholder.typicode.com/{APIName}/{id}")
                .then()
                // .log().body()
                .statusCode(200)
                .extract().as(ToDo.class);

        System.out.println("todo = " + todo);
    }
    /**
     * Task 2
     * create a get request to https://httpstat.us/203
     * expect status 203
     * expect content type TEXT
     */

    @Test
    void task2() {
        given()
                .pathParam("status", "203")
                .when()
                .get("https://httpstat.us/{status}")
                .then()
                .statusCode(203)
                .contentType(ContentType.TEXT);
    }

    /**
     * Task 3
     * create a get request to https://jsonplaceholder.typicode.com/todos/2
     * expect status 200
     * expect content type JSON
     * expect title in response body to be "quis ut nam facilis et officia qui"
     */

    @Test
    void task3() {
    //        given()
    //                .pathParam("APIName","todos")
    //                .pathParam("id","2")
    //                .when()
    //                .get("https://jsonplaceholder.typicode.com/{APIName}/{id}")
    //                .then()
    //                .statusCode(200)
    //                .contentType(ContentType.JSON)
    //                .body("title",equalTo("quis ut nam facilis et officia qui"));

        String title = given()
                .pathParam("APIName", "todos")
                .pathParam("id", "2")
                .when()
                .get("https://jsonplaceholder.typicode.com/{APIName}/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .extract().path("title");

        Assert.assertTrue(title.equals("quis ut nam facilis et officia qui"));
    }

    /**
     * Task 4
     * create a get request to https://jsonplaceholder.typicode.com/todos/2
     * expect status 200
     * expect content type JSON
     * expect response completed status to be false
     */

    @Test
    void task4() {
//        Boolean completed = given()
//                .pathParam("APIName", "todos")
//                .pathParam("id", "2")
//                .when()
//                .get("https://jsonplaceholder.typicode.com/{APIName}/{id}")
//                .then()
//                .statusCode(200)
//                .contentType(ContentType.JSON)
//                .extract().path("completed");
//
//        Assert.assertFalse(completed);

        given()
                .pathParam("APIName","todos")
                .pathParam("id","2")
                .when()
                .get("https://jsonplaceholder.typicode.com/{APIName}/{id}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("completed",equalTo(false));
    }
}
