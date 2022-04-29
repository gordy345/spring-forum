package spring_forum.services;

import spring_forum.domain.User;

import java.util.Set;

public interface UserService extends CrudService<User, Long> {

    Set<User> findAll();

    User findUserByEmail(String email);

    String uploadAvatar(Long id);

    void enableUser(String token);

    void enableUser(Long id);

    void disableUser(Long id);
}
