package service;

import dao.UserDao;
import entities.User;
import exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .age(30)
                .build();
    }

    @Test
    void createUser_ShouldCallDaoSave() {
        userService.createUser(testUser);
        verify(userDao, times(1)).save(testUser);
    }

    @Test
    void getUser_WhenUserExists_ShouldReturnUser() {
        when(userDao.findById(1L)).thenReturn(testUser);

        User found = userService.getUser(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
        assertEquals("John Doe", found.getName());
        verify(userDao, times(1)).findById(1L);
    }

    @Test
    void getUser_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        when(userDao.findById(99L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.getUser(99L));
        verify(userDao, times(1)).findById(99L);
    }

    @Test
    void getAllUsers_ShouldReturnListFromDao() {
        List<User> users = List.of(testUser, User.builder().id(2L).build());
        when(userDao.findAll()).thenReturn(users);

        List<User> result = userService.getAllUsers();

        assertEquals(2, result.size());
        verify(userDao, times(1)).findAll();
    }

    @Test
    void updateUser_WhenUserExists_ShouldCallDaoUpdate() {
        when(userDao.findById(1L)).thenReturn(testUser);

        User updatedUser = User.builder()
                .id(1L)
                .name("John Updated")
                .email("john.updated@example.com")
                .age(31)
                .build();

        userService.updateUser(updatedUser);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userDao, times(1)).update(captor.capture());
        User captured = captor.getValue();
        assertEquals(1L, captured.getId());
        assertEquals("John Updated", captured.getName());
    }

    @Test
    void updateUser_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        when(userDao.findById(99L)).thenReturn(null);
        User nonExistent = User.builder().id(99L).build();

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(nonExistent));
        verify(userDao, never()).update(any());
    }

    @Test
    void deleteUser_WhenUserExists_ShouldCallDaoDelete() {
        when(userDao.findById(1L)).thenReturn(testUser);

        userService.deleteUser(1L);

        verify(userDao, times(1)).delete(1L);
    }

    @Test
    void deleteUser_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        when(userDao.findById(99L)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(99L));
        verify(userDao, never()).delete(any());
    }
}