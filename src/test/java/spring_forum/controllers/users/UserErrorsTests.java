package spring_forum.controllers.users;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import spring_forum.domain.Gender;
import spring_forum.dtos.UserDTO;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@ActiveProfiles(value = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserErrorsTests {

    private static final String DEFAULT_URL = "http://localhost:8080/users";

    @Test
    public void findByIdWithErrorTest() {
        given().when().get(DEFAULT_URL + "/-1").then()
                .body(equalTo("User with ID = -1 doesn't exist."))
                .statusCode(400);
    }

    @Test
    public void findByNameWithErrorTest() {
        given().when().get(DEFAULT_URL + "/name/Danyaa").then()
                .body(equalTo("User \"Danyaa\" doesn't exist."))
                .statusCode(400);
    }

    @Test
    public void saveUserWithWrongNameTest() {
        UserDTO user = UserDTO.builder().name("Danya").email("vanya333@ya.ru").isModerator(false)
                .gender(Gender.M).phoneNumber("+79875463773").build();
        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().post(DEFAULT_URL).then()
                .body(equalTo("User with name \"Danya\" already exists."))
                .statusCode(409);
    }

    @Test
    public void updateUserNotExistsTest() {
        UserDTO user = UserDTO.builder().id(-1L).name("Ivan").email("vanya333@ya.ru").isModerator(false)
                .gender(Gender.M).phoneNumber("+79875463773").build();
        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().put(DEFAULT_URL).then()
                .body(equalTo("User with ID = -1 doesn't exist."))
                .statusCode(400);
    }

    @Test
    public void updateUserWrongNameTest() {
        UserDTO user = UserDTO.builder().id(2L).name("Danya").email("gogog@ya.ru").isModerator(true)
                .gender(Gender.M).phoneNumber("+79875643232").build();
        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().put(DEFAULT_URL).then()
                .body(equalTo("User with name \"Danya\" already exists."))
                .statusCode(409);
    }

    @Test
    public void deleteUserNotExistsTest() {
        given()
                .when().delete(DEFAULT_URL + "/-1").then()
                .body(equalTo("User with ID = -1 doesn't exist."))
                .statusCode(400);
    }

}
