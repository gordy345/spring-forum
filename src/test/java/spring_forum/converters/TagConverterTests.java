package spring_forum.converters;

import org.junit.jupiter.api.Test;
import spring_forum.domain.Post;
import spring_forum.domain.Tag;
import spring_forum.dtos.TagDTO;

import static org.junit.jupiter.api.Assertions.*;

public class TagConverterTests {

    private final TagConverter tagConverter = new TagConverter();
    private final Tag tag = Tag.builder().id(1L).tag("Tag")
            .post(Post.builder().id(1L).build())
            .build();
    private final TagDTO tagDTO = TagDTO.builder().id(1L).tag("Tag").build();


    @Test
    public void testNullObjectToDTO() throws Exception {
        assertNull(tagConverter.convertToTagDTO(null));
    }

    @Test
    public void testNullObjectFromDTO() throws Exception {
        assertNull(tagConverter.convertToTag(null));
    }

    @Test
    public void testEmptyObjectFromDTO() throws Exception {
        assertNotNull(tagConverter.convertToTag(new TagDTO()));
    }

    @Test
    public void convertToDTO() throws Exception {
        TagDTO tagDTOConverted = tagConverter.convertToTagDTO(tag);
        assertEquals(tagDTOConverted, tagDTO);
    }

    @Test
    public void convertFromDTO() throws Exception {
        Tag tagConverted = tagConverter.convertToTag(tagDTO);
        assertEquals(tagConverted, tag);
    }

}
