package spring_forum.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spring_forum.domain.Comment;
import spring_forum.domain.Post;
import spring_forum.exceptions.NotFoundException;
import spring_forum.rabbitMQ.Producer;
import spring_forum.repositories.CommentRepository;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;

import static spring_forum.utils.CacheKeys.*;
import static spring_forum.utils.ExceptionMessages.COMMENT_NOT_FOUND_BY_ID;
import static spring_forum.utils.ExceptionMessages.NO_COMMENTS_FOR_POST;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final CacheService cacheService;
    private final Producer producer;

    @Override
    @Transactional
    public Set<Comment> findCommentsForPostByID(Long postID) {
        log.info("Finding comments for post with ID = " + postID);
        Post post = postService.findByID(postID);
        Set<Comment> comments = post.getComments();
        if (comments.size() == 0) {
            String message = NO_COMMENTS_FOR_POST + postID;
            producer.send(message);
            throw new NotFoundException(message);
        }
        return comments;
    }

    @Override
    @Transactional
    public Long countCommentsByPostId(Long postID) {
        log.info("Counting comments for post with ID = " + postID);
        postService.findByID(postID);
        return commentRepository.countCommentsByPostId(postID);
    }

    @Override
    @Transactional
    public Comment findByID(Long id) {
        log.info("Finding comment with ID = " + id);
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isEmpty()) {
            String message = COMMENT_NOT_FOUND_BY_ID + id;
            producer.send(message);
            throw new NotFoundException(message);
        }
        return commentOptional.get();
    }

    @Override
    @Transactional
    public Comment save(Comment comment) {
        log.info("Saving new comment with text: " + comment.getText());
        cacheService.remove(COMMENTS_FOR_POST + comment.getPost().getId(),
                COMMENTS_COUNT_FOR_POST + comment.getPost().getId());
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment update(Comment comment) {
        log.info("Updating comment with ID = " + comment.getId());
        Comment commentByID = findByID(comment.getId());
        commentByID.setText(comment.getText());
        commentByID.setPost(comment.getPost());
        commentByID.setCommentOwner(comment.getCommentOwner());
        cacheService.remove(COMMENT_BY_ID + comment.getId(),
                COMMENTS_FOR_POST + comment.getPost().getId());
        return commentByID;
    }

    @Override
    @Transactional
    public Comment deleteByID(Long id) {
        log.info("Deleting comment with ID = " + id);
        Comment comment = findByID(id);
        cacheService.remove(COMMENT_BY_ID + id,
                COMMENTS_FOR_POST + comment.getPost().getId(),
                COMMENTS_COUNT_FOR_POST + comment.getPost().getId());
        commentRepository.delete(comment);
        return comment;
    }
}
