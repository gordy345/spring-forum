package spring_forum.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring_forum.domain.Post;
import spring_forum.domain.Tag;
import spring_forum.repositories.TagRepository;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private TagService tagService;

    private final Tag tag = Tag.builder().id(1L).tag("Test").build();

    @BeforeEach
    void setUp() {
        tagService = new TagServiceImpl(tagRepository, postService);
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
        tag.setPost(Post.builder().id(1L).build());
        when(tagRepository.save(any())).thenReturn(tag);
        Tag savedTag = tagService.save(tag);
        assertEquals(1L, savedTag.getId());
        assertEquals("Test", savedTag.getTag());
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void update() {
        when(tagRepository.findById(anyLong())).thenReturn(
                Optional.of(Tag.builder().id(1L).build()));
        tag.setPost(Post.builder().id(1L).build());
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
}