package spring_forum.converters;

import org.junit.jupiter.api.Test;
import spring_forum.domain.Post;
import spring_forum.domain.User;
import spring_forum.dtos.PostDTO;

import static org.junit.jupiter.api.Assertions.*;

public class PostConverterTests {

    private final PostConverter postConverter = new PostConverter();
    private final Post post = Post.builder().id(1L).title("title").text("text")
            .postOwner(User.builder().id(1L).build()).build();
    private final PostDTO postDTO = PostDTO.builder().id(1L).title("title").text("text").build();


    @Test
    public void testNullObjectToDTO() throws Exception {
        assertNull(postConverter.convertToPostDTO(null));
    }

    @Test
    public void testNullObjectFromDTO() throws Exception {
        assertNull(postConverter.convertToPost(null));
    }

    @Test
    public void testEmptyObjectFromDTO() throws Exception {
        assertNotNull(postConverter.convertToPost(new PostDTO()));
    }

    @Test
    public void convertToDTO() throws Exception {
        PostDTO postDTOConverted = postConverter.convertToPostDTO(post);
        assertEquals(postDTOConverted, postDTO);
    }

    @Test
    public void convertFromDTO() throws Exception {
        Post postConverted = postConverter.convertToPost(postDTO);
        assertEquals(postConverted, post);
    }

}
