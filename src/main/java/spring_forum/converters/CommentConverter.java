package spring_forum.converters;

import org.springframework.stereotype.Component;
import spring_forum.domain.Comment;
import spring_forum.dtos.CommentDTO;

@Component
public class CommentConverter {

    public CommentDTO convertToCommentDTO(Comment comment) {
        return CommentDTO.builder()
                .id(comment.getId())
                .text(comment.getText())
                .commentOwnerID(comment.getCommentOwner().getId())
                .postID(comment.getPost().getId())
                .build();
    }

    public Comment convertToComment(CommentDTO commentDTO) {
        return Comment.builder()
                .id(commentDTO.getId())
                .text(commentDTO.getText())
                .build();
    }
}
