package spring_forum.repositories;

import org.springframework.data.repository.CrudRepository;
import spring_forum.domain.Post;

import java.util.Optional;
import java.util.Set;

public interface PostRepository extends CrudRepository<Post, Long> {

    Optional<Post> findPostByTitle(String title);

    Long countPostsByTitle(String title);

}
