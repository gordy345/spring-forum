package spring_forum.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spring_forum.domain.Comment;
import spring_forum.domain.Post;
import spring_forum.repositories.CommentRepository;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;

    public CommentServiceImpl(CommentRepository commentRepository, PostService postService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
    }

    @Override
    @Transactional
    public Set<Comment> findCommentsForPostByID(Long postID) {
        Post post = postService.findByID(postID);
        log.info("Finding comments for post with ID = " + postID);
        Set<Comment> comments = post.getComments();
        if(comments.size() == 0) {
            //todo add exception handling
            throw new RuntimeException();
        }
        comments.forEach(comment -> comment.setPost(post));
        return comments;
    }

    @Override
    @Transactional
    public Comment findByID(Long id) {
        Optional<Comment> commentOptional = commentRepository.findById(id);
        if (commentOptional.isEmpty()) {
            //todo add exception handling
            throw new RuntimeException();
        }
        Comment comment = commentOptional.get();
        comment.setPost(comment.getPost());
        return comment;
    }

    @Override
    @Transactional
    public Comment save(Comment comment) {
        log.info("Saving new comment..");
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
        return commentByID;
    }

    @Override
    @Transactional
    public void deleteByID(Long id) {
        log.info("Deleting comment with ID = " + id);
        Comment comment = findByID(id);
        commentRepository.delete(comment);
    }
}
