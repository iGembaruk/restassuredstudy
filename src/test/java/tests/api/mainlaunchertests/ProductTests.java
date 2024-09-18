package tests.api.mainlaunchertests;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import tests.api.utils.dto.generatorproducts.ProductRoot;
import tests.api.utils.dto.generatorproducts.RatingProduct;

import java.util.Comparator;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ProductTests {

    @BeforeAll
    public static void setUp() {
        RestAssured.baseURI = "https://fakestoreapi.com";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
    }

    @Test
    public void getAllProductsTest() {
        given().get("https://fakestoreapi.com/products")
                .then()
                .log().all()
                .statusCode(200);
    }

    @Test
    public void getSingleProductTheClassesTest() {
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
    public void getSingleProductTheBodyTest() {
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
    public void getLimitResultsTheClassesTest() {
        int limit = 5;

        List<ProductRoot> listProducts = given().queryParam("limit", limit)
                .get("/products")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", ProductRoot.class);

        Assertions.assertEquals(listProducts.size(), limit);
    }

    @Test
    public void getLimitResultsTheBodyTest() {
        int limit = 5;

        given().queryParam("limit", limit)
                .get("https://fakestoreapi.com/products")
                .then()
                .log().all()
                .statusCode(200)
                .body("", hasSize(limit));
    }

    @Test
    public void getSortResultsTheClassesTest() {
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
                .map(x -> x.getId())
                .toList();
        List<Integer> sortedDescList = listProductsDesc.stream()
                .map(x -> x.getId())
                .toList();
        Assertions.assertNotEquals(sortedList, sortedDescList);

        List<Integer> reverseSortedList = sortedList.stream().sorted(Comparator.reverseOrder()).toList();
        Assertions.assertEquals(reverseSortedList, sortedDescList);
    }

    @Test
    public void getSortResultsTheBodyTest() {
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
    public void getAllCategoriesTest() {
        given()
                .get("/products/categories")
                .then()
                .statusCode(200);
    }

    @Test
    public void getProductsSpecificCategoryTheBodyTest() {
        String name = "jewelery";

        List<ProductRoot> listSpecificCategory = given().pathParam("category", name)
                .get("/products/category/{category}")
                .then()
                .statusCode(200)
                .extract().jsonPath().getList("", ProductRoot.class);

        if (!listSpecificCategory.isEmpty()) {
            Assertions.assertTrue(listSpecificCategory.stream().allMatch(category -> category.getCategory().equals(name)));
        }
    }

    @Test
    public void getProductsSpecificCategoryTheClassesTest() {
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
    public void addNewProductTest() {
        RatingProduct ratingProduct = new RatingProduct(4.1, 190);
        ProductRoot productRoot = ProductRoot.builder()
                .image("https://fakestoreapi.com/img/81fPKd-2AYL._AC_SL1500_.jpg")
                .price(116.0)
                .ratingProduct(ratingProduct)
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

    private ProductRoot getProductRoot() {
        RatingProduct ratingProduct = new RatingProduct(4.1, 190);

        return ProductRoot.builder()
                .image("/img/81fPKd-2AYL._AC_SL1500_.jpg")
                .price(116.0)
                .ratingProduct(ratingProduct)
                .id(19)
                .description("great outerwear jackets for Spring/Autumn/Winter, suitable for many occasions, such as working, hiking, camping, mountain/rock climbing, cycling, traveling or other outdoors. Good gift choice for you or your family member. A warm hearted love to Father, husband or son in this thanksgiving or Christmas Day")
                .title("Mens Cotton Jacket")
                .category("fantasy").build();
    }

    @Test
    public void updateProductTest() {
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
    public void deleteProductIdTest() {
        int idProduct = 3;

        given().pathParam("id", idProduct)
                .delete("/products/{id}")
                .then()
                .statusCode(200);
    }
}
