package spring_forum.controllers;

import org.springframework.web.bind.annotation.*;
import spring_forum.converters.TagConverter;
import spring_forum.domain.Post;
import spring_forum.domain.Tag;
import spring_forum.dtos.TagDTO;
import spring_forum.services.PostService;
import spring_forum.services.TagService;

import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;
    private final TagConverter tagConverter;
    private final PostService postService;

    public TagController(TagService tagService, TagConverter tagConverter, PostService postService) {
        this.tagService = tagService;
        this.tagConverter = tagConverter;
        this.postService = postService;
    }

    @GetMapping("/post/{id}")
    public Set<TagDTO> showTagsForPostByID(@PathVariable Long id) {
        return tagService.findTagsForPostByID(id).stream()
                .map(tagConverter::convertToTagDTO)
                .collect(Collectors.toSet());
    }

    @GetMapping("/{id}")
    public TagDTO findTagByID(@PathVariable Long id) {
        return tagConverter.convertToTagDTO(tagService.findByID(id));
    }

    @PostMapping
    public TagDTO saveTag(@RequestBody TagDTO tagDTO) {
        Post postByID = postService.findByID(tagDTO.getPostID());
        Tag tagToSave = tagConverter.convertToTag(tagDTO);
        tagToSave.setPost(postByID);
        Tag savedTag = tagService.save(tagToSave);
        tagDTO.setId(savedTag.getId());
        return tagDTO;
    }

    @PutMapping
    public TagDTO updateTag(@RequestBody TagDTO tagDTO) {
        Post postByID = postService.findByID(tagDTO.getPostID());
        Tag tagToUpdate = tagConverter.convertToTag(tagDTO);
        tagToUpdate.setPost(postByID);
        tagService.update(tagToUpdate);
        return tagDTO;
    }

    @DeleteMapping("/{id}")
    public String deleteTag(@PathVariable Long id) {
        tagService.deleteByID(id);
        return "Tag with ID = " + id + " was deleted.";
    }
}
