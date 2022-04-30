package spring_forum.controllers.users;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import spring_forum.domain.Gender;
import spring_forum.dtos.RegisterDTO;
import spring_forum.dtos.UserDTO;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static spring_forum.TestConstants.NEGATIVE_ID;
import static spring_forum.utils.ExceptionMessages.*;

@ActiveProfiles(value = "test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class UserErrorsTests {

    private static final String DEFAULT_URL = "http://localhost:8080/users";

    @Test
    public void findByIdWithErrorTest() {
        given().when().get(DEFAULT_URL + "/-1").then()
                .body(equalTo(USER_NOT_FOUND_BY_ID + NEGATIVE_ID))
                .statusCode(400);
    }

    @Test
    public void findByEmailWithErrorTest() {
        String nonExistingEmail = "gog@ya.ru";
        given().when().get(DEFAULT_URL + "/email/" + nonExistingEmail).then()
                .body(equalTo(USER_NOT_FOUND_BY_EMAIL + nonExistingEmail))
                .statusCode(400);
    }

    @Test
    public void saveUserWithWrongEmailTest() {
        RegisterDTO user = RegisterDTO.builder().name("Vanya").email("gogo@ya.ru").isModerator(false)
                .gender(Gender.M).phoneNumber("+79875463773").password("password").confirmPassword("password").build();
        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().post(DEFAULT_URL).then()
                .body(equalTo(USER_EXISTS_WITH_EMAIL + user.getEmail()))
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
                .body(equalTo(USER_NOT_FOUND_BY_ID + NEGATIVE_ID))
                .statusCode(400);
    }

    @Test
    public void updateUserWrongEmailTest() {
        UserDTO user = UserDTO.builder().id(2L).name("Ivan").email("gogo@ya.ru").isModerator(true)
                .gender(Gender.M).phoneNumber("+79875643232").build();
        given()
                .contentType(ContentType.JSON)
                .body(user)
                .when().put(DEFAULT_URL).then()
                .body(equalTo(USER_EXISTS_WITH_EMAIL + user.getEmail()))
                .statusCode(409);
    }

    @Test
    public void deleteUserNotExistsTest() {
        given()
                .when().delete(DEFAULT_URL + "/-1").then()
                .body(equalTo(USER_NOT_FOUND_BY_ID + NEGATIVE_ID))
                .statusCode(400);
    }

}
