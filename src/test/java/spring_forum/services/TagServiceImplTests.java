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

import java.util.Collections;
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

    @BeforeEach
    void setUp() {
        tagService = new TagServiceImpl(tagRepository, postService, cacheService, producer);
    }

    @Test
    void findTagsForPostByID() {
        Set<Tag> tagsForPost = new HashSet<>();
        tagsForPost.add(TAG);
        Post postToReturn = Post.builder().id(1L).tags(tagsForPost).build();
        when(postService.findByID(anyLong())).thenReturn(postToReturn);
        Set<Tag> tags = tagService.findTagsForPostByID(1L);
        assertEquals(1, tags.size());
        assertEquals(1L, tags.iterator().next().getId());
        verify(postService).findByID(anyLong());
    }

    @Test
    void findByID() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(TAG));
        Tag receivedTag = tagService.findByID(1L);
        assertEquals(1L, receivedTag.getId());
        assertEquals("Test", receivedTag.getTag());
        verify(tagRepository).findById(anyLong());
    }

    @Test
    void save() {
        when(tagRepository.save(any())).thenReturn(TAG);
        Tag savedTag = tagService.save(TAG);
        assertEquals(1L, savedTag.getId());
        assertEquals("Test", savedTag.getTag());
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void update() {
        when(tagRepository.findById(anyLong())).thenReturn(
                Optional.of(Tag.builder().id(1L).posts(TAG.getPosts()).build()));
        Tag updatedTag = tagService.update(TAG);
        assertEquals(1L, updatedTag.getId());
        assertEquals("Test", updatedTag.getTag());
        assertEquals(1L, updatedTag.getPosts().iterator().next().getId());
        verify(tagRepository).findById(anyLong());
    }

    @Test
    void deleteByID() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(TAG));
        tagService.deleteByID(1L);
        verify(tagRepository).findById(anyLong());
        verify(tagRepository).delete(any(Tag.class));
    }

    @Test
    void findTagsForPostWithError1() {
        when(postService.findByID(anyLong())).thenThrow(new NotFoundException(POST_NOT_FOUND_BY_ID + NEGATIVE_ID));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> tagService.findTagsForPostByID(NEGATIVE_ID));
        assertEquals(POST_NOT_FOUND_BY_ID + NEGATIVE_ID, exception.getMessage());
        verify(postService).findByID(anyLong());
    }

    @Test
    void findTagsForPostWithError2() {
        when(postService.findByID(anyLong())).thenReturn(Post.builder().build());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> tagService.findTagsForPostByID(POST.getId()));
        assertEquals(NO_TAGS_FOR_POST + POST.getId(), exception.getMessage());
        verify(postService).findByID(anyLong());
    }

    @Test
    void findByIDWithError() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> tagService.findByID(NEGATIVE_ID));
        assertEquals(TAG_NOT_FOUND_BY_ID + NEGATIVE_ID, exception.getMessage());
        verify(tagRepository).findById(anyLong());
    }

    @Test
    void saveWithError() {
        Set<Tag> tagsForPost = Set.of(Tag.builder().tag("tag").build());
        Post relatedPost = Post.builder().id(1L).tags(tagsForPost).build();
        Tag tagToSave = Tag.builder().tag("tag").posts(Collections.singleton(relatedPost)).build();
        ExistsException exception = assertThrows(ExistsException.class,
                () -> tagService.save(tagToSave));
        assertEquals(TAG_EXISTS_FOR_POST + relatedPost.getId(), exception.getMessage());
    }

    @Test
    void updateWithError1() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> tagService.update(TAG));
        assertEquals(TAG_NOT_FOUND_BY_ID + TAG.getId(), exception.getMessage());
        verify(tagRepository).findById(anyLong());
    }

    @Test
    void updateWithError2() {
        Set<Tag> tagsForPost = Set.of(Tag.builder().tag("tag").build());
        Post relatedPost = Post.builder().id(1L).tags(tagsForPost).build();
        when(tagRepository.findById(anyLong())).thenReturn
                (Optional.of(Tag.builder().posts(Collections.singleton(relatedPost)).build()));
        Tag tagToUpdate = Tag.builder().id(1L).tag("tag").posts(Collections.singleton(relatedPost)).build();
        ExistsException exception = assertThrows(ExistsException.class,
                () -> tagService.update(tagToUpdate));
        assertEquals(TAG_EXISTS_FOR_POST + relatedPost.getId(), exception.getMessage());
        verify(tagRepository).findById(anyLong());
    }

    @Test
    void deleteWithError() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> tagService.deleteByID(NEGATIVE_ID));
        assertEquals(TAG_NOT_FOUND_BY_ID + NEGATIVE_ID, exception.getMessage());
        verify(tagRepository).findById(anyLong());
    }
}