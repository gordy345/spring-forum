package spring_forum.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring_forum.domain.Post;
import spring_forum.domain.Tag;
import spring_forum.exceptions.ExistsException;
import spring_forum.exceptions.NotFoundException;
import spring_forum.rabbitMQ.Producer;
import spring_forum.repositories.TagRepository;

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
class TagServiceImplTests {

    @Mock
    private TagRepository tagRepository;

    @Mock
    private PostService postService;

    @Mock
    private CacheService cacheService;

    @Mock
    private Producer producer;

    private TagService tagService;

    private final Tag tag = Tag.builder().id(1L).tag("Test")
            .post(Post.builder().id(1L).build()).build();

    @BeforeEach
    void setUp() {
        tagService = new TagServiceImpl(tagRepository, postService, cacheService, producer);
    }

    @Test
    void findTagsForPostByID() {
        Set<Tag> tagsForPost = new HashSet<>();
        tagsForPost.add(tag);
        Post postToReturn = Post.builder().id(1L).tags(tagsForPost).build();
        when(postService.findByID(anyLong())).thenReturn(postToReturn);
        Set<Tag> tags = tagService.findTagsForPostByID(1L);
        assertEquals(1, tags.size());
        assertEquals(1L, tags.iterator().next().getId());
        verify(postService).findByID(anyLong());
    }

    @Test
    void findByID() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(tag));
        Tag receivedTag = tagService.findByID(1L);
        assertEquals(1L, receivedTag.getId());
        assertEquals("Test", receivedTag.getTag());
        verify(tagRepository).findById(anyLong());
    }

    @Test
    void save() {
        when(tagRepository.save(any())).thenReturn(tag);
        Tag savedTag = tagService.save(tag);
        assertEquals(1L, savedTag.getId());
        assertEquals("Test", savedTag.getTag());
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void update() {
        when(tagRepository.findById(anyLong())).thenReturn(
                Optional.of(Tag.builder().id(1L).post(tag.getPost()).build()));
        Tag updatedTag = tagService.update(tag);
        assertEquals(1L, updatedTag.getId());
        assertEquals("Test", updatedTag.getTag());
        assertEquals(1L, updatedTag.getPost().getId());
        verify(tagRepository).findById(anyLong());
    }

    @Test
    void deleteByID() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(tag));
        tagService.deleteByID(1L);
        verify(tagRepository).findById(anyLong());
        verify(tagRepository).delete(any(Tag.class));
    }

    @Test
    void findTagsForPostWithError1() {
        when(postService.findByID(anyLong())).thenThrow(new NotFoundException("There is no post with ID = -1"));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> tagService.findTagsForPostByID(-1L));
        assertEquals("There is no post with ID = -1", exception.getMessage());
        verify(postService).findByID(anyLong());
    }

    @Test
    void findTagsForPostWithError2() {
        when(postService.findByID(anyLong())).thenReturn(Post.builder().build());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> tagService.findTagsForPostByID(1L));
        assertEquals("There are no tags for this post.", exception.getMessage());
        verify(postService).findByID(anyLong());
    }

    @Test
    void findByIDWithError() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> tagService.findByID(-1L));
        assertEquals("Tag with ID = -1 doesn't exist.", exception.getMessage());
        verify(tagRepository).findById(anyLong());
    }

    @Test
    void saveWithError() {
        Set<Tag> tagsForPost = Set.of(Tag.builder().tag("tag").build());
        Post relatedPost = Post.builder().id(1L).tags(tagsForPost).build();
        Tag tagToSave = Tag.builder().tag("tag").post(relatedPost).build();
        ExistsException exception = assertThrows(ExistsException.class,
                () -> tagService.save(tagToSave));
        assertEquals("Tag you're trying to save already exists for post with ID = "
                + relatedPost.getId(), exception.getMessage());
    }

    @Test
    void updateWithError1() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> tagService.update(tag));
        assertEquals("Tag with ID = " + tag.getId() + " doesn't exist.", exception.getMessage());
        verify(tagRepository).findById(anyLong());
    }

    @Test
    void updateWithError2() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(Tag.builder().build()));
        Set<Tag> tagsForPost = Set.of(Tag.builder().tag("tag").build());
        Post relatedPost = Post.builder().id(1L).tags(tagsForPost).build();
        Tag tagToUpdate = Tag.builder().id(1L).tag("tag").post(relatedPost).build();
        ExistsException exception = assertThrows(ExistsException.class,
                () -> tagService.update(tagToUpdate));
        assertEquals("Tag you're trying to save already exists for post with ID = "
                + relatedPost.getId(), exception.getMessage());
        verify(tagRepository).findById(anyLong());
    }

    @Test
    void deleteWithError() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> tagService.deleteByID(-1L));
        assertEquals("Tag with ID = -1 doesn't exist.", exception.getMessage());
        verify(tagRepository).findById(anyLong());
    }
}