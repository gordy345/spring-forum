package spring_forum.repositories;

import org.springframework.data.repository.CrudRepository;
import spring_forum.domain.User;

public interface UserRepository extends CrudRepository<User, Long> {
}
