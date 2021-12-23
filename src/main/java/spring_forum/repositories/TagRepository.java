package spring_forum.repositories;

import org.springframework.data.repository.CrudRepository;
import spring_forum.domain.Tag;

public interface TagRepository extends CrudRepository<Tag, Long> {
}
