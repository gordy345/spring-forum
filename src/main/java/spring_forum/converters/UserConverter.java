package spring_forum.converters;

import org.springframework.stereotype.Component;
import spring_forum.domain.User;
import spring_forum.dtos.UserDTO;

@Component
public class UserConverter {

    public UserDTO convertToUserDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .isModerator(user.isModerator())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .build();
    }

    public User convertToUser(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .isModerator(userDTO.isModerator())
                .gender(userDTO.getGender())
                .phoneNumber(userDTO.getPhoneNumber())
                .build();

    }
}
