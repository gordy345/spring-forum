package spring_forum.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring_forum.domain.User;
import spring_forum.repositories.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTests {

    @Mock
    private UserRepository userRepository;

    private UserService userService;

    private final User user = User.builder().id(1L).name("Dan").build();

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void findAll() {
        User user2 = User.builder().id(2L).name("Kirill").build();
        List<User> usersToReturn = new ArrayList<>();
        usersToReturn.add(user);
        usersToReturn.add(user2);
        when(userRepository.findAll()).thenReturn(usersToReturn);
        Set<User> users = userService.findAll();
        Iterator<User> userIterator = users.iterator();
        assertEquals(2, users.size());
        assertEquals(1L, userIterator.next().getId());
        assertEquals(2L, userIterator.next().getId());
        verify(userRepository).findAll();
    }

    @Test
    void findUserByName() {
        Optional<User> userOptional = Optional.of(user);
        when(userRepository.findUserByName(anyString())).thenReturn(userOptional);
        User receivedUser = userService.findUserByName("Dan");
        assertEquals(1L, receivedUser.getId());
        assertEquals("Dan", receivedUser.getName());
        verify(userRepository).findUserByName(anyString());
    }

    @Test
    void findByID() {
        Optional<User> userOptional = Optional.of(user);
        when(userRepository.findById(anyLong())).thenReturn(userOptional);
        User receivedUser = userService.findByID(1L);
        assertEquals(1L, receivedUser.getId());
        assertEquals("Dan", receivedUser.getName());
        verify(userRepository).findById(anyLong());
    }

    @Test
    void save() {
        when(userRepository.findUserByName(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(user);
        User savedUser = userService.save(user);
        assertEquals(1L, savedUser.getId());
        assertEquals("Dan", savedUser.getName());
        verify(userRepository).findUserByName(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        User updatedUser = userService.update(user);
        assertEquals(1L, updatedUser.getId());
        assertEquals("Dan", updatedUser.getName());
        verify(userRepository).findById(anyLong());
    }

    @Test
    void deleteByID() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        userService.deleteByID(1L);
        verify(userRepository).findById(anyLong());
        verify(userRepository).delete(any(User.class));
    }
}