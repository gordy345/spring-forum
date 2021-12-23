package spring_forum.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@SuppressWarnings("ALL")
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tags")
public class Tag extends BaseEntity {

    @Column(name = "tag")
    private String tag;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public Tag(Long id, String tag, Post post) {
        super(id);
        this.tag = tag;
        if (post != null) {
            this.post = post;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag1 = (Tag) o;
        return Objects.equals(tag, tag1.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag);
    }
}
