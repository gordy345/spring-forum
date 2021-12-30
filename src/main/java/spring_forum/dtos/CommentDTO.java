package spring_forum.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class CommentDTO extends BaseDTO {

    @NotBlank(message = "Comment cannot be null")
    @Size(min = 2, max = 8000, message = "Text size must be between 2 and 8000.")
    private String text;

    @NotNull(message = "Comment owner ID cannot be empty")
    private Long commentOwnerID;

    @NotNull(message = "Post ID cannot be empty")
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
