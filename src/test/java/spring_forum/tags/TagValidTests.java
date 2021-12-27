package spring_forum.tags;


import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import spring_forum.dtos.TagDTO;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TagValidTests {

    private static final String DEFAULT_URL = "http://localhost:8080/tags";

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
                .body("tag", equalTo("firstTag"))
                .body("postID", equalTo(1))
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
                .when().delete(DEFAULT_URL + "/{tagID}").then()
                .body(equalTo("Tag with ID = " + id + " was deleted."))
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
