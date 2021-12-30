package spring_forum.controllers;

import org.springframework.web.bind.annotation.*;
import spring_forum.converters.PostConverter;
import spring_forum.domain.Post;
import spring_forum.domain.User;
import spring_forum.dtos.PostDTO;
import spring_forum.services.PostService;
import spring_forum.services.UserService;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final PostConverter postConverter;
    private final UserService userService;

    public PostController(PostService postService, PostConverter postConverter, UserService userService) {
        this.postService = postService;
        this.postConverter = postConverter;
        this.userService = userService;
    }

    @GetMapping
    public Set<PostDTO> showAllPosts() {
        return postService.findAll().stream()
                .map(postConverter::convertToPostDTO)
                .collect(Collectors.toSet());
    }

    @GetMapping("/user/{id}")
    public Set<PostDTO> showPostsForUser(@PathVariable Long id) {
        return postService.findPostsForUserByID(id).stream()
                .map(postConverter::convertToPostDTO)
                .collect(Collectors.toSet());
    }

    @GetMapping("/tag/{tag}")
    public Set<PostDTO> showPostsWithTag(@PathVariable String tag) {
        return postService.findPostsByTag(tag).stream()
                .map(postConverter::convertToPostDTO)
                .collect(Collectors.toSet());
    }

    @GetMapping("/{id}")
    public PostDTO findPostByID(@PathVariable Long id) {
        return postConverter.convertToPostDTO(postService.findByID(id));
    }

    @GetMapping("/title/{title}")
    public PostDTO findPostByTitle(@PathVariable String title) {
        return postConverter.convertToPostDTO(postService.findPostByTitle(title));
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
