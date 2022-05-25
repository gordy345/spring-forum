package spring_forum.converters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import spring_forum.domain.User;
import spring_forum.domain.enums.NameColor;
import spring_forum.dtos.UserDTO;

import static org.junit.jupiter.api.Assertions.*;
import static spring_forum.TestConstants.*;

@ExtendWith(MockitoExtension.class)
public class UserConverterTests {

    private final UserConverter userConverter = new UserConverter(new BCryptPasswordEncoder());

    @Test
    public void testNullObjectToDTO() {
        assertNull(userConverter.convertToUserDTO(null));
    }

    @Test
    public void testEmptyObjectToDTO() {
        assertNotNull(userConverter.convertToUserDTO(new User()));
    }

    @Test
    public void testEmptyObjectFromDTO() {
        assertNotNull(userConverter.convertToUser(new UserDTO()));
    }

    @Test
    public void convertToDTO() {
        UserDTO userDTOConverted = userConverter.convertToUserDTO(USER);
        assertEquals(userDTOConverted, USER_DTO);
    }

    @Test
    public void convertFromUserDTO() {
        User userConverted = userConverter.convertToUser(USER_DTO);
        userConverted.setImageUrl(USER.getImageUrl());
        assertEquals(userConverted, USER);
    }

    @Test
    public void convertFromRegisterDTO() {
        User userConverted = userConverter.convertToUser(REGISTER_DTO);
        userConverted.setEnabled(true);
        userConverted.setImageUrl(USER.getImageUrl());
        assertEquals(userConverted.getNameColor(), NameColor.BLACK);
        userConverted.setNameColor(USER.getNameColor());
        assertEquals(userConverted, USER);
    }

}