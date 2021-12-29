package spring_forum.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring_forum.domain.Comment;
import spring_forum.domain.Post;
import spring_forum.domain.User;
import spring_forum.repositories.CommentRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTests {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    private CommentService commentService;

    private final Comment comment = Comment.builder().id(1L).text("Test").build();

    @BeforeEach
    void setUp() {
        commentService = new CommentServiceImpl(commentRepository, postService);
    }

    @Test
    void findCommentsForPostByID() {
        Set<Comment> commentsForPost = new HashSet<>();
        commentsForPost.add(comment);
        Post postToReturn = Post.builder().id(1L).comments(commentsForPost).build();
        when(postService.findByID(anyLong())).thenReturn(postToReturn);
        Set<Comment> comments = commentService.findCommentsForPostByID(1L);
        assertEquals(1, comments.size());
        assertEquals(1L, comments.iterator().next().getId());
        verify(postService).findByID(anyLong());
    }

    @Test
    void findByID() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        Comment receivedComment = commentService.findByID(1L);
        assertEquals(1L, receivedComment.getId());
        assertEquals("Test", receivedComment.getText());
        verify(commentRepository).findById(anyLong());
    }

    @Test
    void save() {
        when(commentRepository.save(any())).thenReturn(comment);
        Comment savedComment = commentService.save(comment);
        assertEquals(1L, savedComment.getId());
        assertEquals("Test", savedComment.getText());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void update() {
        when(commentRepository.findById(anyLong())).thenReturn(
                Optional.of(Comment.builder().id(1L).build()));
        comment.setCommentOwner(User.builder().id(1L).build());
        Comment updatedComment = commentService.update(comment);
        assertEquals(1L, updatedComment.getId());
        assertEquals("Test", updatedComment.getText());
        assertEquals(1L, updatedComment.getCommentOwner().getId());
        verify(commentRepository).findById(anyLong());
    }

    @Test
    void deleteByID() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        commentService.deleteByID(1L);
        verify(commentRepository).findById(anyLong());
        verify(commentRepository).delete(any(Comment.class));

    }
}