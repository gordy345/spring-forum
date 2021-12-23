package spring_forum.repositories;

import org.springframework.data.repository.CrudRepository;
import spring_forum.domain.Post;

public interface PostRepository extends CrudRepository<Post, Long> {
}
