package spring_forum.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class TagDTO extends BaseDTO {

    private String tag;

    private Long postID;

    @Builder
    public TagDTO(Long id, String tag, Long postID) {
        super(id);
        this.tag = tag;
        this.postID = postID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagDTO tagDTO = (TagDTO) o;
        return Objects.equals(tag, tagDTO.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag);
    }
}
