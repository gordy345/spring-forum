package spring_forum.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring_forum.domain.Post;
import spring_forum.exceptions.ExistsException;
import spring_forum.exceptions.NotFoundException;
import spring_forum.repositories.PostRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static spring_forum.TestConstants.*;
import static spring_forum.utils.ExceptionMessages.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTests {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserService userService;

    @Mock
    private TagService tagService;

    @Mock
    private CacheService cacheService;

    private PostService postService;

    @BeforeEach
    void setUp() {
        postService = new PostServiceImpl(postRepository, userService, tagService, cacheService);
    }

    @Test
    void findAll() {
        Post post2 = Post.builder().id(2L).build();
        List<Post> postsToReturn = new ArrayList<>();
        postsToReturn.add(POST);
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
        postsForUser.add(POST);
        USER.setPosts(postsForUser);
        when(userService.findByID(anyLong())).thenReturn(USER);
        Set<Post> posts = postService.findPostsForUserByID(1L);
        assertEquals(1, posts.size());
        assertEquals(1L, posts.iterator().next().getId());
        verify(userService).findByID(anyLong());
    }

    @Test
    void findPostsByTag() {
        when(tagService.findTagByValue(anyString())).thenReturn(TAG);
        Set<Post> postsByTag = postService.findPostsByTag(TAG.getTag());
        assertEquals(1, postsByTag.size());
        assertEquals(1L, postsByTag.iterator().next().getId());
        verify(tagService).findTagByValue(anyString());
    }

    @Test
    void findPostByTitle() {
        when(postRepository.findPostByTitle(anyString())).thenReturn(Optional.of(POST));
        Post postByTitle = postService.findPostByTitle(PLUG);
        assertEquals(1L, postByTitle.getId());
        assertEquals(POST.getTitle(), postByTitle.getTitle());
        assertEquals(POST.getText(), postByTitle.getText());
        verify(postRepository).findPostByTitle(anyString());
    }

    @Test
    void findByID() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(POST));
        Post receivedPost = postService.findByID(1L);
        assertEquals(1L, receivedPost.getId());
        assertEquals(POST.getTitle(), receivedPost.getTitle());
        assertEquals(POST.getText(), receivedPost.getText());
        verify(postRepository).findById(anyLong());
    }

    @Test
    void save() {
        when(postRepository.findPostByTitle(anyString())).thenReturn(Optional.empty());
        when(postRepository.save(any())).thenReturn(POST);
        Post savedPost = postService.save(POST);
        assertEquals(1L, savedPost.getId());
        assertEquals(POST.getTitle(), savedPost.getTitle());
        assertEquals(POST.getText(), savedPost.getText());
        verify(postRepository).findPostByTitle(anyString());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void update() {
        when(postRepository.findById(anyLong())).thenReturn(
                Optional.of(Post.builder().id(1L)
                        .title("Test").postOwner(POST.getPostOwner()).build()));
        Post updatedPost = postService.update(POST);
        assertEquals(1L, updatedPost.getId());
        assertEquals(POST.getTitle(), updatedPost.getTitle());
        assertEquals(POST.getText(), updatedPost.getText());
        verify(postRepository).findById(anyLong());
    }

    @Test
    void deleteByID() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(POST));
        postService.deleteByID(1L);
        verify(postRepository).findById(anyLong());
        verify(postRepository).delete(any(Post.class));
    }

    @Test
    void findAllWithError() {
        when(postRepository.findAll()).thenReturn(new ArrayList<>());
        NotFoundException exception = assertThrows(NotFoundException.class, postService::findAll);
        assertEquals(NO_POSTS, exception.getMessage());
        verify(postRepository).findAll();
    }

    @Test
    void findPostsForUserByIDWithError1() {
        when(userService.findByID(anyLong())).thenThrow(
                new NotFoundException(USER_NOT_FOUND_BY_ID + NEGATIVE_ID));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> postService.findPostsForUserByID(NEGATIVE_ID));
        assertEquals(USER_NOT_FOUND_BY_ID + NEGATIVE_ID, exception.getMessage());
        verify(userService).findByID(anyLong());
    }

    @Test
    void findPostsForUserByIDWithError2() {
        USER.getPosts().clear();
        when(userService.findByID(anyLong())).thenReturn(USER);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> postService.findPostsForUserByID(USER.getId()));
        assertEquals(NO_POSTS_FOR_USER + USER.getId(), exception.getMessage());
        verify(userService).findByID(anyLong());
    }

    @Test
    void findPostsByTagWithError1() {
        when(tagService.findTagByValue(anyString())).thenReturn(TAG_EMPTY);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> postService.findPostsByTag(PLUG));
        assertEquals(NO_POSTS_WITH_TAG + PLUG, exception.getMessage());
        verify(tagService).findTagByValue(anyString());
    }

    @Test
    void findPostByTitleWithError() {
        when(postRepository.findPostByTitle(anyString())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> postService.findPostByTitle(PLUG));
        assertEquals(POST_NOT_FOUND_BY_TITLE + PLUG, exception.getMessage());
        verify(postRepository).findPostByTitle(anyString());
    }

    @Test
    void findByIDWithError() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> postService.findByID(NEGATIVE_ID));
        assertEquals(POST_NOT_FOUND_BY_ID + NEGATIVE_ID, exception.getMessage());
        verify(postRepository).findById(anyLong());
    }

    @Test
    void saveWithError() {
        when(postRepository.findPostByTitle(anyString())).thenReturn(Optional.of(POST_EMPTY));
        ExistsException exception = assertThrows(ExistsException.class,
                () -> postService.save(POST));
        assertEquals(POST_EXISTS_WITH_TITLE + POST.getTitle(), exception.getMessage());
        verify(postRepository).findPostByTitle(anyString());
    }

    @Test
    void updateWithError1() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> postService.update(POST));
        assertEquals(POST_NOT_FOUND_BY_ID + POST.getId(), exception.getMessage());
        verify(postRepository).findById(anyLong());
    }

    @Test
    void updateWithError2() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(Post.builder().title("title").build()));
        when(postRepository.findPostByTitle(anyString())).thenReturn(Optional.of(POST_EMPTY));
        ExistsException exception = assertThrows(ExistsException.class,
                () -> postService.update(POST));
        assertEquals(POST_EXISTS_WITH_TITLE + POST.getTitle(), exception.getMessage());
        verify(postRepository).findById(anyLong());
        verify(postRepository).findPostByTitle(anyString());
    }

    @Test
    void deleteWithError() {
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> postService.deleteByID(NEGATIVE_ID));
        assertEquals(POST_NOT_FOUND_BY_ID + NEGATIVE_ID, exception.getMessage());
        verify(postRepository).findById(anyLong());
    }
}