package spring_forum.converters;

import org.springframework.stereotype.Component;
import spring_forum.domain.Post;
import spring_forum.dtos.PostDTO;

@Component
public class PostConverter {

    public PostDTO convertToPostDTO(Post post) {
        if (post == null) return null;
        return PostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .text(post.getText())
                .postOwnerID(post.getPostOwner().getId())
                .build();
    }

    public Post convertToPost(PostDTO postDTO) {
        if (postDTO == null) return null;
        return Post.builder()
                .id(postDTO.getId())
                .title(postDTO.getTitle())
                .text(postDTO.getText())
                .build();
    }
}
