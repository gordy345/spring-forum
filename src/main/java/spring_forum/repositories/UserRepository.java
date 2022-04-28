package spring_forum.repositories;

import org.springframework.data.repository.CrudRepository;
import spring_forum.domain.User;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findUserByEmail(String email);

    Long countUsersById(Long id);
}
