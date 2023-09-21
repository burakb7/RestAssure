import POJOClasses.Location;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ZippoTest {

    @Test
    void test1() {

        given() //preperation (token, request body, parameters, cookies...)
                .when() // for url, request method (get, post, put, patch, delete )
                .then(); // response (response body, tests, extract data from the response)

    }

    @Test
    void statusCodeTest() {
        given()
                .when()
                .get("https://api.zippopotam.us/us/90210")
                .then()
                .log().body() // prints response body to the console
                .log().status() // prints the status of the request
                .statusCode(200); //tests if the status code is the same with the expected
    }

    @Test
    void contentTypeTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .contentType(ContentType.JSON); // tests if the response body is in JSON format
    }

    @Test
    void checkCountryFromResponseBody() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("country", equalTo("United States"));

        // Hamcrest: Lets us to test values from the response body
    }

    // Postman                                      Rest Assured
    // pm.response.json() -> body                       body()
    // pm.response.json().country                       body("country")
    // pm.response.json().places[0].'places name'       body("places[0].'place name'") //gives the place name variable of the first element of places list
    // if the variable name has spaces in it write it between ' '

    @Test
    void checkStateFromResponse() {

        // Send a request to "http://api.zippopotam.us/us/90210"
        // and check if the state is "California"

        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .statusCode(200)
                .body("places[0].state", equalTo("California"));
    }

    @Test
    void checkStateAbbreviationFromResponse() {
        // Send a request to "http://api.zippopotam.us/us/90210"
        // and check if the state abbreviation is "CA"

        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("places[0].'state abbreviation'", equalTo("CA"));
    }

    @Test
    void bodyArrayHasItem() {
        // Send a request to "http://api.zippopotam.us/tr/01000"
        // and check if the body has "Büyükdikili Köyü"

        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")
                .then()
                .log().body()
                .body("places.'place name'", hasItem("Büyükdikili Köyü"));
        // When we don't use index it gets all place names from the response and creates an array with them.
        // hasItem checks if that array contains "Büyükdikili Köyü" value in it
    }

    @Test
    void arraySizeTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("places", hasSize(1)); // checks the size of the array in the body
    }

    @Test
    void arraySizeTest2() {
        // Send a request to "http://api.zippopotam.us/tr/01000"
        // and check if the place name array's size is 71

        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")
                .then()
                .log().body()
                .body("places.'place name'", hasSize(71));
    }

    @Test
    void multipleTest() {
        given()
                .when()
                .get("http://api.zippopotam.us/tr/01000")
                .then()
                .log().body()
                .statusCode(200)
                .body("places", hasSize(71))
                .body("places.'place name'", hasItem("Büyükdikili Köyü"))
                .contentType(ContentType.JSON);
    }

    @Test
    void pathParameterTest() { // the parameters that are separated with / are called path parameters
        given()
                .pathParam("Country", "us")
                .pathParam("ZipCode", "90210")
                .log().uri() // prints the request url
                .when()
                .get("http://api.zippopotam.us/{Country}/{ZipCode}")
                .then()
                .log().body()
                .statusCode(200);
    }

    @Test
    void pathParameterTest2() {
        // send a get request for zipcodes between 90210 and 90213 and verify that in all responses the size
        // of the places array is 1

        for (int i = 90210; i <= 90213; i++) {

            given()
                    .pathParam("ZipCode", i)
                    .when()
                    .get("http://api.zippopotam.us/us/{ZipCode}")
                    .then()
                    .log().body()
                    .body("places", hasSize(1)); // checks the size of the array in the body
        }

    }

    @Test
    void queryParamTest() { // If the parameter is separated by ? it is called query parameter
        given()
                .param("page", 3) // https://gorest.co.in/public/v1/users?page=3
                .pathParam("APIName", "users")
                .log().uri()
                .when()
                .get("https://gorest.co.in/public/v1/{APIName}")
                .then()
                .log().body()
                .statusCode(200)
                .body("meta.pagination.page", equalTo(3));
    }

    @Test
    void queryParamTest1() {
        // send the same request for the pages between 1-10 and check if
        // the page number we send from request and page number we get from response are the same
        for (int i = 1; i <= 10; i++) {
            given()
                    .param("page", i)
                    .pathParam("APIName", "users")
                    .log().uri()
                    .when()
                    .get("https://gorest.co.in/public/v1/{APIName}")
                    .then()
                    .log().body()
                    .statusCode(200)
                    .body("meta.pagination.page", equalTo(i));
        }
    }
    @Test(dataProvider = "pageNumbers")
    void queryParamTestWithDataProvider(int page) {
        // send the same request for the pages between 1-10 and check if
        // the page number we send from request and page number we get from response are the same

        given()
                .param("page", page)
                .pathParam("APIName", "users")
                .log().uri()
                .when()
                .get("https://gorest.co.in/public/v1/{APIName}")
                .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("meta.pagination.page", equalTo(page));

    }

    @DataProvider
    public Object[][] pageNumbers() {

        Object[][] pageNumberList = {{1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}};

        return pageNumberList;
    }

    RequestSpecification requestSpecification;
    ResponseSpecification responseSpecification;

    @BeforeClass
    public void setUp() {
        baseURI = "https://gorest.co.in/public/v1"; // if the request url in the request method doesn't have http part
        // rest assured puts baseURI to the beginning of the url in the request method


        requestSpecification = new RequestSpecBuilder()
                .log(LogDetail.URI)
                .setContentType(ContentType.JSON)
                .addPathParam("APIName", "users")
                .addParam("page", 2)
                .build();

        responseSpecification = new ResponseSpecBuilder()
                .log(LogDetail.BODY)
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .build();

    }

    @Test
    void baseURITest() {
        given()
                .param("page", 3) // https://gorest.co.in/public/v1/users?page=3
                .pathParam("APIName", "users")
                .log().uri()
                .when()
                .get("/{APIName}")
                .then()
                .log().body()
                .statusCode(200)
                .body("meta.pagination.page", equalTo(3));
    }

    @Test
    void requestAndResponseSpecTest() {
        given()
                .spec(requestSpecification)
                .when()
                .get("/{APIName}")
                .then()
                .body("meta.pagination.page", equalTo(3))
                .spec(responseSpecification);
    }

    // Extract Data from JSON object

    @Test
    void extractStringData() {

        String placeName = given()
                .pathParam("Country", "us")
                .pathParam("ZipCode", "90210")
                .when()
                .get("http://api.zippopotam.us/{Country}/{ZipCode}")
                .then()
                .statusCode(200)
                .extract().path("places[0].'place name'"); // with extract method our request returns a value(not Objects)
        // extract returns only one part of the response(the part that we specify in path method)
        // we can assign it to a variable and use it however we want

        System.out.println("placeName = " + placeName);
    }

    @Test
    void extractIntData() {
        int limit = given()
                .spec(requestSpecification)
                .when()
                .get("/{APIName}")// https://gorest.co.in/public/v1/users?page=2
                .then()
                .spec(responseSpecification)
                .extract().path("meta.pagination.limit");
        // We are not allowed to assign an int to a String(cannot assign a type to another type)

        System.out.println("limit = " + limit);
    }

    @Test
    void extractListData() {

        List<Integer> idList = given()
                .spec(requestSpecification)
                .when()
                .get("/{APIName}")// https://gorest.co.in/public/v1/users?page=2
                .then()
                .spec(responseSpecification)
                .extract().path("data.id");

        System.out.println("idList.size() = " + idList.size());
        System.out.println("idList.get(1) = " + idList.get(1));
        Assert.assertTrue(idList.contains(5143698));
    }


    // send get request to https://gorest.co.in/public/v1/users.
    // extract all names from data to a list
    @Test
    void extractListData1(){
        List<String> nameList = given()
                .pathParam("APIName","users")
                .when()
                .get("/{APIName}")
                .then()
                .spec(responseSpecification)
                .extract().path("data.name");

        System.out.println("nameList.size() = " + nameList.size());
        System.out.println("nameList.get(5) = " + nameList.get(5));
    }

    @Test
    void extractResponse(){
        Response response = given()
                .pathParam("APIName","users")
                .when()
                .get("/{APIName}")
                .then()
                .spec(responseSpecification)
                .extract().response(); // return the entire response and assigns it to a Response object.
        // By using this object we are able to reach any part of the response

        int limit  = response.path("meta.pagination.limit");
        System.out.println("limit = " + limit);

        String current = response.path("meta.pagination.links.current");
        System.out.println("current = " + current);

        List<Integer> idList = response.path("data.id");
        System.out.println("idList.size() = " + idList.size());

        List<String> nameList = response.path("data.name" );

        Assert.assertTrue(nameList.contains("Tejas Devar CPA"));
    }

    // POJO (Plain Old Java Object)
    @Test
    void extractJsonPOJO(){
        Location location = given()
                .pathParam("ZipCode",90210)
                .when()
                .get("http://api.zippopotam.us/us/{ZipCode}")
                .then()
                .log().body()
                .extract().as(Location.class); // This request extracts the entire response
        // and assigns it to Location class as a Location object
        // We cannot extract the body partially (e.g. cannot extract place object separately)

        System.out.println("location.getPostCode() = " + location.getPostCode());
        System.out.println("location.getCountry() = " + location.getCountry());
        System.out.println("location.getCountryAbbreviation() = " + location.getCountryAbbreviation());
        System.out.println("location.getPlaces().get(0).getPlaceName() = " + location.getPlaces().get(0).getPlaceName());
        System.out.println("location.getPlaces().get(0).getLongitude() = " + location.getPlaces().get(0).getLongitude());
        System.out.println("location.getPlaces().get(0).getState() = " + location.getPlaces().get(0).getState());
        System.out.println("location.getPlaces().get(0).getStateAbbreviation() = " + location.getPlaces().get(0).getStateAbbreviation());
        System.out.println("location.getPlaces().get(0).getLatitude() = " + location.getPlaces().get(0).getLatitude());


        // public class Location{                   public class Place{
        //      String post code;                            String place name;
        //      String country;                              String longitude;
        //      String country abbreviation;                 String state;
        //      List<Place> places;                          String state abbreviation;
        //                                                   String latitude;
        // }                                           }



    }
}
