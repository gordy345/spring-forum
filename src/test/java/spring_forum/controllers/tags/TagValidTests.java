package spring_forum.controllers.tags;


import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import spring_forum.dtos.TagDTO;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

@ActiveProfiles(value = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TagValidTests {

    private static final String DEFAULT_URL = "http://localhost:8080/tags";

    @BeforeAll
    static void beforeAll() {
        RestAssured.registerParser("text/plain", Parser.JSON);
    }

    @Test
    public void showTagsForPostValidTest() {
        given()
                .when().get(DEFAULT_URL + "/post/1").then()
                .body(containsString("firstTag"))
                .body(containsString("secondTag"))
                .statusCode(200);
    }

    @Test
    public void findByIdValidTest() {
        given()
                .when().get(DEFAULT_URL + "/1").then()
                .body(equalTo("firstTag"))
                .statusCode(200);
    }

    @Test
    public void saveAndDeleteTagValidTest() {
        TagDTO tag = TagDTO.builder().tag("newTag").postID(1L).build();
        Integer id = given()
                .contentType(ContentType.JSON)
                .body(tag)
                .when().post(DEFAULT_URL).then()
                .body("tag", equalTo("newTag"))
                .statusCode(200)
                .extract().path("id");

        given()
                .pathParam("tagID", id)
                .pathParam("postID", tag.getPostID())
                .when().delete(DEFAULT_URL + "/{tagID}/post/{postID}").then()
                .body(equalTo("Tag with ID = " + id + " was deleted for post with ID = " + tag.getPostID()))
                .statusCode(200);
    }

    @Test
    public void updateTagValidTest() {
        TagDTO tag = TagDTO.builder().id(1L).tag("newTag").postID(1L).build();
        given()
                .contentType(ContentType.JSON)
                .body(tag)
                .when().put(DEFAULT_URL).then()
                .body("tag", equalTo("newTag"))
                .statusCode(200);
        tag.setTag("firstTag");
        given()
                .contentType(ContentType.JSON)
                .body(tag)
                .when().put(DEFAULT_URL).then()
                .body("tag", equalTo("firstTag"))
                .statusCode(200);
    }
}
