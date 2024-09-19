package tests.api.mainlaunchertests;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tests.api.utils.dto.generatorcart.CartRoot;
import tests.api.utils.dto.generatorcart.CartInfo;
import tests.api.utils.wrappers.ParseDate;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.equalTo;

public class CartTests {

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://fakestoreapi.com";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    public void getAllCartsTest() {
        given()
                .get("/carts")
                .then()
                .statusCode(200);
    }

    @Test
    public void getSingleCartTheBodyTest() {
        int idCart = 5;
        given().pathParam("id", idCart)
                .get("/carts/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(idCart));
    }

    @Test
    public void getSingleCartTheClassesTest() {
        int idCart = 5;
        CartRoot cartRoot = given().pathParam("id", idCart)
                .get("/carts/{id}")
                .then()
                .statusCode(200)
                .extract().as(CartRoot.class);

        Assertions.assertEquals(cartRoot.getId(), idCart);
    }

    @Test
    public void getLimitResultsCartTheBodyTest() {
        int limit = 5;

        given().queryParam("limit", limit)
                .get("/carts")
                .then()
                .statusCode(200)
                .body("", hasSize(limit));
    }

    @Test
    public void getLimitResultsCartTheClassesTest() {
        int limit = 5;

        List<CartRoot> listCartLimit = given().queryParam("limit", limit)
                .get("/carts")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", CartRoot.class);

        Assertions.assertEquals(limit, listCartLimit.size());
    }

    @Test
    public void getSortResultsCartTheBodyTest() {
        String sorted = "desc";

        Response descResponse = given().queryParam("sort", sorted)
                .get("/carts")
                .then()
                .statusCode(200)
                .extract().response();

        Response sortResponse = given()
                .get("https://fakestoreapi.com/carts")
                .then()
                .log().all()
                .statusCode(200)
                .extract().response();

        List<Integer> listDescId = descResponse.jsonPath().getList("id");
        List<Integer> listSortedId = sortResponse.jsonPath().getList("id");
        Assertions.assertNotEquals(listDescId, listSortedId);

        List<Integer> listReverseSorted = listSortedId.stream().sorted(Comparator.reverseOrder()).toList();
        Assertions.assertEquals(listDescId, listReverseSorted);
    }

    @Test
    public void getSortResultsCartTheClassesTest() {
        String sorted = "desc";

        List<CartRoot> listDesc = given().queryParam("sort", sorted)
                .get("/carts")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", CartRoot.class);

        List<CartRoot> listNoSort = given()
                .get("/carts")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", CartRoot.class);

        List<Integer> listDescId = listDesc
                .stream()
                .map(x -> x.getId())
                .toList();
        List<Integer> listSortedId = listNoSort.stream()
                .map(x -> x.getId())
                .toList();
        Assertions.assertNotEquals(listDescId, listSortedId);

        List<Integer> listReverseSorted = listSortedId.stream().sorted(Comparator.reverseOrder()).toList();
        Assertions.assertEquals(listDescId, listReverseSorted);
    }

    @Test
    public void getCartsInDateRangeTest() throws ParseException {
        String startDateStr = "2019-12-10";
        String endDateStr = "2020-10-10";

        Date dateStart = ParseDate.parseDateFromStringToDate(startDateStr, "yyyy-MM-dd");
        Date dateEnd = ParseDate.parseDateFromStringToDate(endDateStr, "yyyy-MM-dd");

        List<CartRoot> listCartBetweenDate = given().queryParam("startdate", startDateStr)
                .queryParam("enddate", endDateStr)
                .get("/carts")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", CartRoot.class);

        List<String> responseEditingDateFormatYYYYMMDD = listCartBetweenDate.stream()
                .map(object -> object.getDate().substring(0, 10))
                .toList();
        List<Date> listDateResponse = ParseDate.parseListDateFromStringToDate(responseEditingDateFormatYYYYMMDD, "yyyy-MM-dd");

        Assertions.assertTrue(listCartBetweenDate.stream().allMatch(object -> object.getId() > 0));
        Assertions.assertTrue(listDateResponse.stream().allMatch(object ->
                (object.equals(dateStart) || object.after(dateStart)) && (object.equals(dateEnd) || object.before(dateEnd))));
    }

    @Test
    public void getUserCartsTest() {
        int idUser = 2;

        List<CartRoot> listCart = given().pathParam("id", idUser)
                .get("/carts/user/{id}")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", CartRoot.class);

        boolean allMatchUserId = listCart.stream().allMatch(cart -> cart.getUserId() == idUser);
        Assertions.assertTrue(allMatchUserId);
    }

    @Test
    public void addNewCartTest() {
        CartRoot cartRoot = getObjectClassCart();

        CartRoot cart = given().body(cartRoot)
                .post("/carts")
                .then()
                .statusCode(200)
                .extract().as(CartRoot.class);

        Assertions.assertTrue(cart.getId() != 0);
    }

    private CartRoot getObjectClassCart() {
        CartInfo cartInfo = new CartInfo(6, 15);
        CartInfo cartInfo2 = new CartInfo(1, 1);
        List<CartInfo> list = new ArrayList<>();
        list.add(cartInfo);
        list.add(cartInfo2);

        return CartRoot.builder()
                .date("2024-09-10")
                .userId(4)
                .id(9)
                .products(list)
                .build();
    }

    @Test
    public void updateCartTest() {
        CartRoot cartRoot = getObjectClassCart();

        int countQualityCart = cartRoot.getProducts().size();
        CartInfo cartInfo3 = new CartInfo(4, 1);
        cartRoot.getProducts().add(cartInfo3);

        int idProduct = cartRoot.getId();

        CartRoot cart = given().pathParam("id", idProduct)
                .body(cartRoot)
                .put("/carts/{id}")
                .then()
                .statusCode(200)
                .extract().as(CartRoot.class);

        Assertions.assertEquals(idProduct, cart.getId());
        Assertions.assertTrue(countQualityCart != cartRoot.getProducts().size());
    }

    @Test
    public void deleteCartIdTest() {
        int idCart = 1;

        given().pathParam("id", idCart)
                .delete("/carts/{id}")
                .then()
                .statusCode(200);
    }
}
