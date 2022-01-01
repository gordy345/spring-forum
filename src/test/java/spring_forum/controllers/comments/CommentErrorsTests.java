package spring_forum.controllers.comments;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import spring_forum.dtos.CommentDTO;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@ActiveProfiles(value = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CommentErrorsTests {

    private static final String DEFAULT_URL = "http://localhost:8080/comments";

    @Test
    void findByIDWithErrorTest() {
        given().when().get(DEFAULT_URL + "/-1").then()
                .body(equalTo("Comment with ID = -1 doesn't exist."))
                .statusCode(400);
    }

    @Test
    public void findCommentsForPostWithErrorTest() {
        given()
                .when().get(DEFAULT_URL + "/post/-1").then()
                .body(equalTo("There is no post with ID = -1"))
                .statusCode(400);
    }

    @Test
    public void saveCommentWithWrongCommentOwnerIDTest() {
        CommentDTO comment = CommentDTO.builder().text("New Comment").postID(1L).commentOwnerID(-1L).build();
        given()
                .contentType(ContentType.JSON)
                .body(comment)
                .when().post(DEFAULT_URL).then()
                .body(equalTo("User with ID = -1 doesn't exist."))
                .statusCode(400);
    }

    @Test
    public void saveCommentForNonExistingPostTest() {
        CommentDTO comment = CommentDTO.builder().text("New Comment").postID(-1L).commentOwnerID(1L).build();
        given()
                .contentType(ContentType.JSON)
                .body(comment)
                .when().post(DEFAULT_URL).then()
                .body(equalTo("There is no post with ID = -1"))
                .statusCode(400);
    }

    @Test
    public void updateCommentNotExistsTest() {
        CommentDTO comment = CommentDTO.builder().text("New Comment").postID(1L).commentOwnerID(1L).build();
        comment.setId(-1L);
        given()
                .contentType(ContentType.JSON)
                .body(comment)
                .when().put(DEFAULT_URL).then()
                .body(equalTo("Comment with ID = -1 doesn't exist."))
                .statusCode(400);
    }

    @Test
    public void deleteCommentNotExistsTest() {
        given()
                .when().delete(DEFAULT_URL + "/-1").then()
                .body(equalTo("Comment with ID = -1 doesn't exist."))
                .statusCode(400);
    }
}
