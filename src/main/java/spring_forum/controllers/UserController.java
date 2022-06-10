package spring_forum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import spring_forum.converters.UserConverter;
import spring_forum.domain.User;
import spring_forum.dtos.RegisterDTO;
import spring_forum.dtos.UserDTO;
import spring_forum.services.CacheService;
import spring_forum.services.UserService;
import spring_forum.utils.GeoUtils;
import spring_forum.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Set;
import java.util.stream.Collectors;

import static spring_forum.utils.CacheKeys.*;

@CrossOrigin
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final CacheService cacheService;
    private final UserConverter userConverter;

    @GetMapping
    public String showAllUsers() {
        String cacheKey = ALL_USERS;
        if (cacheService.containsKey(cacheKey)) {
            return cacheService.get(cacheKey);
        }
        Set<UserDTO> userDTOS = userService.findAll().stream()
                .map(userConverter::convertToUserDTO)
                .collect(Collectors.toSet());
        String jsonResult = Utils.convertToJson(userDTOS);
        cacheService.put(cacheKey, jsonResult);
        return jsonResult;
    }

    @GetMapping("/{id}")
    public String findUserByID(@PathVariable Long id) {
        String cacheKey = USER_BY_ID + id;
        if (cacheService.containsKey(cacheKey)) {
            return cacheService.get(cacheKey);
        }
        UserDTO userDTO = userConverter.convertToUserDTO(userService.findByID(id));
        String jsonResult = Utils.convertToJson(userDTO);
        cacheService.put(cacheKey, jsonResult);
        return jsonResult;
    }

    @GetMapping("/email/{email}")
    public String findUserByEmail(@PathVariable String email) {
        String cacheKey = USER_BY_EMAIL + email;
        if (cacheService.containsKey(cacheKey)) {
            return cacheService.get(cacheKey);
        }
        UserDTO userDTO =
                userConverter.convertToUserDTO(userService.findUserByEmail(email));
        String jsonResult = Utils.convertToJson(userDTO);
        cacheService.put(cacheKey, jsonResult);
        return jsonResult;
    }

    @PostMapping
    public UserDTO saveUser(@Valid @RequestBody RegisterDTO registerDTO, HttpServletRequest request) {
        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
            throw new ValidationException("Passwords don't match");
        }
        if (registerDTO.getCountry() == null) {
            registerDTO.setCountry(GeoUtils.getCountryByIp(request.getRemoteHost()));
        }
        if (registerDTO.getLanguage() == null) {
            registerDTO.setLanguage(GeoUtils.getLanguageByCountry(registerDTO.getCountry()));
        }
        User savedUser = userService.save(userConverter.convertToUser(registerDTO));
        return userConverter.convertToUserDTO(savedUser);
    }

    @PutMapping
    public UserDTO updateUser(@Valid @RequestBody UserDTO userDTO) {
        User updatedUser = userService.update(userConverter.convertToUser(userDTO));
        userDTO.setCountry(updatedUser.getCountry());
        userDTO.setLanguage(updatedUser.getLanguage());
        return userDTO;
    }

    @GetMapping("/rating/up/{id}/{amount}")
    public UserDTO upVoteRatingForUser(@PathVariable Long id, @PathVariable Long amount) {
        User user = userService.findByID(id);
        user.setRating(user.getRating() + amount);
        userService.update(user);
        return userConverter.convertToUserDTO(user);
    }

    @GetMapping("/rating/down/{id}/{amount}")
    public UserDTO downVoteRatingForUser(@PathVariable Long id, @PathVariable Long amount) {
        User user = userService.findByID(id);
        user.setRating(user.getRating() - amount);
        userService.update(user);
        return userConverter.convertToUserDTO(user);
    }

    @DeleteMapping("/{id}")
    public String deleteUserByID(@PathVariable Long id) {
        User deletedUser = userService.deleteByID(id);
        return "User with ID = " + id + " was deleted.";
    }

    @GetMapping("/enable/{userId}")
    public String enableUser(@PathVariable Long userId) {
        userService.enableUser(userId);
        return "User've been enabled!";
    }

    @GetMapping("/disable/{userId}")
    public String disableUser(@PathVariable Long userId) {
        userService.disableUser(userId);
        return "User've been disabled!";
    }
}
