package spring_forum.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spring_forum.domain.User;
import spring_forum.exceptions.ExistsException;
import spring_forum.exceptions.NotFoundException;
import spring_forum.rabbitMQ.Producer;
import spring_forum.repositories.UserRepository;

import javax.transaction.Transactional;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Producer producer;

    public UserServiceImpl(UserRepository userRepository, Producer producer) {
        this.userRepository = userRepository;
        this.producer = producer;
    }

    @Override
    @Transactional
    public Set<User> findAll() {
        log.info("Finding all users.");
        Set<User> users = new LinkedHashSet<>();
        userRepository.findAll().forEach(users::add);
        if (users.size() == 0) {
            String message = "There are no users now.";
            producer.send(message);
            throw new NotFoundException(message);
        }
        return users;
    }

    @Override
    @Transactional
    public User findUserByName(String name) {
        log.info("Finding user by name: " + name);
        Optional<User> userOptional = userRepository.findUserByName(name);
        if(userOptional.isEmpty()) {
            String message = "User \"" + name + "\" doesn't exist.";
            producer.send(message);
            throw new NotFoundException(message);
        }
        return userOptional.get();
    }

    @Override
    @Transactional
    public User findByID(Long id) {
        log.info("Finding user with ID = " + id);
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty()) {
            String message = "User with ID = " + id + " doesn't exist.";
            producer.send(message);
            throw new NotFoundException(message);
        }
        return userOptional.get();
    }

    @Override
    @Transactional
    public User save(User user) {
        log.info("Saving user with name: " + user.getName());
        if (userRepository.findUserByName(user.getName()).isPresent()) {
            throw new ExistsException("User with name \"" + user.getName() + "\" already exists.");
        }
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(User user) {
        log.info("Updating user with ID = " + user.getId());
        User userByID = findByID(user.getId());
        if (!userByID.getName().equals(user.getName()) &&
                userRepository.findUserByName(user.getName()).isPresent()) {
            throw new ExistsException("User with name \"" + user.getName() + "\" already exists.");
        }
        userByID.setName(user.getName());
        userByID.setEmail(user.getEmail());
        userByID.setGender(user.getGender());
        userByID.setModerator(user.isModerator());
        userByID.setPhoneNumber(user.getPhoneNumber());
        if (user.getCountry() != null) {
            userByID.setCountry(user.getCountry());
        }
        if (user.getLanguage() != null) {
            userByID.setLanguage(user.getLanguage());
        }
        return userByID;
    }

    @Override
    @Transactional
    public void deleteByID(Long id) {
        log.info("Deleting user with ID = " + id);
        User user = findByID(id);
        userRepository.delete(user);
    }
}
