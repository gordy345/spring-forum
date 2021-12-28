package spring_forum.converters;

import org.springframework.stereotype.Component;
import spring_forum.domain.Tag;
import spring_forum.dtos.TagDTO;

@Component
public class TagConverter {

    public TagDTO convertToTagDTO(Tag tag) {
        if (tag == null) return null;
        return TagDTO.builder()
                .id(tag.getId())
                .tag(tag.getTag())
                .postID(tag.getPost().getId())
                .build();
    }

    public Tag convertToTag(TagDTO tagDTO) {
        if (tagDTO == null) return null;
        return Tag.builder()
                .id(tagDTO.getId())
                .tag(tagDTO.getTag())
                .build();
    }
}
