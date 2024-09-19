package tests.api;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tests.api.generatorcart.CartGeneratorRoot;
import tests.api.generatorcart.CartInfoGenerator;
import tests.api.generatorproducts.ProductRoot;
import tests.api.generatorproducts.Rating;
import tests.api.generatoruser.*;
import tests.api.wrappers.WrappersDate;

import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class RefactoringMainLauncherInModulClassesTests {

    @BeforeAll
    public static void setUp(){
        RestAssured.baseURI = "https://fakestoreapi.com";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    // Products //

    @Test
    public void getAllProductsTest(){
        given().get("/products")
                .then()
                .statusCode(200);
    }

    @Test
    public void getSingleProductTest(){
        int idProduct = 2;

        ProductRoot productRoot = given().pathParam("id", idProduct)
                .get("/products/{id}")
                .then()
                .statusCode(200)
                .extract().as(ProductRoot.class);

        Assertions.assertNotNull(productRoot.getId());
        Assertions.assertTrue(productRoot.getId() > 0);
        Assertions.assertEquals(idProduct, productRoot.getId());
    }

    @Test
    public void getLimitResultsTest(){
        int limit = 5;

        List<ProductRoot> listProducts = given().queryParam("limit", limit)
                .get("/products")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", ProductRoot.class);

        Assertions.assertEquals(listProducts.size(), limit);
    }

    @Test
    public void getSortResultsTest(){
        String sorted = "desc";

        List<ProductRoot> listProductsDesc = given().queryParam("sort", sorted)
                .get("/products")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", ProductRoot.class);

        List<ProductRoot> listProductsNoDesc = given()
                .get("/products")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", ProductRoot.class);


        List<Integer> sortedList = listProductsNoDesc.stream()
                .map(x->x.getId())
                .toList();
        List<Integer> sortedDescList = listProductsDesc.stream()
                        .map(x -> x.getId())
                        .toList();
        Assertions.assertNotEquals(sortedList, sortedDescList);

        List<Integer> reverseSortedList = sortedList.stream().sorted(Comparator.reverseOrder()).toList();
        Assertions.assertEquals(reverseSortedList, sortedDescList);
    }

    @Test
    public void getAllCategoriesTest(){
        given()
                .get("/products/categories")
                .then()
                .statusCode(200);
    }

    @Test
    public void getProductsSpecificCategoryTest(){
        String name = "jewelery";

        List<ProductRoot> listSpecificCategory = given().pathParam("category", name)
                .get("/products/category/{category}")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", ProductRoot.class);

        if (!listSpecificCategory.isEmpty()){
        Assertions.assertTrue(listSpecificCategory.stream().allMatch(category -> category.getCategory().equals(name)));
        }
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

        ProductRoot rootResponse = given().body(productRoot)
                .post("/products")
                .then()
                .statusCode(200)
                .extract().as(ProductRoot.class);

        Assertions.assertTrue(rootResponse.getId() > 0);
    }

    private ProductRoot getProductRoot(){
        Rating rating = new Rating(4.1, 190);

        return ProductRoot.builder()
                .image("/img/81fPKd-2AYL._AC_SL1500_.jpg")
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
                .put("/products/{id}")
                .then()
                .statusCode(200)
                .extract().as(ProductRoot.class);

        Assertions.assertNotEquals(response.getCategory(), oldCategory);
        Assertions.assertTrue(response.getId() > 0);
        Assertions.assertEquals(response.getId(), product.getId());
    }

    @Test
    public void deleteProductIdTest(){
        int idProduct = 3;

        given().pathParam("id", idProduct)
                .delete("/products/{id}")
                .then()
                .log().all()
                .statusCode(200);
    }

    //Cart

    @Test
    public void getAllCartsTest(){
        given()
                .get("/carts")
                .then()
                .statusCode(200)
                .body("", hasSize(greaterThan(0)));
    }

    @Test
    public void getSingleCartTest(){
        int idCart = 5;
        CartGeneratorRoot cartGeneratorRoot = given().pathParam("id", idCart)
                .get("/carts/{id}")
                .then()
                .statusCode(200)
                .extract().as(CartGeneratorRoot.class);

        Assertions.assertEquals(cartGeneratorRoot.getId(), idCart);
    }

    @Test
    public void getLimitResultsCartTest(){
        int limit = 5;

        List<CartGeneratorRoot> listCartLimit = given().queryParam("limit", limit)
                .get("/carts")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", CartGeneratorRoot.class);

        Assertions.assertEquals(limit, listCartLimit.size());
    }

    @Test
    public void getSortResultsCartTest(){
        String sorted = "desc";

        List<CartGeneratorRoot> listDesc = given().queryParam("sort", sorted)
                .get("/carts")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", CartGeneratorRoot.class);

        List<CartGeneratorRoot> listNoSort = given()
                .get("/carts")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", CartGeneratorRoot.class);

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
    public void getCartsInDateRangeTest(){
        String startDateStr = "2019-12-10";
        String endDateStr = "2020-10-10";

        Date dateStart = WrappersDate.parseDateFromStringToDate(startDateStr, "yyyy-MM-dd");
        Date dateEnd = WrappersDate.parseDateFromStringToDate(endDateStr, "yyyy-MM-dd");

       List<CartGeneratorRoot> listCartBetweenDate = given().queryParam("startdate", startDateStr)
                .queryParam("enddate", endDateStr)
                .get("/carts")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", CartGeneratorRoot.class);

        List<String> responseEditingDateFormatYYYYMMDD = listCartBetweenDate.stream()
                .map(object -> object.getDate().substring(0, 10))
                        .toList();
        List<Date> listDateResponse = WrappersDate.parseListDateFromStringToDate(responseEditingDateFormatYYYYMMDD, "yyyy-MM-dd");

       Assertions.assertTrue(listCartBetweenDate.stream().allMatch(object -> object.getId() > 0));
       Assertions.assertTrue(listDateResponse.stream().allMatch(object ->
               (object.equals(dateStart) || object.after(dateStart)) && (object.equals(dateEnd) || object.before(dateEnd))));
    }

    @Test
    public void getUserCartsTest(){
        int idUser = 2;

        List<CartGeneratorRoot> listCart = given().pathParam("id", idUser)
                .get("/carts/user/{id}")
                .then()
                .statusCode(200)
                        .extract().jsonPath().getList("", CartGeneratorRoot.class);

        boolean allMatchUserId = listCart.stream().allMatch(cart -> cart.getUserId() == idUser);

        Assertions.assertTrue(allMatchUserId);
// либо       Assertions.assertTrue(listCart.stream().allMatch(x -> x.getUserId() == idUser));
    }

    @Test
    public void addNewCartTest(){//TODO
        CartGeneratorRoot cartGeneratorRoot = getObjectClassCart();

        CartGeneratorRoot cart = given().body(cartGeneratorRoot)
                .post("/carts")
                .then()
                .statusCode(200)
                .extract().as(CartGeneratorRoot.class);

        Assertions.assertTrue(cart.getId() != 0);

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

        CartGeneratorRoot cart = given().pathParam("id", idProduct)
                .body(getCartGeneratorCartRoot)
                .put("https://fakestoreapi.com/carts/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", equalTo(idProduct))
                        .extract().as(CartGeneratorRoot.class);

        Assertions.assertEquals(idProduct, cart.getId());
        Assertions.assertTrue(countQualityCart != getCartGeneratorCartRoot.getProducts().size() );
    }

    @Test
    public void deleteCartIdTest(){
        int idCart = 1;

        given().pathParam("id", idCart)
                .delete("/carts/{id}")
                .then()
                .statusCode(200);
    }

    // User //

    @Test
    public void getAllUsersTest(){
        given()
                .get("/users")
                .then()
                .statusCode(200);
    }

    @Test
    public void getSingleUserTest(){
        int idUser = 1;

        UserRoot user = given().pathParam("id", idUser)
                .get("/users/{id}")
                .then()
                .statusCode(200)
                .extract().as(UserRoot.class);

        Assertions.assertEquals(user.getId(), idUser);
        Assertions.assertTrue(user.getAddress().getZipcode().matches("\\d{5}-\\d{4}"));
    }

    @Test
    public void getLimitResultsUserTest(){
        int limitResults = 6;

       List<UserRoot> users = given().queryParam("limit", limitResults)
                .get("/users")
                .then()
                .statusCode(200)
               .extract().jsonPath().getList("", UserRoot.class);
               //Либо использовать абстрактный класс библиотеки RestAssure, пример ниже:
//                       .extract().as(new TypeRef<List<UserRoot>>() {});
       Assertions.assertEquals(users.size(), limitResults);
    }

    @Test
    public void getSortResultsUserIdTest(){
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
        Assertions.assertNotEquals(listSortedIdSort,listSortedIdDesc);

        List<Integer> listSortedIdSortReverse = listSortedIdSort.stream().sorted(Comparator.reverseOrder()).toList();
        Assertions.assertEquals(listSortedIdDesc, listSortedIdSortReverse);
    }

    @Test
    public void addUserTest(){
        UserRoot userRoot = getUserRootClasses();

        int idUser = given().body(userRoot)
                .post("/users")
                .then()
                .statusCode(200)
                .extract().jsonPath().getInt("id");

        Assertions.assertNotNull(idUser);
        Assertions.assertTrue(idUser > 0);
    }

    private UserRoot getUserRootClasses(){
        Random random = new Random();
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
                .id(random.nextInt(100))
                .email("i.gembaruk@mail.ru")
                .username("gembaruk").build();
    }

    @Test
    public void updateUserPasswordTest(){
        UserRoot userRoot = getUserRootClasses();
        String oldPassword = userRoot.getPassword();
        userRoot.setPassword("alakalakakll");

        UserRoot updatedUser = given().pathParam("id", userRoot.getId())
                .body(userRoot)
                .put("/users/{id}")
                .then()
                .statusCode(200)
                .extract().as(UserRoot.class);

        Assertions.assertNotEquals(oldPassword, updatedUser.getPassword());
    }

    @Test
    public void deleteUserTest(){
        int idUser = 1;
        given().pathParam("id", idUser)
                .delete("/users/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(idUser));
    }

    @Test
    public void autorizationLoginPasswordTest(){
        AutorizationUser autorizationUser = new AutorizationUser("mor_2314", "83r5^_" );

        String token = given().contentType(ContentType.JSON)
                .body(autorizationUser)
                .post("/auth/login")
                .then()
                .statusCode(200)
                .extract().jsonPath().getString("token");

        Assertions.assertNotNull(token);
    }
}
