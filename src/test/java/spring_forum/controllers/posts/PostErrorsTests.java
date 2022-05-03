package spring_forum.controllers.posts;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import spring_forum.dtos.PostDTO;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static spring_forum.TestConstants.NEGATIVE_ID;
import static spring_forum.utils.ExceptionMessages.*;

@ActiveProfiles(value = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class PostErrorsTests {

    private static final String DEFAULT_URL = "http://localhost:8080/posts";

    @Test
    public void findPostsForUserWithErrorTest() {
        given()
                .when().get(DEFAULT_URL + "/user/-1").then()
                .body(equalTo(USER_NOT_FOUND_BY_ID + NEGATIVE_ID))
                .statusCode(400);
    }

    @Test
    public void findPostsByTagWithErrorTest() {
        String tag = "-1n";
        given()
                .when().get(DEFAULT_URL + "/tag/" + tag).then()
                .body(equalTo(TAG_NOT_FOUND_BY_VALUE + tag))
                .statusCode(400);
    }

    @Test
    public void findByIdWithErrorTest() {
        given()
                .when().get(DEFAULT_URL + "/-1").then()
                .body(equalTo(POST_NOT_FOUND_BY_ID + NEGATIVE_ID))
                .statusCode(400);
    }

    @Test
    public void findByTitleWithErrorTest() {
        String title = "nonExistingPost";
        given()
                .when().get(DEFAULT_URL + "/title/" + title).then()
                .body(equalTo(POST_NOT_FOUND_BY_TITLE + title))
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
                .body(equalTo(USER_NOT_FOUND_BY_ID + NEGATIVE_ID))
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
                .body(equalTo(POST_EXISTS_WITH_TITLE + post.getTitle()))
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
                .body(equalTo(POST_NOT_FOUND_BY_ID + NEGATIVE_ID))
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
                .body(equalTo(POST_EXISTS_WITH_TITLE + post.getTitle()))
                .statusCode(409);
    }

    @Test
    public void deletePostNotExistsTest() {
        given()
                .when().delete(DEFAULT_URL + "/-1").then()
                .body(equalTo(POST_NOT_FOUND_BY_ID + NEGATIVE_ID))
                .statusCode(400);
    }
}
