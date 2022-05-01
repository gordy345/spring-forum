package spring_forum.controllers.tags;


import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import spring_forum.dtos.TagDTO;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static spring_forum.TestConstants.NEGATIVE_ID;
import static spring_forum.utils.ExceptionMessages.*;

@ActiveProfiles(value = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TagErrorsTests {

    private static final String DEFAULT_URL = "http://localhost:8080/tags";

    @Test
    public void findTagsForPostWithErrorTest() {
        given()
                .when().get(DEFAULT_URL + "/post/-1").then()
                .body(equalTo(POST_NOT_FOUND_BY_ID + NEGATIVE_ID))
                .statusCode(400);
    }

    @Test
    public void saveTagForNonExistingPostTest() {
        TagDTO tag = TagDTO.builder().tag("newTag").postID(-1L).build();
        given()
                .contentType(ContentType.JSON)
                .body(tag)
                .when().post(DEFAULT_URL).then()
                .body(equalTo(POST_NOT_FOUND_BY_ID + NEGATIVE_ID))
                .statusCode(400);
    }

    @Test
    public void saveAlreadyExistingTagTest() {
        TagDTO tag = TagDTO.builder().tag("firstTag").postID(1L).build();
        given()
                .contentType(ContentType.JSON)
                .body(tag)
                .when().post(DEFAULT_URL).then()
                .body(equalTo(TAG_EXISTS_FOR_POST + tag.getPostID()))
                .statusCode(409);
    }

    @Test
    public void updateTagNotExists() {
        TagDTO tag = TagDTO.builder().tag("newTag").postID(1L).build();
        tag.setId(NEGATIVE_ID);
        given()
                .contentType(ContentType.JSON)
                .body(tag)
                .when().put(DEFAULT_URL).then()
                .body(equalTo(TAG_NOT_FOUND_BY_ID + NEGATIVE_ID))
                .statusCode(400);
    }

    @Test
    public void updateTagWithWrongPostIDTest() {
        TagDTO tag = TagDTO.builder().tag("newTag").postID(-1L).build();
        tag.setId(1L);
        given()
                .contentType(ContentType.JSON)
                .body(tag)
                .when().put(DEFAULT_URL).then()
                .body(equalTo(POST_NOT_FOUND_BY_ID + NEGATIVE_ID))
                .statusCode(400);
    }

    @Test
    public void updateTagSetAlreadyExistingTagTest() {
        TagDTO tag = TagDTO.builder().tag("secondTag").postID(1L).build();
        tag.setId(1L);
        given()
                .contentType(ContentType.JSON)
                .body(tag)
                .when().put(DEFAULT_URL).then()
                .body(equalTo(TAG_EXISTS_FOR_POST + tag.getPostID()))
                .statusCode(409);
    }

    @Test
    public void updateTagWithWrongPostTest() {
        TagDTO tag = TagDTO.builder().tag("newTag").postID(2L).build();
        tag.setId(1L);
        given()
                .contentType(ContentType.JSON)
                .body(tag)
                .when().put(DEFAULT_URL).then()
                .body(equalTo(POST_DOESNT_CONTAIN_TAG + 2))
                .statusCode(400);
    }

    @Test
    public void deleteTagNotExistsTest() {
        given()
                .when().delete(DEFAULT_URL + "/-1/post/1").then()
                .body(equalTo(TAG_NOT_FOUND_BY_ID + NEGATIVE_ID))
                .statusCode(400);
    }
}
