package spring_forum.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @OneToMany(mappedBy = "postOwner")
    private Set<Post> posts = new HashSet<>();

    @Builder
    public User(Long id, String name, String email, boolean isModerator, Gender gender, String phoneNumber, Set<Post> posts) {
        super(id);
        this.name = name;
        this.email = email;
        this.isModerator = isModerator;
        this.gender = gender;
        this.phoneNumber = phoneNumber;
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
                Objects.equals(name, user.name) &&
                Objects.equals(email, user.email) &&
                gender == user.gender &&
                Objects.equals(phoneNumber, user.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, isModerator, gender, phoneNumber);
    }
}
