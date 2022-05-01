package spring_forum.converters;

import org.junit.jupiter.api.Test;
import spring_forum.domain.Tag;
import spring_forum.dtos.TagDTO;

import static org.junit.jupiter.api.Assertions.*;
import static spring_forum.TestConstants.TAG;
import static spring_forum.TestConstants.TAG_DTO;

public class TagConverterTests {

    private final TagConverter tagConverter = new TagConverter();

    @Test
    public void testNullObjectToDTO() {
        assertNull(tagConverter.convertToTagDTO(null));
    }

    @Test
    public void testNullObjectFromDTO() {
        assertNull(tagConverter.convertToTag(null));
    }

    @Test
    public void testEmptyObjectFromDTO() {
        assertNotNull(tagConverter.convertToTag(new TagDTO()));
    }

    @Test
    public void convertToDTO() {
        TagDTO tagDTOConverted = tagConverter.convertToTagDTO(TAG);
        assertEquals(tagDTOConverted, TAG_DTO);
    }

    @Test
    public void convertFromDTO() {
        Tag tagConverted = tagConverter.convertToTag(TAG_DTO);
        assertEquals(tagConverted, TAG);
    }

}
