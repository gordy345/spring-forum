package spring_forum.services;

import spring_forum.domain.Comment;

import java.util.Set;

public interface CommentService extends CrudService<Comment, Long> {

    Set<Comment> findCommentsForPostByID(Long postID);
}
