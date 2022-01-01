package spring_forum.controllers.posts;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import spring_forum.dtos.PostDTO;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

@ActiveProfiles(value = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PostValidTests {

    private static final String DEFAULT_URL = "http://localhost:8080/posts";

    @Test
    public void basicStatusCodeTest() {
        given().when().get(DEFAULT_URL).then().statusCode(200);
    }

    @Test
    public void showAllPostsValidTest() {
        given()
                .when().get(DEFAULT_URL).then()
                .body(containsString("First post"))
                .body(containsString("It is my first post"))
                .body(containsString("Second post"))
                .body(containsString("This is the second post!"));
    }

    @Test
    public void showAllPostsForUserValidTest() {
        given()
                .when().get(DEFAULT_URL + "/user/1").then()
                .body(containsString("First post"))
                .body(containsString("It is my first post!"))
                .statusCode(200);
    }

    @Test
    public void showAllPostsWithTagValidTest() {
        given()
                .when().get(DEFAULT_URL + "/tag/firstTag").then()
                .body(containsString("First post"))
                .body(containsString("It is my first post!"))
                .statusCode(200);
    }

    @Test
    public void findByIdValidTest() {
        given()
                .when().get(DEFAULT_URL + "/1").then()
                .body("title", equalTo("First post"))
                .body("text", equalTo("It is my first post!"))
                .statusCode(200);
    }

    @Test
    public void findByTitleValidTest() {
        given()
                .when().get(DEFAULT_URL + "/title/First post").then()
                .body("title", equalTo("First post"))
                .body("text", equalTo("It is my first post!"))
                .statusCode(200);
    }

    @Test
    public void saveAndDeletePostValidTest() {
        PostDTO post = PostDTO.builder().title("New Post!").text("This is New Post!")
                .postOwnerID(1L).build();
        Integer id = given()
                .contentType(ContentType.JSON)
                .body(post)
                .when().post(DEFAULT_URL).then()
                .statusCode(200)
                .extract().path("id");

        given()
                .pathParam("postID", id)
                .when().delete(DEFAULT_URL + "/{postID}").then()
                .body(equalTo("Post with ID = " + id + " was deleted."))
                .statusCode(200);
    }

    @Test
    public void updatePostValidTest() {
        PostDTO post = PostDTO.builder().id(1L).title("First posting").text("It is my first post!")
                .postOwnerID(1L).build();
        given()
                .contentType(ContentType.JSON)
                .body(post)
                .when().put(DEFAULT_URL).then()
                .body("title", equalTo("First posting"))
                .statusCode(200);
        post.setTitle("First post");
        given()
                .contentType(ContentType.JSON)
                .body(post)
                .when().put(DEFAULT_URL).then()
                .body("title", equalTo("First post"))
                .statusCode(200);
    }
}
