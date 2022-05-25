package spring_forum.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import spring_forum.domain.User;
import spring_forum.domain.VerificationToken;
import spring_forum.exceptions.ExistsException;
import spring_forum.exceptions.NotFoundException;
import spring_forum.exceptions.TokenExpiredException;
import spring_forum.rabbitMQ.Producer;
import spring_forum.repositories.UserRepository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static spring_forum.utils.CacheKeys.*;
import static spring_forum.utils.ExceptionMessages.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final CacheService cacheService;
    private final VerificationTokenService verificationTokenService;
    private final Producer producer;

    @Override
    @Transactional
    public Set<User> findAll() {
        log.info("Finding all users.");
        Set<User> users = new LinkedHashSet<>();
        userRepository.findAll().forEach(users::add);
        if (users.size() == 0) {
            producer.send(NO_USERS);
            throw new NotFoundException(NO_USERS);
        }
        return users;
    }

    @Override
    @Transactional
    public User findUserByEmail(String email) {
        log.info("Finding user by email: " + email);
        Optional<User> userOptional = userRepository.findUserByEmail(email);
        if (userOptional.isEmpty()) {
            String message = USER_NOT_FOUND_BY_EMAIL + email;
            producer.send(message);
            throw new NotFoundException(message);
        }
        return userOptional.get();
    }

    @Override
    @Transactional
    public String uploadAvatar(Long id) {
        log.info("Uploading avatar for user with ID = " + id);
        User user = findByID(id);
        String url = user.getImageUrl();
        if (url == null) {
            url = "https://webdav.yandex.ru/avatars/"
                    + RandomStringUtils.random(10, true, true) + ".jpeg";
            user.setImageUrl(url);
        }
        return url;
    }

    @Override
    @Transactional
    public void enableUser(String token) {
        VerificationToken foundToken
                = verificationTokenService.findTokenByValue(token);
        User user = foundToken.getUser();
        verificationTokenService.deleteTokenByValue(token);
        log.info("Enabling user with ID = " + user.getId());
        if (foundToken.getExpiryDate().before(new Date())) {
            throw new TokenExpiredException(TOKEN_EXPIRED);
        }
        user.setEnabled(true);
        cacheService.remove(ALL_USERS, USER_BY_ID + user.getId(),
                USER_BY_EMAIL + user.getEmail());
    }

    @Override
    @Transactional
    public void enableUser(Long id) {
        log.info("Enabling user with ID = " + id);
        User user = findByID(id);
        user.setEnabled(true);
        cacheService.remove(ALL_USERS, USER_BY_ID + user.getId(),
                USER_BY_EMAIL + user.getEmail());
    }

    @Override
    @Transactional
    public void disableUser(Long id) {
        log.info("Disabling user with ID = " + id);
        User user = findByID(id);
        user.setEnabled(false);
        cacheService.remove(ALL_USERS, USER_BY_ID + user.getId(),
                USER_BY_EMAIL + user.getEmail());
    }

    @Override
    @Transactional
    public User findByID(Long id) {
        log.info("Finding user with ID = " + id);
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isEmpty()) {
            String message = USER_NOT_FOUND_BY_ID + id;
            producer.send(message);
            throw new NotFoundException(message);
        }
        return userOptional.get();
    }

    @Override
    @Transactional
    public User save(User user) {
        log.info("Saving user with email: " + user.getEmail());
        if (userRepository.findUserByEmail(user.getEmail()).isPresent()) {
            throw new ExistsException(USER_EXISTS_WITH_EMAIL + user.getEmail());
        }
        cacheService.remove(ALL_USERS);
        User savedUser = userRepository.save(user);
        String token = verificationTokenService.createNewTokenForUser(savedUser);
        emailService.sendConfirmationEmail(user.getEmail(), token);
        return savedUser;
    }

    @Override
    @Transactional
    public User update(User user) {
        log.info("Updating user with ID = " + user.getId());
        User userByID = findByID(user.getId());
        if (!userByID.getEmail().equals(user.getEmail()) &&
                userRepository.findUserByEmail(user.getEmail()).isPresent()) {
            throw new ExistsException(USER_EXISTS_WITH_EMAIL + user.getEmail());
        }
        cacheService.remove(ALL_USERS, USER_BY_ID + user.getId(),
                USER_BY_EMAIL + userByID.getEmail());
        userByID.setName(user.getName());
        userByID.setEmail(user.getEmail());
        userByID.setGender(user.getGender());
        userByID.setModerator(user.isModerator());
        userByID.setPhoneNumber(user.getPhoneNumber());
        userByID.setRating(user.getRating());
        userByID.setNameColor(user.getNameColor());
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
    public User deleteByID(Long id) {
        log.info("Deleting user with ID = " + id);
        User user = findByID(id);
        cacheService.remove(ALL_USERS, USER_BY_ID + id,
                USER_BY_EMAIL + user.getEmail(), AVATAR_FOR_USER + id,
                POSTS_FOR_USER + id);
        user.setEnabled(false);
        return user;
    }
}
