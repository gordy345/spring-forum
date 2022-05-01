package spring_forum.controllers;

import org.springframework.web.bind.annotation.*;
import spring_forum.converters.CommentConverter;
import spring_forum.domain.Comment;
import spring_forum.domain.Post;
import spring_forum.domain.User;
import spring_forum.dtos.CommentDTO;
import spring_forum.services.CacheService;
import spring_forum.services.CommentService;
import spring_forum.services.PostService;
import spring_forum.services.UserService;
import spring_forum.utils.Utils;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

import static spring_forum.utils.CacheKeys.*;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentConverter commentConverter;
    private final UserService userService;
    private final PostService postService;
    private final CacheService cacheService;

    public CommentController(CommentService commentService, CommentConverter commentConverter, UserService userService, PostService postService, CacheService cacheService) {
        this.commentService = commentService;
        this.commentConverter = commentConverter;
        this.userService = userService;
        this.postService = postService;
        this.cacheService = cacheService;
    }

    @GetMapping("/post/{id}")
    public String showCommentsForPostByID(@PathVariable Long id) {
        String cacheKey = COMMENTS_FOR_POST + id;
        if (cacheService.containsKey(cacheKey)) {
            return cacheService.get(cacheKey);
        }
        Set<CommentDTO> commentDTOS =
                commentService.findCommentsForPostByID(id).stream()
                        .map(commentConverter::convertToCommentDTO)
                        .collect(Collectors.toSet());
        String jsonResult = Utils.convertToJson(commentDTOS);
        cacheService.put(cacheKey, jsonResult);
        return jsonResult;
    }

    @GetMapping("/{id}")
    public String findCommentByID(@PathVariable Long id) {
        String cacheKey = COMMENT_BY_ID + id;
        if (cacheService.containsKey(cacheKey)) {
            return cacheService.get(cacheKey);
        }
        CommentDTO commentDTO =
                commentConverter.convertToCommentDTO(commentService.findByID(id));
        String jsonResult = Utils.convertToJson(commentDTO);
        cacheService.put(cacheKey, jsonResult);
        return jsonResult;
    }

    @GetMapping("/count/{postId}")
    public String countCommentsByPostID(@PathVariable Long postId) {
        String cacheKey = COMMENTS_COUNT_FOR_POST + postId;
        if (cacheService.containsKey(cacheKey)) {
            return cacheService.get(cacheKey);
        }
        String result = String.valueOf(commentService.countCommentsByPostId(postId));
        cacheService.put(cacheKey, result);
        return result;
    }

    @PostMapping
    public CommentDTO saveComment(@Valid @RequestBody CommentDTO commentDTO) {
        User commentOwner = userService.findByID(commentDTO.getCommentOwnerID());
        Post post = postService.findByID(commentDTO.getPostID());
        Comment commentToSave = commentConverter.convertToComment(commentDTO);
        commentToSave.setCommentOwner(commentOwner);
        commentToSave.setPost(post);
        Comment savedComment = commentService.save(commentToSave);
        commentDTO.setId(savedComment.getId());
        return commentDTO;
    }

    @PutMapping
    public CommentDTO updateComment(@Valid @RequestBody CommentDTO commentDTO) {
        User commentOwner = userService.findByID(commentDTO.getCommentOwnerID());
        Post post = postService.findByID(commentDTO.getPostID());
        Comment commentToUpdate = commentConverter.convertToComment(commentDTO);
        commentToUpdate.setCommentOwner(commentOwner);
        commentToUpdate.setPost(post);
        commentService.update(commentToUpdate);
        return commentDTO;
    }

    @DeleteMapping("/{id}")
    public String deleteComment(@PathVariable Long id) {
        commentService.deleteByID(id);
        return "Comment with ID = " + id + " was deleted.";
    }
}
