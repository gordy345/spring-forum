package spring_forum.converters;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import spring_forum.domain.User;
import spring_forum.domain.enums.NameColor;
import spring_forum.dtos.RegisterDTO;
import spring_forum.dtos.UserDTO;

@Component
@RequiredArgsConstructor
public class UserConverter {

    private final PasswordEncoder passwordEncoder;

    public UserDTO convertToUserDTO(User user) {
        if (user == null) {
            return null;
        }
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .isModerator(user.isModerator())
                .gender(user.getGender())
                .phoneNumber(user.getPhoneNumber())
                .country(user.getCountry())
                .language(user.getLanguage())
                .enabled(user.isEnabled())
                .rating(user.getRating())
                .nameColor(user.getNameColor())
                .build();
    }

    public User convertToUser(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }
        return User.builder()
                .id(userDTO.getId())
                .name(userDTO.getName())
                .email(userDTO.getEmail())
                .isModerator(userDTO.isModerator())
                .gender(userDTO.getGender())
                .phoneNumber(userDTO.getPhoneNumber())
                .country(userDTO.getCountry())
                .language(userDTO.getLanguage())
                .enabled(userDTO.isEnabled())
                .rating(userDTO.getRating())
                .nameColor(userDTO.getNameColor())
                .build();
    }

    public User convertToUser(RegisterDTO registerDTO) {
        if (registerDTO == null) {
            return null;
        }
        return User.builder()
                .name(registerDTO.getName())
                .email(registerDTO.getEmail())
                .isModerator(registerDTO.isModerator())
                .gender(registerDTO.getGender())
                .phoneNumber(registerDTO.getPhoneNumber())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .country(registerDTO.getCountry())
                .language(registerDTO.getLanguage())
                .nameColor(NameColor.BLACK)
                .build();
    }
}
