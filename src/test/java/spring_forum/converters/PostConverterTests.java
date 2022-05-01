package spring_forum.converters;

import org.junit.jupiter.api.Test;
import spring_forum.domain.Post;
import spring_forum.dtos.PostDTO;

import static org.junit.jupiter.api.Assertions.*;
import static spring_forum.TestConstants.POST;
import static spring_forum.TestConstants.POST_DTO;

public class PostConverterTests {

    private final PostConverter postConverter = new PostConverter();

    @Test
    public void testNullObjectToDTO() {
        assertNull(postConverter.convertToPostDTO(null));
    }

    @Test
    public void testNullObjectFromDTO() {
        assertNull(postConverter.convertToPost(null));
    }

    @Test
    public void testEmptyObjectFromDTO() {
        assertNotNull(postConverter.convertToPost(new PostDTO()));
    }

    @Test
    public void convertToDTO() {
        PostDTO postDTOConverted = postConverter.convertToPostDTO(POST);
        assertEquals(postDTOConverted, POST_DTO);
    }

    @Test
    public void convertFromDTO() {
        Post postConverted = postConverter.convertToPost(POST_DTO);
        assertEquals(postConverted, POST);
    }

}
