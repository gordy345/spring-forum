package spring_forum.controllers.users;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import spring_forum.domain.Gender;
import spring_forum.dtos.UserDTO;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserValidTests {

    private static final String DEFAULT_URL = "http://localhost:8080/users";

    @Test
    public void basicStatusCodeTest() {
        given().when().get(DEFAULT_URL).then().statusCode(200);
    }

    @Test
    public void showAllUsersTest() {
        given().when().get(DEFAULT_URL).then()
                .body(containsString("Danya"))
                .body(containsString("Kirill"));
    }

    @Test
    public void findByIdValidTest() {
        given()
                .when().get(DEFAULT_URL + "/1").then()
                .body("name", equalTo("Danya"))
                .body("email", equalTo("gogo@ya.ru"))
                .body("gender", equalTo("M"))
                .body("phoneNumber", equalTo("+79875643232"))
                .body("moderator", equalTo(true))
                .statusCode(200);
    }

    @Test
    public void findByNameValidTest() {
        given().when().get(DEFAULT_URL + "/name/Danya").then()
                .body("id", equalTo(1))
                .body("email", equalTo("gogo@ya.ru"))
                .body("gender", equalTo("M"))
                .body("phoneNumber", equalTo("+79875643232"))
                .body("moderator", equalTo(true))
                .statusCode(200);
    }

    @Test
    public void saveAndDeleteUserValidTest() {
        UserDTO user = UserDTO.builder().name("Ivan").email("vanya333@ya.ru").isModerator(false)
                .gender(Gender.M).phoneNumber("+79875463773").build();
        Integer id = given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().post(DEFAULT_URL).then()
                .statusCode(200)
                .extract().path("id");

        given()
                .pathParam("userID", id)
                .when().delete(DEFAULT_URL + "/{userID}").then()
                .body(equalTo("User with ID = " + id + " was deleted."))
                .statusCode(200);
    }

    @Test
    public void updateUserValidTest() {
        UserDTO user = UserDTO.builder().id(1L).name("Danya").email("gogog@ya.ru").isModerator(true)
                .gender(Gender.M).phoneNumber("+79875643232").build();
        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().put(DEFAULT_URL).then()
                .body("email", equalTo("gogog@ya.ru"))
                .statusCode(200);
        user.setEmail("gogo@ya.ru");
        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().put(DEFAULT_URL).then()
                .body("email", equalTo("gogo@ya.ru"))
                .statusCode(200);
    }
}