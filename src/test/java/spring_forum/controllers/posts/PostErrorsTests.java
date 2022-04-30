package spring_forum.controllers.posts;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import spring_forum.dtos.PostDTO;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@ActiveProfiles(value = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PostErrorsTests {

    private static final String DEFAULT_URL = "http://localhost:8080/posts";

    @Test
    public void findPostsForUserWithErrorTest() {
        given()
                .when().get(DEFAULT_URL + "/user/-1").then()
                .body(equalTo("User with ID = -1 doesn't exist."))
                .statusCode(400);
    }

    @Test
    public void findPostsByTagWithErrorTest() {
        given()
                .when().get(DEFAULT_URL + "/tag/-1n").then()
                .body(equalTo("There are no posts with tag \"-1n\"."))
                .statusCode(400);
    }

    @Test
    public void findByIdWithErrorTest() {
        given()
                .when().get(DEFAULT_URL + "/-1").then()
                .body(equalTo("There is no post with ID = -1"))
                .statusCode(400);
    }

    @Test
    public void findByTitleWithErrorTest() {
        given()
                .when().get(DEFAULT_URL + "/title/nonExistingPost").then()
                .body(equalTo("Post with title \"nonExistingPost\" doesn't exist."))
                .statusCode(400);
    }

    @Test
    public void savePostWithWrongOwnerIDTest() {
        PostDTO post = PostDTO.builder().title("New Post!").text("This is New Post!")
                .postOwnerID(-1L).build();
        given()
                .contentType(ContentType.JSON)
                .body(post)
                .when().post(DEFAULT_URL).then()
                .body(equalTo("User with ID = -1 doesn't exist."))
                .statusCode(400);
    }

    @Test
    public void savePostWithWrongTitleTest() {
        PostDTO post = PostDTO.builder().title("First post").text("This is New Post!")
                .postOwnerID(1L).build();
        given()
                .contentType(ContentType.JSON)
                .body(post)
                .when().post(DEFAULT_URL).then()
                .body(equalTo("Post with title \"First post\" already exists."))
                .statusCode(409);
    }

    @Test
    public void updatePostNotExistsTest() {
        PostDTO post = PostDTO.builder().id(-1L).title("New Post!").text("This is New Post!")
                .postOwnerID(1L).build();
        given()
                .contentType(ContentType.JSON)
                .body(post)
                .when().put(DEFAULT_URL).then()
                .body(equalTo("There is no post with ID = -1"))
                .statusCode(400);
    }

    @Test
    public void updatePostWithWrongTitleTest() {
        PostDTO post = PostDTO.builder().id(2L).title("First post").text("This is New Post!")
                .postOwnerID(1L).build();
        given()
                .contentType(ContentType.JSON)
                .body(post)
                .when().put(DEFAULT_URL).then()
                .body(equalTo("Post with title \"First post\" already exists."))
                .statusCode(409);
    }

    @Test
    public void deletePostNotExistsTest() {
        given()
                .when().delete(DEFAULT_URL + "/-1").then()
                .body(equalTo("There is no post with ID = -1"))
                .statusCode(400);
    }
}
