package spring_forum.controllers;

import org.springframework.web.bind.annotation.*;
import spring_forum.converters.TagConverter;
import spring_forum.domain.Post;
import spring_forum.domain.Tag;
import spring_forum.dtos.TagDTO;
import spring_forum.services.CacheService;
import spring_forum.services.PostService;
import spring_forum.services.TagService;
import spring_forum.utils.Utils;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

import static spring_forum.utils.CacheKeys.TAGS_FOR_POST;
import static spring_forum.utils.CacheKeys.TAG_BY_ID;

@RestController
@RequestMapping("/tags")
public class TagController {

    private final TagService tagService;
    private final TagConverter tagConverter;
    private final PostService postService;
    private final CacheService cacheService;

    public TagController(TagService tagService, TagConverter tagConverter, PostService postService, CacheService cacheService) {
        this.tagService = tagService;
        this.tagConverter = tagConverter;
        this.postService = postService;
        this.cacheService = cacheService;
    }

    @GetMapping("/post/{id}")
    public String showTagsForPostByID(@PathVariable Long id) {
        String cacheKey = TAGS_FOR_POST + id;
        if (cacheService.containsKey(cacheKey)) {
            return cacheService.get(cacheKey);
        }
        Set<TagDTO> tagDTOS = tagService.findTagsForPostByID(id).stream()
                .map(tagConverter::convertToTagDTO)
                .collect(Collectors.toSet());
        String jsonResult = Utils.convertToJson(tagDTOS);
        cacheService.put(cacheKey, jsonResult);
        return jsonResult;
    }

    @GetMapping("/{id}")
    public String findTagByID(@PathVariable Long id) {
        String cacheKey = TAG_BY_ID + id;
        if (cacheService.containsKey(cacheKey)) {
            return cacheService.get(cacheKey);
        }
        TagDTO tagDTO = tagConverter.convertToTagDTO(tagService.findByID(id));
        String jsonResult = Utils.convertToJson(tagDTO);
        cacheService.put(cacheKey, jsonResult);
        return jsonResult;
    }

    @PostMapping
    public TagDTO saveTag(@Valid @RequestBody TagDTO tagDTO) {
        Post postByID = postService.findByID(tagDTO.getPostID());
        Tag tagToSave = tagConverter.convertToTag(tagDTO);
        tagToSave.setPost(postByID);
        Tag savedTag = tagService.save(tagToSave);
        tagDTO.setId(savedTag.getId());
        return tagDTO;
    }

    @PutMapping
    public TagDTO updateTag(@Valid @RequestBody TagDTO tagDTO) {
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
