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
public class PostDTO extends BaseDTO {

    @NotBlank(message = "Title cannot be null")
    @Size(min = 2, max = 60, message = "Title size must be between 2 and 60.")
    private String title;

    @NotBlank(message = "Text cannot be empty")
    private String text;

    @NotNull(message = "Post owner ID cannot be null")
    private Long postOwnerID;

    @Builder
    public PostDTO(Long id, String title, String text, Long postOwnerID) {
        super(id);
        this.title = title;
        this.text = text;
        this.postOwnerID = postOwnerID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostDTO postDTO = (PostDTO) o;
        return Objects.equals(title, postDTO.title) &&
                Objects.equals(text, postDTO.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, text);
    }
}
