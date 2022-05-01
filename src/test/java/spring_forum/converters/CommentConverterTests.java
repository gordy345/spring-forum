package spring_forum.converters;

import org.junit.jupiter.api.Test;
import spring_forum.domain.Comment;
import spring_forum.dtos.CommentDTO;

import static org.junit.jupiter.api.Assertions.*;
import static spring_forum.TestConstants.COMMENT;
import static spring_forum.TestConstants.COMMENT_DTO;

public class CommentConverterTests {

    private final CommentConverter commentConverter = new CommentConverter();

    @Test
    public void testNullObjectToDTO() {
        assertNull(commentConverter.convertToCommentDTO(null));
    }

    @Test
    public void testNullObjectFromDTO() {
        assertNull(commentConverter.convertToComment(null));
    }

    @Test
    public void testEmptyObjectFromDTO() {
        assertNotNull(commentConverter.convertToComment(new CommentDTO()));
    }

    @Test
    public void convertToDTO() {
        CommentDTO commentDTOConverted = commentConverter.convertToCommentDTO(COMMENT);
        assertEquals(commentDTOConverted, COMMENT_DTO);
    }

    @Test
    public void convertFromDTO() {
        Comment commentConverted = commentConverter.convertToComment(COMMENT_DTO);
        assertEquals(commentConverted, COMMENT);
    }

}
