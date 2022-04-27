package spring_forum.controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring_forum.converters.UserConverter;
import spring_forum.domain.User;
import spring_forum.dtos.UserDTO;
import spring_forum.services.ImageService;
import spring_forum.services.UserService;
import spring_forum.utils.GeoUtils;
import spring_forum.utils.ImageUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final ImageService imageService;
    private final UserConverter userConverter;

    public UserController(UserService userService, ImageService imageService, UserConverter userConverter) {
        this.userService = userService;
        this.imageService = imageService;
        this.userConverter = userConverter;
    }

    @GetMapping
    public Set<UserDTO> showAllUsers() {
        return userService.findAll().stream()
                .map(userConverter::convertToUserDTO)
                .collect(Collectors.toSet());
    }

    @GetMapping("/{id}")
    public UserDTO findUserByID(@PathVariable Long id) {
        return userConverter.convertToUserDTO(userService.findByID(id));
    }

    @GetMapping("/name/{name}")
    public UserDTO findUserByName(@PathVariable String name) {
        return userConverter.convertToUserDTO(userService.findUserByName(name));
    }

    @PostMapping
    public UserDTO saveUser(@Valid @RequestBody UserDTO userDTO, HttpServletRequest request) {
        if (userDTO.getCountry() == null) {
            userDTO.setCountry(GeoUtils.getCountryByIp(request.getRemoteHost()));
        }
        if (userDTO.getLanguage() == null) {
            userDTO.setLanguage(GeoUtils.getLanguageByCountry(userDTO.getCountry()));
        }
        User savedUser = userService.save(userConverter.convertToUser(userDTO));
        userDTO.setId(savedUser.getId());
        return userDTO;
    }

    @PutMapping
    public UserDTO updateUser(@Valid @RequestBody UserDTO userDTO) {
        User updatedUser = userService.update(userConverter.convertToUser(userDTO));
        userDTO.setCountry(updatedUser.getCountry());
        userDTO.setLanguage(updatedUser.getLanguage());
        return userDTO;
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
        User user = userService.findByID(id);
        if (user.getImageUrl() == null) {
            avatar = ImageUtils.getDefaultImage();
        } else {
            avatar = imageService.getImage(user.getImageUrl());
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
            String imageUrl = userService.uploadAvatar(id, fileContent);
            imageService.uploadImage(imageUrl, fileContent);
            return "Image was successfully uploaded.";
        } catch (IOException e) {
            return errorMessage;
        }
    }
}
