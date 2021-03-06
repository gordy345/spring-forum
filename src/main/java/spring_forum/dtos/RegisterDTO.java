package spring_forum.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring_forum.domain.enums.Gender;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class RegisterDTO {

    @NotBlank(message = "Name cannot be null")
    @Size(min = 2, max = 64, message = "Minimum size of name = 2 and maximum = 64")
    private String name;

    @NotBlank(message = "Email cannot be null")
    @Email(message = "Please enter valid email.")
    private String email;

    private boolean isModerator;

    @NotNull(message = "Gender field cannot be null")
    private Gender gender;

    @NotBlank(message = "Phone number cannot be null")
    private String phoneNumber;

    @NotBlank(message = "Password cannot be null")
    @Size(min = 6, max = 20, message = "Minimum size of password = 6 and maximum = 20")
    private String password;

    @NotBlank(message = "Confirm password cannot be null")
    private String confirmPassword;

    private String country;

    private String language;

    @Builder
    public RegisterDTO(String name, String email, boolean isModerator, Gender gender, String phoneNumber,
                       String password, String confirmPassword, String country, String language) {
        this.name = name;
        this.email = email;
        this.isModerator = isModerator;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.country = country;
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RegisterDTO that = (RegisterDTO) o;
        return isModerator == that.isModerator &&
                Objects.equals(name, that.name) &&
                Objects.equals(email, that.email) &&
                gender == that.gender &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(country, that.country) &&
                Objects.equals(language, that.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, isModerator, gender, phoneNumber, country, language);
    }
}
