package spring_forum.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring_forum.domain.Post;
import spring_forum.domain.Tag;
import spring_forum.domain.User;
import spring_forum.exceptions.ExistsException;
import spring_forum.exceptions.NotFoundException;
import spring_forum.rabbitMQ.Producer;
import spring_forum.repositories.PostRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTests {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @Mock
    private Producer producer;

    private PostService postService;

    private final Post post = Post.builder().id(1L).title("Test").text("Test").build();

    @BeforeEach
    void setUp() {
        postService = new PostServiceImpl(postRepository, userService, producer);
    }

    @Test
    void findAll() {
        Post post2 = Post.builder().id(2L).build();
        List<Post> postsToReturn = new ArrayList<>();
        postsToReturn.add(post);
        postsToReturn.add(post2);
        when(postRepository.findAll()).thenReturn(postsToReturn);
        Set<Post> posts = postService.findAll();
        Iterator<Post> postIterator = posts.iterator();
        assertEquals(2, posts.size());
        assertEquals(1L, postIterator.next().getId());
        assertEquals(2L, postIterator.next().getId());
        verify(postRepository).findAll();
    }

    @Test
    void findPostsForUserByID() {
        Set<Post> postsForUser = new HashSet<>();
        postsForUser.add(post);
        User userToReturn = User.builder().id(1L).posts(postsForUser).build();
        when(userService.findByID(anyLong())).thenReturn(userToReturn);
        Set<Post> posts = postService.findPostsForUserByID(1L);
        assertEquals(1, posts.size());
        assertEquals(1L, posts.iterator().next().getId());
        verify(userService).findByID(anyLong());
    }

    @Test
    void findPostsByTag() {
        Set<Post> postsToReturn = new HashSet<>();
        postsToReturn.add(post);
        Set<Tag> tagsForPost = new HashSet<>();
        tagsForPost.add(Tag.builder().id(1L).tag("test").build());
        postsToReturn.add(Post.builder().id(2L).tags(tagsForPost).build());
        when(postRepository.findAll()).thenReturn(postsToReturn);
        Set<Post> postsByTag = postService.findPostsByTag("test");
        assertEquals(1, postsByTag.size());
        assertEquals(2L, postsByTag.iterator().next().getId());
        verify(postRepository).findAll();
    }

    @Test
    void findPostByTitle() {
        when(postRepository.findPostByTitle(anyString())).thenReturn(Optional.of(post));
        Post postByTitle = postService.findPostByTitle("Test");
        assertEquals(1L, postByTitle.getId());
        assertEquals("Test", postByTitle.getTitle());
        assertEquals("Test", postByTitle.getText());
        verify(postRepository).findPostByTitle(anyString());
    }

    @Test
    void findByID() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        Post receivedPost = postService.findByID(1L);
        assertEquals(1L, receivedPost.getId());
        assertEquals("Test", receivedPost.getTitle());
        assertEquals("Test", receivedPost.getText());
        verify(postRepository).findById(anyLong());
    }

    @Test
    void save() {
        when(postRepository.findPostByTitle(anyString())).thenReturn(Optional.empty());
        when(postRepository.save(any())).thenReturn(post);
        Post savedPost = postService.save(post);
        assertEquals(1L, savedPost.getId());
        assertEquals("Test", savedPost.getTitle());
        assertEquals("Test", savedPost.getText());
        verify(postRepository).findPostByTitle(anyString());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void update() {
        when(postRepository.findById(anyLong())).thenReturn(
                Optional.of(Post.builder().id(1L).title("Test").build()));
        Post updatedPost = postService.update(post);
        assertEquals(1L, updatedPost.getId());
        assertEquals("Test", updatedPost.getTitle());
        assertEquals("Test", updatedPost.getText());
        verify(postRepository).findById(anyLong());
    }

    @Test
    void deleteByID() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        postService.deleteByID(1L);
        verify(postRepository).findById(anyLong());
        verify(postRepository).delete(any(Post.class));
    }

    @Test
    void findAllWithError() {
        when(postRepository.findAll()).thenReturn(new ArrayList<>());
        NotFoundException exception = assertThrows(NotFoundException.class, postService::findAll);
        assertEquals("There are no posts now.", exception.getMessage());
        verify(postRepository).findAll();
    }

    @Test
    void findPostsForUserByIDWithError1() {
        when(userService.findByID(anyLong())).thenThrow(
                new NotFoundException("User with ID = -1 doesn't exist."));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> postService.findPostsForUserByID(-1L));
        assertEquals("User with ID = -1 doesn't exist.", exception.getMessage());
        verify(userService).findByID(anyLong());
    }

    @Test
    void findPostsForUserByIDWithError2() {
        when(userService.findByID(anyLong())).thenReturn(User.builder().posts(new HashSet<>()).build());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> postService.findPostsForUserByID(1L));
        assertEquals("There are no posts for this user.", exception.getMessage());
        verify(userService).findByID(anyLong());
    }

    @Test
    void findPostsByTagWithError() {
        List<Post> listToReturn = List.of(Post.builder().build());
        when(postRepository.findAll()).thenReturn(listToReturn);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> postService.findPostsByTag("test"));
        assertEquals("There are no posts with this tag.", exception.getMessage());
        verify(postRepository).findAll();
    }

    @Test
    void findPostByTitleWithError() {
        when(postRepository.findPostByTitle(anyString())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> postService.findPostByTitle("test"));
        assertEquals("Post with title \"test\" doesn't exist.", exception.getMessage());
        verify(postRepository).findPostByTitle(anyString());
    }

    @Test
    void findByIDWithError() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> postService.findByID(-1L));
        assertEquals("There is no post with ID = -1", exception.getMessage());
        verify(postRepository).findById(anyLong());
    }

    @Test
    void saveWithError() {
        when(postRepository.findPostByTitle(anyString())).thenReturn(Optional.of(Post.builder().build()));
        ExistsException exception = assertThrows(ExistsException.class,
                () -> postService.save(post));
        assertEquals("Post with title \"" + post.getTitle() + "\" already exists.", exception.getMessage());
        verify(postRepository).findPostByTitle(anyString());
    }

    @Test
    void updateWithError1() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> postService.update(post));
        assertEquals("There is no post with ID = 1", exception.getMessage());
        verify(postRepository).findById(anyLong());
    }

    @Test
    void updateWithError2() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(Post.builder().title("title").build()));
        when(postRepository.findPostByTitle(anyString())).thenReturn(Optional.of(Post.builder().build()));
        ExistsException exception = assertThrows(ExistsException.class,
                () -> postService.update(post));
        assertEquals("Post with title \"" + post.getTitle() + "\" already exists.", exception.getMessage());
        verify(postRepository).findById(anyLong());
        verify(postRepository).findPostByTitle(anyString());
    }

    @Test
    void deleteWithError() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> postService.deleteByID(-1L));
        assertEquals("There is no post with ID = -1", exception.getMessage());
        verify(postRepository).findById(anyLong());
    }
}