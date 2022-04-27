package spring_forum.converters;

import org.junit.jupiter.api.Test;
import spring_forum.domain.Gender;
import spring_forum.domain.User;
import spring_forum.dtos.UserDTO;

import static org.junit.jupiter.api.Assertions.*;

public class UserConverterTests {

    private final UserConverter userConverter = new UserConverter();
    private final User user = User.builder().id(1L).name("Dan").email("Dan@ya.ru")
            .isModerator(true).gender(Gender.M).phoneNumber("+7")
            .country("country").language("language").build();
    private final UserDTO userDTO = UserDTO.builder().id(1L).name("Dan").email("Dan@ya.ru")
            .isModerator(true).gender(Gender.M).phoneNumber("+7")
            .country("country").language("language").build();

    @Test
    public void testNullObjectToDTO() throws Exception {
        assertNull(userConverter.convertToUserDTO(null));
    }

    @Test
    public void testNullObjectFromDTO() throws Exception {
        assertNull(userConverter.convertToUser(null));
    }

    @Test
    public void testEmptyObjectToDTO() throws Exception {
        assertNotNull(userConverter.convertToUserDTO(new User()));
    }

    @Test
    public void testEmptyObjectFromDTO() throws Exception {
        assertNotNull(userConverter.convertToUser(new UserDTO()));
    }

    @Test
    public void convertToDTO() throws Exception {
        UserDTO userDTOConverted = userConverter.convertToUserDTO(user);
        assertEquals(userDTOConverted, userDTO);
    }

    @Test
    public void convertFromDTO() throws Exception {
        User userConverted = userConverter.convertToUser(userDTO);
        assertEquals(userConverted, user);
    }

}