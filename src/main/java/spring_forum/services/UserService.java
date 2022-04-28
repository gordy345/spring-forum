package spring_forum.services;

import spring_forum.domain.User;

import java.util.Set;

public interface UserService extends CrudService<User, Long> {

    Set<User> findAll();

    User findUserByName(String name);

    String uploadAvatar(Long id);

}
