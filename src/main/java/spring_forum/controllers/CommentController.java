package spring_forum.controllers;

import org.springframework.web.bind.annotation.*;
import spring_forum.converters.CommentConverter;
import spring_forum.domain.Comment;
import spring_forum.domain.Post;
import spring_forum.domain.User;
import spring_forum.dtos.CommentDTO;
import spring_forum.services.CommentService;
import spring_forum.services.PostService;
import spring_forum.services.UserService;

import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final CommentConverter commentConverter;
    private final UserService userService;
    private final PostService postService;

    public CommentController(CommentService commentService, CommentConverter commentConverter, UserService userService, PostService postService) {
        this.commentService = commentService;
        this.commentConverter = commentConverter;
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/post/{id}")
    public Set<CommentDTO> showCommentsForPostByID(@PathVariable Long id) {
        return commentService.findCommentsForPostByID(id).stream()
                .map(commentConverter::convertToCommentDTO)
                .collect(Collectors.toSet());
    }

    @GetMapping("/{id}")
    public CommentDTO findCommentByID(@PathVariable Long id) {
        return commentConverter.convertToCommentDTO(commentService.findByID(id));
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
