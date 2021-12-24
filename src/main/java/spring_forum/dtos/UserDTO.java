package spring_forum.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring_forum.domain.Gender;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO extends BaseDTO {

    private String name;

    private String email;

    private boolean isModerator;

    private Gender gender;

    private String phoneNumber;

    @Builder
    public UserDTO(Long id, String name, String email, boolean isModerator, Gender gender, String phoneNumber) {
        super(id);
        this.name = name;
        this.email = email;
        this.isModerator = isModerator;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return isModerator == userDTO.isModerator &&
                Objects.equals(name, userDTO.name) &&
                Objects.equals(email, userDTO.email) &&
                gender == userDTO.gender &&
                Objects.equals(phoneNumber, userDTO.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, isModerator, gender, phoneNumber);
    }
}
