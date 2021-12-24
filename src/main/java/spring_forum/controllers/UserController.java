package spring_forum.controllers;

import org.springframework.web.bind.annotation.*;
import spring_forum.converters.UserConverter;
import spring_forum.domain.User;
import spring_forum.dtos.UserDTO;
import spring_forum.services.UserService;

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
    public UserDTO saveUser(@RequestBody UserDTO userDTO) {
        User savedUser = userService.save(userConverter.convertToUser(userDTO));
        userDTO.setId(savedUser.getId());
        return userDTO;
    }

    @PutMapping
    public UserDTO updateUser(@RequestBody UserDTO userDTO) {
        userService.update(userConverter.convertToUser(userDTO));
        return userDTO;
    }

    @DeleteMapping("/{id}")
    public String deleteUserByID(@PathVariable Long id) {
        userService.deleteByID(id);
        return "User with ID = " + id + " was deleted.";
    }
}
