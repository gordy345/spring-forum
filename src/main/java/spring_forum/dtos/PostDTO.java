package spring_forum.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class PostDTO extends BaseDTO {

    private String title;

    private String text;

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
