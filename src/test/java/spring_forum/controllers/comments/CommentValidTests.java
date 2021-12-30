package spring_forum.controllers.comments;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import spring_forum.dtos.CommentDTO;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class CommentValidTests {

    private static final String DEFAULT_URL = "http://localhost:8080/comments";

    @Test
    public void showCommentsForPostValidTest() {
        given()
                .when().get(DEFAULT_URL + "/post/1").then()
                .body(containsString("First comment!"))
                .body(containsString("1"))
                .statusCode(200);
    }

    @Test
    public void findByIdValidTest() {
        given()
                .when().get(DEFAULT_URL + "/1").then()
                .body("text", equalTo("First comment!"))
                .body("commentOwnerID", equalTo(1))
                .body("postID", equalTo(1))
                .statusCode(200);
    }

    @Test
    public void saveAndDeleteCommentValidTest() {
        CommentDTO comment = CommentDTO.builder().text("New Comment").postID(1L).commentOwnerID(1L).build();
        Integer id = given()
                .contentType(ContentType.JSON)
                .body(comment)
                .when().post(DEFAULT_URL).then()
                .body("commentOwnerID", equalTo(1))
                .statusCode(200)
                .extract().path("id");

        given()
                .pathParam("commentID", id)
                .when().delete(DEFAULT_URL + "/{commentID}").then()
                .body(equalTo("Comment with ID = " + id + " was deleted."))
                .statusCode(200);
    }

    @Test
    public void updateCommentValidTest() {
        CommentDTO comment = CommentDTO.builder().id(1L).text("New Comment").postID(1L).commentOwnerID(1L).build();
        given()
                .contentType(ContentType.JSON)
                .body(comment)
                .when().put(DEFAULT_URL).then()
                .body("text", equalTo("New Comment"))
                .body("commentOwnerID", equalTo(1))
                .statusCode(200);
        comment.setText("First comment!");
        given()
                .contentType(ContentType.JSON)
                .body(comment)
                .when().put(DEFAULT_URL).then()
                .body("text", equalTo("First comment!"))
                .statusCode(200);
    }

}
