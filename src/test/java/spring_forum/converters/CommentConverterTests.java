package spring_forum.converters;

import org.junit.jupiter.api.Test;
import spring_forum.domain.Comment;
import spring_forum.domain.Post;
import spring_forum.domain.User;
import spring_forum.dtos.CommentDTO;

import static org.junit.jupiter.api.Assertions.*;

public class CommentConverterTests {

    private final CommentConverter commentConverter = new CommentConverter();
    private final Comment comment = Comment.builder().id(1L).text("Comment")
            .commentOwner(User.builder().id(1L).build())
            .post(Post.builder().id(1L).build())
            .build();
    private final CommentDTO commentDTO = CommentDTO.builder().id(1L).text("Comment").build();


    @Test
    public void testNullObjectToDTO() throws Exception {
        assertNull(commentConverter.convertToCommentDTO(null));
    }

    @Test
    public void testNullObjectFromDTO() throws Exception {
        assertNull(commentConverter.convertToComment(null));
    }

    @Test
    public void testEmptyObjectFromDTO() throws Exception {
        assertNotNull(commentConverter.convertToComment(new CommentDTO()));
    }

    @Test
    public void convertToDTO() throws Exception {
        CommentDTO commentDTOConverted = commentConverter.convertToCommentDTO(comment);
        assertEquals(commentDTOConverted, commentDTO);
    }

    @Test
    public void convertFromDTO() throws Exception {
        Comment commentConverted = commentConverter.convertToComment(commentDTO);
        assertEquals(commentConverted, comment);
    }

}
