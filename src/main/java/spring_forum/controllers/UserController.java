package spring_forum.controllers;

import org.springframework.web.bind.annotation.*;
import spring_forum.converters.UserConverter;
import spring_forum.domain.User;
import spring_forum.dtos.UserDTO;
import spring_forum.services.UserService;
import spring_forum.utils.GeoUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserConverter userConverter;

    public UserController(UserService userService, UserConverter userConverter) {
        this.userService = userService;
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
        userService.deleteByID(id);
        return "User with ID = " + id + " was deleted.";
    }
}
