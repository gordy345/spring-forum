package spring_forum.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring_forum.domain.enums.Gender;
import spring_forum.domain.enums.NameColor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@SuppressWarnings("ALL")
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "is_moderator")
    private boolean isModerator;

    @Column(name = "gender")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "password")
    private String password;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "country")
    private String country;

    @Column(name = "language")
    private String language;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "rating")
    private long rating;

    @Column(name = "name_color")
    @Enumerated(EnumType.STRING)
    private NameColor nameColor;

    @OneToMany(mappedBy = "postOwner")
    private Set<Post> posts = new HashSet<>();

    @Builder
    public User(Long id, String name, String email, boolean isModerator, NameColor nameColor,
                Gender gender, String phoneNumber, String password, boolean enabled,
                String country, String language, String imageUrl, long rating, Set<Post> posts) {
        super(id, 0L);
        this.name = name;
        this.email = email;
        this.isModerator = isModerator;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.enabled = enabled;
        this.country = country;
        this.language = language;
        this.imageUrl = imageUrl;
        this.rating = rating;
        this.nameColor = nameColor;
        if (posts != null) {
            this.posts = posts;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return isModerator == user.isModerator &&
                enabled == user.enabled &&
                Objects.equals(name, user.name) &&
                Objects.equals(email, user.email) &&
                gender == user.gender &&
                nameColor == user.nameColor &&
                Objects.equals(phoneNumber, user.phoneNumber) &&
                Objects.equals(country, user.country) &&
                Objects.equals(language, user.language) &&
                Objects.equals(imageUrl, user.imageUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, isModerator, gender, nameColor, phoneNumber, enabled, country, language, imageUrl);
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", isModerator=" + isModerator +
                ", gender=" + gender +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", country='" + country + '\'' +
                ", language='" + language + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", rating=" + rating +
                ", nameColor=" + nameColor +
                '}';
    }
}
