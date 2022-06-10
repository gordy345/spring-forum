package spring_forum.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring_forum.domain.Comment;
import spring_forum.exceptions.NotFoundException;
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
import static spring_forum.TestConstants.*;
import static spring_forum.utils.ExceptionMessages.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTests {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostService postService;

    @Mock
    private CacheService cacheService;

    private CommentService commentService;

    @BeforeEach
    void setUp() {
        commentService = new CommentServiceImpl(commentRepository, postService, cacheService);
    }

    @Test
    void findCommentsForPostByID() {
        Set<Comment> commentsForPost = new HashSet<>();
        commentsForPost.add(COMMENT);
        POST.setComments(commentsForPost);
        when(postService.findByID(anyLong())).thenReturn(POST);
        Set<Comment> comments = commentService.findCommentsForPostByID(1L);
        assertEquals(1, comments.size());
        assertEquals(1L, comments.iterator().next().getId());
        verify(postService).findByID(anyLong());
    }

    @Test
    void countCommentsByPostId() {
        when(commentRepository.countCommentsByPostId(anyLong())).thenReturn(1L);
        Long resultCount = commentService.countCommentsByPostId(1L);
        assertEquals(1L, resultCount);
        verify(postService).findByID(anyLong());
        verify(commentRepository).countCommentsByPostId(anyLong());
    }

    @Test
    void findByID() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(COMMENT));
        Comment receivedComment = commentService.findByID(1L);
        assertEquals(1L, receivedComment.getId());
        assertEquals(COMMENT.getText(), receivedComment.getText());
        verify(commentRepository).findById(anyLong());
    }

    @Test
    void save() {
        when(commentRepository.save(any())).thenReturn(COMMENT);
        Comment savedComment = commentService.save(COMMENT);
        assertEquals(1L, savedComment.getId());
        assertEquals(COMMENT.getText(), savedComment.getText());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void update() {
        when(commentRepository.findById(anyLong())).thenReturn(
                Optional.of(COMMENT_EMPTY));
        Comment updatedComment = commentService.update(COMMENT);
        assertEquals(COMMENT.getText(), updatedComment.getText());
        assertEquals(USER.getId(), updatedComment.getCommentOwner().getId());
        verify(commentRepository).findById(anyLong());
    }

    @Test
    void deleteByID() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(COMMENT));
        commentService.deleteByID(1L);
        verify(commentRepository).findById(anyLong());
        verify(commentRepository).delete(any(Comment.class));
    }

    @Test
    void findCommentsForPostWithError1() {
        when(postService.findByID(anyLong())).thenThrow(new NotFoundException(POST_NOT_FOUND_BY_ID + NEGATIVE_ID));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.findCommentsForPostByID(NEGATIVE_ID));
        assertEquals(POST_NOT_FOUND_BY_ID + NEGATIVE_ID, exception.getMessage());
        verify(postService).findByID(anyLong());
    }

    @Test
    void findCommentsForPostWithError2() {
        when(postService.findByID(anyLong())).thenReturn(POST_EMPTY);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.findCommentsForPostByID(POST.getId()));
        assertEquals(NO_COMMENTS_FOR_POST + POST.getId(), exception.getMessage());
        verify(postService).findByID(anyLong());
    }

    @Test
    void findByIDWithError() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.findByID(NEGATIVE_ID));
        assertEquals(COMMENT_NOT_FOUND_BY_ID + NEGATIVE_ID, exception.getMessage());
        verify(commentRepository).findById(anyLong());
    }

    @Test
    void updateWithError() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.update(COMMENT));
        assertEquals(COMMENT_NOT_FOUND_BY_ID + COMMENT.getId(), exception.getMessage());
        verify(commentRepository).findById(anyLong());
    }

    @Test
    void deleteWithError() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.deleteByID(NEGATIVE_ID));
        assertEquals(COMMENT_NOT_FOUND_BY_ID + NEGATIVE_ID, exception.getMessage());
        verify(commentRepository).findById(anyLong());
    }
}