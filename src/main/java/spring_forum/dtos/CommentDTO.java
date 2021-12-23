package spring_forum.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class CommentDTO extends BaseDTO {

    private String text;

    private Long commentOwnerID;

    private Long postID;

    @Builder
    public CommentDTO(Long id, String text, Long commentOwnerID, Long postID) {
        super(id);
        this.text = text;
        this.commentOwnerID = commentOwnerID;
        this.postID = postID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommentDTO that = (CommentDTO) o;
        return Objects.equals(text, that.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }
}
