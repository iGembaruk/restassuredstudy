package tests.api.mainlaunchertests;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tests.api.utils.dto.generatoruser.*;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class UserTests {

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://fakestoreapi.com";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    public void getAllUsersTest() {
        given()
                .get("https://fakestoreapi.com/users")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    public void getSingleUserTheBodyTest() {
        int idUser = 1;

        given().pathParam("id", idUser)
                .get("https://fakestoreapi.com/users/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", equalTo(idUser))
                .body("address.zipcode", Matchers.matchesPattern("\\d{5}-\\d{4}"));
    }

    @Test
    public void getSingleUserTheClassesTest() {
        int idUser = 1;

        UserRoot user = given().pathParam("id", idUser)
                .get("/users/{id}")
                .then()
                .statusCode(200)
                .extract().as(UserRoot.class);

        Assertions.assertEquals(user.getId(), idUser);
        Assertions.assertTrue(user.getAddressUser().getZipcode().matches("\\d{5}-\\d{4}"));
    }

    @Test
    public void getLimitResultsUserTheBodyTest() {
        int limitResults = 3;

        given().queryParam("limit", limitResults)
                .get("https://fakestoreapi.com/users")
                .then()
                .log().all()
                .statusCode(200)
                .body("", hasSize(limitResults));
    }

    @Test
    public void getLimitResultsUserTheClassesTest() {
        int limitResults = 6;

        List<UserRoot> users = given().queryParam("limit", limitResults)
                .get("/users")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", UserRoot.class);
        Assertions.assertEquals(users.size(), limitResults);
    }

    @Test
    public void getSortResultsUserIdTheBodyTest() {
        String sort = "desc";

        Response responseDesc = given().queryParam("sort", sort)
                .get("https://fakestoreapi.com/users")
                .then()
                .log().all()
                .statusCode(200)
                .extract().response();

        Response responseSort = given()
                .get("https://fakestoreapi.com/users")
                .then()
                .log().all()
                .statusCode(200)
                .extract().response();

        List<Integer> listSortedIdDesc = responseDesc.jsonPath().getList("id");
        List<Integer> listSortedIdSort = responseSort.jsonPath().getList("id");
        Assertions.assertNotEquals(listSortedIdSort, listSortedIdDesc);

        List<Integer> listSortedIdSortReverse = listSortedIdSort.stream().sorted(Comparator.reverseOrder()).toList();
        Assertions.assertEquals(listSortedIdDesc, listSortedIdSortReverse);
    }

    @Test
    public void getSortResultsUserIdTheClassesTest() {
        String sort = "desc";

        List<UserRoot> usersSortedDesc = given().queryParam("sort", sort)
                .get("/users")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", UserRoot.class);

        List<UserRoot> usersSorted = given()
                .get("/users")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", UserRoot.class);

        List<Integer> listSortedIdDesc = usersSortedDesc.stream()
                .map(x -> x.getId())
                .toList();
        List<Integer> listSortedIdSort = usersSorted.stream()
                .map(UserRoot::getId)
                .toList();
        Assertions.assertNotEquals(listSortedIdSort, listSortedIdDesc);

        List<Integer> listSortedIdSortReverse = listSortedIdSort.stream().sorted(Comparator.reverseOrder()).toList();
        Assertions.assertEquals(listSortedIdDesc, listSortedIdSortReverse);
    }

    private UserRoot getUserRootClasses() {
        GeolocationUser geolocationUser = new GeolocationUser("-21.2222", "51.2211");
        AddressUser addressUser = AddressUser.builder()
                .zipcode("12926-3871")
                .number(2222)
                .city("Penza")
                .street("Lunacharskogo")
                .geolocationUser(geolocationUser).build();
        NameUser nameUser = new NameUser("Igor", "Gembaruk");

        return UserRoot.builder()
                .password("a1111")
                .addressUser(addressUser)
                .phone("89656363968")
                .nameUser(nameUser)
                .id(1000)
                .email("i.gembaruk@mail.ru")
                .username("gembaruk").build();
    }

    @Test
    public void addUserTheBodyTest() {
        GeolocationUser geolocationUser = new GeolocationUser("-21.2222", "51.2211");
        AddressUser addressUser = AddressUser.builder()
                .zipcode("12926-3871")
                .number(2222)
                .city("Penza")
                .street("Lunacharskogo")
                .geolocationUser(geolocationUser).build();
        NameUser nameUser = new NameUser("Igor", "Gembaruk");

        UserRoot userRoot = UserRoot.builder()
                .password("a1111")
                .addressUser(addressUser)
                .phone("89656363968")
                .nameUser(nameUser)
                .id(1000)
                .email("i.gembaruk@mail.ru")
                .username("gembaruk").build();

        given().body(userRoot)
                .post("https://fakestoreapi.com/users")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", notNullValue())
                .body("id", not(empty()));
    }

    @Test
    public void addUserTheClassesTest() {
        UserRoot userRoot = getUserRootClasses();

        int idUser = given().body(userRoot)
                .post("/users")
                .then()
                .statusCode(200)
                .extract().jsonPath().getInt("id");

        Assertions.assertNotNull(idUser);
        Assertions.assertTrue(idUser > 0);
    }

    @Test
    public void updateUserPasswordTheBodyTest() {
        UserRoot userRoot = getUserRootClasses();
        String oldPassword = userRoot.getPassword();
        userRoot.setPassword("alakalakakll");
        int idUser = 1;

        given().pathParam("id", idUser)
                .body(userRoot)
                .put("https://fakestoreapi.com/users/{id}")
                .then()
                .log().all()
                .statusCode(200);
        Assertions.assertNotEquals(userRoot.getPassword(), oldPassword);
    }


    @Test
    public void deleteUserTest() {
        int idUser = 1;
        given().pathParam("id", idUser)
                .delete("https://fakestoreapi.com/users/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", equalTo(idUser));
    }

    @Test
    public void autorizationLoginPasswordTest() {
        Map<String, String> user = new HashMap<>();
        user.put("username", "mor_2314");
        user.put("password", "83r5^_");

        given().contentType(ContentType.JSON)
                .body(user)
                .post("https://fakestoreapi.com/auth/login")
                .then()
                .log().all()
                .statusCode(200)
                .body("token", notNullValue())
                .body("token", not(empty()));
    }

    @Test
    public void autorizationLoginPasswordTheClassesTest() {
        AutorizationUser autorizationUser = new AutorizationUser("mor_2314", "83r5^_");

        given().contentType(ContentType.JSON)
                .body(autorizationUser)
                .post("https://fakestoreapi.com/auth/login")
                .then()
                .log().all()
                .statusCode(200)
                .body("token", notNullValue())
                .body("token", not(empty()));
    }
}
