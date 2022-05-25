package spring_forum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import spring_forum.converters.PostConverter;
import spring_forum.domain.Post;
import spring_forum.domain.User;
import spring_forum.dtos.PostDTO;
import spring_forum.services.CacheService;
import spring_forum.services.PostService;
import spring_forum.services.UserService;
import spring_forum.utils.Utils;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

import static spring_forum.utils.CacheKeys.*;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostConverter postConverter;
    private final UserService userService;
    private final CacheService cacheService;

    @GetMapping
    public String showAllPosts() {
        String cacheKey = ALL_POSTS;
        if (cacheService.containsKey(cacheKey)) {
            return cacheService.get(cacheKey);
        }
        Set<PostDTO> postDTOS = postService.findAll().stream()
                .map(postConverter::convertToPostDTO)
                .collect(Collectors.toSet());
        String jsonResult = Utils.convertToJson(postDTOS);
        cacheService.put(cacheKey, jsonResult);
        return jsonResult;
    }

    @GetMapping("/user/{id}")
    public String showPostsForUser(@PathVariable Long id) {
        String cacheKey = POSTS_FOR_USER + id;
        if (cacheService.containsKey(cacheKey)) {
            return cacheService.get(cacheKey);
        }
        Set<PostDTO> postDTOS = postService.findPostsForUserByID(id).stream()
                .map(postConverter::convertToPostDTO)
                .collect(Collectors.toSet());
        String jsonResult = Utils.convertToJson(postDTOS);
        cacheService.put(cacheKey, jsonResult);
        return jsonResult;
    }

    @GetMapping("/tag/{tag}")
    public String showPostsWithTag(@PathVariable String tag) {
        String cacheKey = POSTS_BY_TAG + tag;
        if (cacheService.containsKey(cacheKey)) {
            return cacheService.get(cacheKey);
        }
        Set<PostDTO> postDTOS = postService.findPostsByTag(tag).stream()
                .map(postConverter::convertToPostDTO)
                .collect(Collectors.toSet());
        String jsonResult = Utils.convertToJson(postDTOS);
        cacheService.put(cacheKey, jsonResult);
        return jsonResult;
    }

    @GetMapping("/{id}")
    public String findPostByID(@PathVariable Long id) {
        String cacheKey = POST_BY_ID + id;
        if (cacheService.containsKey(cacheKey)) {
            return cacheService.get(cacheKey);
        }
        PostDTO postDTO = postConverter.convertToPostDTO(postService.findByID(id));
        String jsonResult = Utils.convertToJson(postDTO);
        cacheService.put(cacheKey, jsonResult);
        return jsonResult;
    }

    @GetMapping("/title/{title}")
    public String findPostByTitle(@PathVariable String title) {
        String cacheKey = POST_BY_TITLE + title;
        if (cacheService.containsKey(cacheKey)) {
            return cacheService.get(cacheKey);
        }
        PostDTO postDTO =
                postConverter.convertToPostDTO(postService.findPostByTitle(title));
        String jsonResult = Utils.convertToJson(postDTO);
        cacheService.put(cacheKey, jsonResult);
        return jsonResult;
    }

    @PostMapping
    public PostDTO savePost(@Valid @RequestBody PostDTO postDTO) {
        User postOwner = userService.findByID(postDTO.getPostOwnerID());
        Post postToSave = postConverter.convertToPost(postDTO);
        postToSave.setPostOwner(postOwner);
        Post savedPost = postService.save(postToSave);
        postDTO.setId(savedPost.getId());
        return postDTO;
    }

    @PutMapping
    public PostDTO updatePost(@Valid @RequestBody PostDTO postDTO) {
        postService.update(postConverter.convertToPost(postDTO));
        return postDTO;
    }

    @DeleteMapping("/{id}")
    public String deletePost(@PathVariable Long id) {
        postService.deleteByID(id);
        return "Post with ID = " + id + " was deleted.";
    }
}
