package spring_forum.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring_forum.domain.User;
import spring_forum.exceptions.ExistsException;
import spring_forum.exceptions.NotFoundException;
import spring_forum.rabbitMQ.Producer;
import spring_forum.repositories.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static spring_forum.TestConstants.*;
import static spring_forum.utils.ExceptionMessages.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CacheService cacheService;

    @Mock
    private Producer producer;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, cacheService, producer);
    }

    @Test
    void findAll() {
        User user2 = User.builder().id(2L).name("Kirill").build();
        List<User> usersToReturn = new ArrayList<>();
        usersToReturn.add(USER);
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
    void findUserByEmail() {
        Optional<User> userOptional = Optional.of(USER);
        when(userRepository.findUserByEmail(anyString())).thenReturn(userOptional);
        User receivedUser = userService.findUserByEmail(USER.getEmail());
        assertEquals(1L, receivedUser.getId());
        assertEquals(USER.getName(), receivedUser.getName());
        assertEquals(USER.getEmail(), receivedUser.getEmail());
        verify(userRepository).findUserByEmail(anyString());
    }

    @Test
    void findByID() {
        Optional<User> userOptional = Optional.of(USER);
        when(userRepository.findById(anyLong())).thenReturn(userOptional);
        User receivedUser = userService.findByID(1L);
        assertEquals(1L, receivedUser.getId());
        assertEquals(USER.getName(), receivedUser.getName());
        verify(userRepository).findById(anyLong());
    }

    @Test
    void enableUserByID() {
        Optional<User> userOptional = Optional.of(USER);
        when(userRepository.findById(anyLong())).thenReturn(userOptional);
        USER.setEnabled(false);
        userService.enableUser(USER.getId());
        assertTrue(USER.isEnabled());
    }

    @Test
    void disableUserByID() {
        Optional<User> userOptional = Optional.of(USER);
        when(userRepository.findById(anyLong())).thenReturn(userOptional);
        userService.disableUser(USER.getId());
        assertFalse(USER.isEnabled());
        USER.setEnabled(true);
    }

    @Test
    void save() {
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(USER);
        User savedUser = userService.save(USER);
        assertEquals(USER.getId(), savedUser.getId());
        assertEquals(USER.getName(), savedUser.getName());
        verify(userRepository).findUserByEmail(anyString());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void update() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(USER));
        User updatedUser = userService.update(USER);
        assertEquals(1L, updatedUser.getId());
        assertEquals("Dan", updatedUser.getName());
        verify(userRepository).findById(anyLong());
    }

    @Test
    void deleteByID() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(USER));
        User deletedUser = userService.deleteByID(1L);
        verify(userRepository).findById(anyLong());
        assertFalse(deletedUser.isEnabled());
        USER.setEnabled(true);
    }

    @Test
    void findAllWithError() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());
        NotFoundException exception = assertThrows(NotFoundException.class, userService::findAll);
        assertEquals(NO_USERS, exception.getMessage());
        verify(userRepository).findAll();
    }

    @Test
    void findUserByEmailWithError() {
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.findUserByEmail(USER.getEmail()));
        assertEquals(USER_NOT_FOUND_BY_EMAIL + USER.getEmail(), exception.getMessage());
        verify(userRepository).findUserByEmail(anyString());
    }

    @Test
    void findByIDWithError() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.findByID(NEGATIVE_ID));
        assertEquals(USER_NOT_FOUND_BY_ID + NEGATIVE_ID, exception.getMessage());
        verify(userRepository).findById(anyLong());
    }

    @Test
    void saveWithError() {
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(USER_EMPTY));
        ExistsException exception = assertThrows(ExistsException.class,
                () -> userService.save(USER));
        assertEquals(USER_EXISTS_WITH_EMAIL + USER.getEmail(), exception.getMessage());
        verify(userRepository).findUserByEmail(anyString());
    }

    @Test
    void updateWithError1() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.update(USER));
        assertEquals(USER_NOT_FOUND_BY_ID + USER.getId(), exception.getMessage());
        verify(userRepository).findById(anyLong());
    }

    @Test
    void updateWithError2() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().email("gogog@ya.ru").build()));
        when(userRepository.findUserByEmail(anyString())).thenReturn(Optional.of(USER_EMPTY));
        ExistsException exception = assertThrows(ExistsException.class,
                () -> userService.update(USER));
        assertEquals(USER_EXISTS_WITH_EMAIL + USER.getEmail(), exception.getMessage());
        verify(userRepository).findById(anyLong());
        verify(userRepository).findUserByEmail(anyString());
    }

    @Test
    void deleteWithError() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> userService.deleteByID(NEGATIVE_ID));
        assertEquals(USER_NOT_FOUND_BY_ID + NEGATIVE_ID, exception.getMessage());
        verify(userRepository).findById(anyLong());
    }
}