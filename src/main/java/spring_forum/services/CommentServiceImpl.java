package spring_forum.services;

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

import static spring_forum.utils.CacheKeys.COMMENTS_FOR_POST;
import static spring_forum.utils.CacheKeys.COMMENT_BY_ID;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final CacheService cacheService;
    private final Producer producer;

    public CommentServiceImpl(CommentRepository commentRepository, PostService postService, CacheService cacheService, Producer producer) {
        this.commentRepository = commentRepository;
        this.postService = postService;
        this.cacheService = cacheService;
        this.producer = producer;
    }

    @Override
    @Transactional
    public Set<Comment> findCommentsForPostByID(Long postID) {
        log.info("Finding comments for post with ID = " + postID);
        Post post = postService.findByID(postID);
        Set<Comment> comments = post.getComments();
        if(comments.size() == 0) {
            String message = "There are no comments for this post.";
            producer.send(message);
            throw new NotFoundException(message);
        }
        return comments;
    }

    @Override
    @Transactional
    public Comment findByID(Long id) {
        log.info("Finding comment with ID = " + id);
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isEmpty()) {
            String message = "Comment with ID = " + id + " doesn't exist.";
            producer.send(message);
            throw new NotFoundException(message);
        }
        return commentOptional.get();
    }

    @Override
    @Transactional
    public Comment save(Comment comment) {
        log.info("Saving new comment..");
        cacheService.remove(COMMENTS_FOR_POST + comment.getPost().getId());
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
                COMMENTS_FOR_POST + comment.getPost().getId());
        commentRepository.delete(comment);
        return comment;
    }
}
