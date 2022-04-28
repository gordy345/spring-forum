package spring_forum.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring_forum.domain.Comment;
import spring_forum.domain.Post;
import spring_forum.domain.User;
import spring_forum.exceptions.NotFoundException;
import spring_forum.rabbitMQ.Producer;
import spring_forum.repositories.CommentRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Mock
    private CacheService cacheService;

    @Mock
    private Producer producer;

    private CommentService commentService;

    private final Comment comment = Comment.builder().id(1L).text("Test")
            .post(Post.builder().id(1L).build()).build();

    @BeforeEach
    void setUp() {
        commentService = new CommentServiceImpl(commentRepository, postService, cacheService, producer);
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

    @Test
    void findCommentsForPostWithError1() {
        when(postService.findByID(anyLong())).thenThrow(new NotFoundException("There is no post with ID = -1"));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.findCommentsForPostByID(-1L));
        assertEquals("There is no post with ID = -1", exception.getMessage());
        verify(postService).findByID(anyLong());
    }

    @Test
    void findCommentsForPostWithError2() {
        when(postService.findByID(anyLong())).thenReturn(Post.builder().build());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.findCommentsForPostByID(1L));
        assertEquals("There are no comments for this post.", exception.getMessage());
        verify(postService).findByID(anyLong());
    }

    @Test
    void findByIDWithError() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.findByID(-1L));
        assertEquals("Comment with ID = -1 doesn't exist.", exception.getMessage());
        verify(commentRepository).findById(anyLong());
    }

    @Test
    void updateWithError() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.update(comment));
        assertEquals("Comment with ID = " + comment.getId() + " doesn't exist.", exception.getMessage());
        verify(commentRepository).findById(anyLong());
    }

    @Test
    void deleteWithError() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.deleteByID(-1L));
        assertEquals("Comment with ID = -1 doesn't exist.", exception.getMessage());
        verify(commentRepository).findById(anyLong());
    }
}