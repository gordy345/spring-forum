package spring_forum.services;

import spring_forum.domain.Post;

import java.util.Set;

public interface PostService extends CrudService<Post, Long> {

    Set<Post> findAll();

    Set<Post> findPostsForUserByID(Long userId);

    Set<Post> findPostsByTag(String tag);

    Post findPostByTitle(String title);
    
}
