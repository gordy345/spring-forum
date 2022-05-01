package spring_forum.repositories;

import org.springframework.data.repository.CrudRepository;
import spring_forum.domain.Comment;

public interface CommentRepository extends CrudRepository<Comment, Long> {

    Long countCommentsByPostId(Long postID);
}
