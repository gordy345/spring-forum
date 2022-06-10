package spring_forum.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring_forum.domain.Tag;
import spring_forum.exceptions.ExistsException;
import spring_forum.exceptions.NotFoundException;
import spring_forum.repositories.TagRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
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

    private TagService tagService;

    @BeforeEach
    void setUp() {
        tagService = new TagServiceImpl(tagRepository, postService, cacheService);
    }

    @Test
    void findTagByValue() {
        when(tagRepository.findTagByTag(anyString())).thenReturn(TAG);
        Tag foundTag = tagService.findTagByValue(TAG.getTag());
        assertEquals(foundTag.getTag(), TAG.getTag());
        assertEquals(foundTag.getId(), TAG.getId());
        verify(tagRepository).findTagByTag(anyString());
    }

    @Test
    void findTagsForPostByID() {
        Set<Tag> tagsForPost = new HashSet<>();
        tagsForPost.add(TAG);
        POST.setTags(tagsForPost);
        when(postService.findByID(anyLong())).thenReturn(POST);
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
        assertEquals(TAG.getTag(), receivedTag.getTag());
        verify(tagRepository).findById(anyLong());
    }

    @Test
    void save() {
        when(tagRepository.save(any())).thenReturn(TAG);
        POST.getTags().clear();
        Tag savedTag = tagService.save(TAG);
        assertEquals(1L, savedTag.getId());
        assertEquals(TAG.getTag(), savedTag.getTag());
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void update() {
        when(tagRepository.findById(anyLong())).thenReturn(
                Optional.of(Tag.builder().id(1L).posts(TAG.getPosts()).build()));
        POST.getTags().clear();
        Tag updatedTag = tagService.update(TAG);
        assertEquals(1L, updatedTag.getId());
        assertEquals(TAG.getTag(), updatedTag.getTag());
        assertEquals(1L, updatedTag.getPosts().iterator().next().getId());
        verify(tagRepository).findById(anyLong());
    }

    @Test
    void deleteTagForPost() {
        POST.getTags().add(TAG);
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(TAG));
        when(postService.findByID(anyLong())).thenReturn(POST);
        tagService.deleteTagForPost(TAG.getId(), POST.getId());
        assertEquals(0, POST.getTags().size());
        verify(tagRepository).findById(anyLong());
        verify(tagRepository).deleteById(anyLong());
    }

    @Test
    void deleteByID() {
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(TAG));
        tagService.deleteByID(1L);
        verify(tagRepository).findById(anyLong());
        verify(tagRepository).delete(any(Tag.class));
    }

    @Test
    void deleteAll() {
        tagService.deleteAll(Collections.EMPTY_LIST);
        verify(tagRepository).deleteAll(any());
    }

    @Test
    void findTagByValueWithError() {
        when(tagRepository.findTagByTag(anyString())).thenReturn(null);
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> tagService.findTagByValue(TAG.getTag()));
        assertEquals(TAG_NOT_FOUND_BY_VALUE + TAG.getTag(), exception.getMessage());
        verify(tagRepository).findTagByTag(anyString());
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
        when(postService.findByID(anyLong())).thenReturn(POST_EMPTY);
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
        POST.getTags().add(TAG);
        ExistsException exception = assertThrows(ExistsException.class,
                () -> tagService.save(TAG));
        assertEquals(TAG_EXISTS_FOR_POST + POST.getId(), exception.getMessage());
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
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(TAG_EMPTY));
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> tagService.update(TAG));
        assertEquals(POST_DOESNT_CONTAIN_TAG + POST.getId(), exception.getMessage());
        verify(tagRepository).findById(anyLong());
    }

    @Test
    void updateWithError3() {
        POST.getTags().add(TAG);
        when(tagRepository.findById(anyLong())).thenReturn(Optional.of(TAG));
        ExistsException exception = assertThrows(ExistsException.class,
                () -> tagService.update(TAG));
        assertEquals(TAG_EXISTS_FOR_POST + POST.getId(), exception.getMessage());
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