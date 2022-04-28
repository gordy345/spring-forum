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
@Table(name = "posts")
public class Post extends BaseEntity {

    @Column(name = "title")
    private String title;

    @Lob
    @Column(name = "text")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User postOwner;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post", fetch = FetchType.LAZY)
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "post", fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>();

    @Builder
    public Post(Long id, String title, String text, User postOwner, Set<Tag> tags, Set<Comment> comments) {
        super(id, 0L);
        this.title = title;
        this.text = text;
        this.postOwner = postOwner;
        if (tags != null) {
            this.tags = tags;
        }
        if (comments != null) {
            this.comments = comments;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return title.equals(post.title) &&
                text.equals(post.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, text);
    }
}
