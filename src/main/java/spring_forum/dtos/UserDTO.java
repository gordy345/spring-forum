package spring_forum.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring_forum.domain.enums.Gender;
import spring_forum.domain.enums.NameColor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO extends BaseDTO {

    @NotBlank(message = "Name cannot be null")
    @Size(min = 2, max = 64, message = "Minimum size of name = 2 and maximum = 64")
    private String name;

    @NotBlank(message = "Email cannot be null")
    @Email(message = "Please enter valid email.")
    private String email;

    @NotNull(message = "isModerator property cannot be null")
    private boolean isModerator;

    @NotNull(message = "Gender field cannot be null")
    private Gender gender;

    @NotBlank(message = "Phone number cannot be null")
    private String phoneNumber;

    private String country;

    private String language;

    private boolean enabled;

    private long rating;

    private NameColor nameColor;

    @Builder
    public UserDTO(Long id, String name, String email, boolean isModerator, Gender gender, NameColor nameColor,
                   String phoneNumber, boolean enabled, String country, long rating, String language) {
        super(id);
        this.name = name;
        this.email = email;
        this.isModerator = isModerator;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.enabled = enabled;
        this.country = country;
        this.rating = rating;
        this.language = language;
        this.nameColor = nameColor;
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
                Objects.equals(phoneNumber, userDTO.phoneNumber) &&
                Objects.equals(country, userDTO.country) &&
                Objects.equals(language, userDTO.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, isModerator, gender, phoneNumber, country, language);
    }
}
