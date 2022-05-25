package spring_forum.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring_forum.converters.UserConverter;
import spring_forum.domain.User;
import spring_forum.dtos.RegisterDTO;
import spring_forum.dtos.UserDTO;
import spring_forum.services.CacheService;
import spring_forum.services.ImageService;
import spring_forum.services.UserService;
import spring_forum.utils.GeoUtils;
import spring_forum.utils.ImageUtils;
import spring_forum.utils.Utils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import static spring_forum.utils.CacheKeys.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ImageService imageService;
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

    @GetMapping("/rating/up/{id}")
    public UserDTO upVoteRatingForUser(@PathVariable Long id) {
        User user = userService.findByID(id);
        user.setRating(user.getRating() + 1);
        userService.update(user);
        return userConverter.convertToUserDTO(user);
    }

    @GetMapping("/rating/down/{id}")
    public UserDTO downVoteRatingForUser(@PathVariable Long id) {
        User user = userService.findByID(id);
        user.setRating(user.getRating() - 1);
        userService.update(user);
        return userConverter.convertToUserDTO(user);
    }

    @DeleteMapping("/{id}")
    public String deleteUserByID(@PathVariable Long id) {
        User deletedUser = userService.deleteByID(id);
        imageService.deleteImage(deletedUser.getImageUrl());
        return "User with ID = " + id + " was deleted.";
    }

    @GetMapping("/{id}/avatar")
    public ResponseEntity<byte[]> getAvatar(@PathVariable Long id) {
        byte[] avatar;
        String cacheKey = AVATAR_FOR_USER + id;
        if (cacheService.containsKey(cacheKey)) {
            avatar = cacheService.getImage(cacheKey);
        } else {
            User user = userService.findByID(id);
            if (user.getImageUrl() == null) {
                avatar = ImageUtils.getDefaultImage();
            } else {
                avatar = imageService.getImage(user.getImageUrl());
            }
            cacheService.putImage(cacheKey, avatar);
        }
        return ResponseEntity
                .ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(avatar);
    }

    @PutMapping("/{id}/avatar")
    public String uploadAvatar(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        String errorMessage = "Image was not uploaded. Try again.";
        if (file == null) {
            return errorMessage;
        }
        try {
            byte[] fileContent = file.getBytes();
            String imageUrl = userService.uploadAvatar(id);
            imageService.uploadImage(imageUrl, fileContent);
            String cacheKey = AVATAR_FOR_USER + id;
            cacheService.remove(cacheKey);
            cacheService.putImage(cacheKey, fileContent);
            return "Image was successfully uploaded.";
        } catch (IOException e) {
            return errorMessage;
        }
    }

    @GetMapping("/confirm-account")
    public String confirmEmailForUser(@RequestParam("token") String token) {
        userService.enableUser(token);
        return "You've successfully enabled your account!";
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
