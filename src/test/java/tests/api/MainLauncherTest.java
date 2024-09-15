package tests.api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tests.api.generatorcart.CartGeneratorRoot;
import tests.api.generatorcart.CartInfoGenerator;
import tests.api.generatorproducts.ProductRoot;
import tests.api.generatorproducts.Rating;
import tests.api.generatoruser.Address;
import tests.api.generatoruser.Geolocation;
import tests.api.generatoruser.Name;
import tests.api.generatoruser.UserRoot;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class MainLauncherTest {

    @Test
    public void getAllProductsTest(){
        given().get("https://fakestoreapi.com/products")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    public void getSingleProductTest(){
        int idProduct = 2;

        given().pathParam("id", idProduct)
                .get("https://fakestoreapi.com/products/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", notNullValue())
                .body("id", not(empty()));
    }

    @Test
    public void getLimitResultsTest(){
        int limit = 5;

        given().queryParam("limit", limit)
                .get("https://fakestoreapi.com/products")
                .then()
                .log().all()
                .statusCode(200)
                .body("", hasSize(limit));
    }

    @Test
    public void getSortResultsTest(){
        String sorted = "desc";

        Response responseDescSorted = given().queryParam("sort", sorted)
                .get("https://fakestoreapi.com/products")
                .then()
                .log().all()
                .statusCode(200)
                .extract().response();

        Response responseSorted = given()
                .get("https://fakestoreapi.com/products")
                .then()
                .log().all()
                .statusCode(200)
                .extract().response();

        List<Integer> sortedList = responseSorted.jsonPath().getList("id");
        List<Integer> sortedDescList = responseDescSorted.jsonPath().getList("id");
        Assertions.assertNotEquals(sortedDescList, sortedList);

        List<Integer> reverseSortedList = sortedList.stream().sorted(Comparator.reverseOrder()).toList();
        Assertions.assertEquals(reverseSortedList, sortedDescList);
    }

    @Test
    public void getAllCategoriesTest(){
        given()
                .get("https://fakestoreapi.com/products/categories")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    public void getProductsSpecificCategoryTest(){
        String name = "jewelery";
        Integer assertZero = 0;

        given().pathParam("category", name)
                .get("https://fakestoreapi.com/products/category/{category}")
                .then()
                .log().all()
                .statusCode(200)
                .body("", hasSize(not(equalTo(assertZero))));
    }

    @Test
    public void addNewProductTest(){
        Rating rating = new Rating(4.1, 190);
        ProductRoot productRoot = ProductRoot.builder()
                .image("https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg")
                .price(116.0)
                .rating(rating)
                .description("great outerwear jackets for Spring/Autumn/Winter, suitable for many occasions, such as working, hiking, camping, mountain/rock climbing, cycling, traveling or other outdoors. Good gift choice for you or your family member. A warm hearted love to Father, husband or son in this thanksgiving or Christmas Day")
                .title("Mens Cotton Jacket")
                .category("fantasy").build();

        given().body(productRoot)
                .post("https://fakestoreapi.com/products")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", notNullValue());
    }

    private ProductRoot getProductRoot(){
        Rating rating = new Rating(4.1, 190);

        return ProductRoot.builder()
                .image("https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg")
                .price(116.0)
                .rating(rating)
                .id(19)
                .description("great outerwear jackets for Spring/Autumn/Winter, suitable for many occasions, such as working, hiking, camping, mountain/rock climbing, cycling, traveling or other outdoors. Good gift choice for you or your family member. A warm hearted love to Father, husband or son in this thanksgiving or Christmas Day")
                .title("Mens Cotton Jacket")
                .category("fantasy").build();
    }

    @Test
    public void updateProductTest(){
        ProductRoot product = getProductRoot();
        String oldCategory = product.getCategory();
        product.setCategory("Comedy");

        ProductRoot response = given().pathParam("id", product.getId())
                .body(product)
                .put("https://fakestoreapi.com/products/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", equalTo(product.getId()))
                .extract().as(ProductRoot.class);

        Assertions.assertNotEquals(response.getCategory(), oldCategory);
    }

    @Test
    public void deleteProductIdTest(){
        int idProduct = 3;

        given().pathParam("id", idProduct)
                .delete("https://fakestoreapi.com/products/{id}")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    public void getAllCartsTest(){
        given()
                .get("https://fakestoreapi.com/carts")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    public void getSingleCartTest(){
        int idCart = 5;
        given().pathParam("id", idCart)
                .get("https://fakestoreapi.com/carts/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", equalTo(idCart));
    }

    @Test
    public void getLimitResultsCartTest(){
        int limit = 5;

        given().queryParam("limit", limit)
                .get("https://fakestoreapi.com/carts")
                .then()
                .log().all()
                .statusCode(200)
                .body("", hasSize(limit));
    }

    @Test
    public void getSortResultsCartTest(){
        String sorted = "desc";

        Response descResponse = given().queryParam("sort", sorted)
                .get("https://fakestoreapi.com/carts")
                .then()
                .log().all()
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
    public void getCartsInDateRangeTest(){
        String startDate = "2019-12-10";
        String endDate = "2020-10-10";

        given().queryParam("startdate", startDate)
                .queryParam("enddate", endDate)
                .get("https://fakestoreapi.com/carts")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", notNullValue())
                .body("id", not(empty()));
    }

    @Test
    public void getUserCartsTest(){
        int idUser = 2;

        given().pathParam("id", idUser)
                .get("https://fakestoreapi.com/carts/user/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("userId[0]", equalTo(idUser));
    }

    @Test
    public void addNewCartTest(){
        CartInfoGenerator cartInfoGenerator = new CartInfoGenerator(6, 15);
        CartInfoGenerator cartInfoGenerator2 = new CartInfoGenerator(1, 1);
        List<CartInfoGenerator> list = new ArrayList<>();
        list.add(cartInfoGenerator);
        list.add(cartInfoGenerator2);
        CartGeneratorRoot cartGeneratorRoot = CartGeneratorRoot.builder()
                .date("2024-09-10")
                .userId(4)
                .id(9)
                .products(list)
                .build();

        given().body(cartGeneratorRoot)
                .post("https://fakestoreapi.com/carts")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", notNullValue());
    }

    private CartGeneratorRoot getObjectClassCart(){
        CartInfoGenerator cartInfoGenerator = new CartInfoGenerator(6, 15);
        CartInfoGenerator cartInfoGenerator2 = new CartInfoGenerator(1, 1);
        List<CartInfoGenerator> list = new ArrayList<>();
        list.add(cartInfoGenerator);
        list.add(cartInfoGenerator2);

        return CartGeneratorRoot.builder()
                .date("2024-09-10")
                .userId(4)
                .id(9)
                .products(list)
                .build();
    }

    @Test
    public void updateCartTest(){
        CartGeneratorRoot getCartGeneratorCartRoot = getObjectClassCart();
        int countQualityCart = getCartGeneratorCartRoot.getProducts().size();
        CartInfoGenerator cartInfoGenerator3 = new CartInfoGenerator(4, 1);
        getCartGeneratorCartRoot.getProducts().add(cartInfoGenerator3);

        int idProduct = getCartGeneratorCartRoot.getId();
        given().pathParam("id", idProduct)
                .body(getCartGeneratorCartRoot)
                .put("https://fakestoreapi.com/carts/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", equalTo(idProduct));
        Assertions.assertTrue(countQualityCart != getCartGeneratorCartRoot.getProducts().size() );
    }

    @Test
    public void deleteCartIdTest(){
        int idCart = 1;

        given().pathParam("id", idCart)
                .delete("https://fakestoreapi.com/carts/{id}")
                .then()
                .log().all()
                .statusCode(200);
    }

    // User //

    @Test
    public void getAllUsersTest(){
        given()
                .get("https://fakestoreapi.com/users")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    public void getSingleUserTest(){
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
    public void getLimitResultsUserTest(){
       int limitResults = 3;

       given().queryParam("limit", limitResults)
               .get("https://fakestoreapi.com/users")
               .then()
               .log().all()
               .statusCode(200)
               .body("", hasSize(limitResults));
    }

    @Test
    public void getSortResultsUserIdTest(){
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
        Assertions.assertNotEquals(listSortedIdSort,listSortedIdDesc);

        List<Integer> listSortedIdSortReverse = listSortedIdSort.stream().sorted(Comparator.reverseOrder()).toList();
        Assertions.assertEquals(listSortedIdDesc, listSortedIdSortReverse);
    }

    @Test
    public void addUserTest(){
        Geolocation geolocation = new Geolocation("-21.2222", "51.2211");
        Address address = Address.builder()
                .zipcode("12926-3871")
                .number(2222)
                .city("Penza")
                .street("Lunacharskogo")
                .geolocation(geolocation).build();
        Name name = new Name("Igor", "Gembaruk");

        UserRoot userRoot = UserRoot.builder()
                .password("a1111")
                .address(address)
                .phone("89656363968")
                .name(name)
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

    private UserRoot getUserRootClasses(){
        Geolocation geolocation = new Geolocation("-21.2222", "51.2211");
        Address address = Address.builder()
                .zipcode("12926-3871")
                .number(2222)
                .city("Penza")
                .street("Lunacharskogo")
                .geolocation(geolocation).build();
        Name name = new Name("Igor", "Gembaruk");

        return UserRoot.builder()
                .password("a1111")
                .address(address)
                .phone("89656363968")
                .name(name)
                .id(1000)
                .email("i.gembaruk@mail.ru")
                .username("gembaruk").build();
    }

    @Test
    public void updateUserPasswordTest(){
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
    public void deleteUserTest(){
        int idUser = 1;
        given().pathParam("id", idUser)
                .delete("https://fakestoreapi.com/users/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", equalTo(idUser));
    }

    @Test
    public void autorizationLoginPasswordTest(){
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
}
