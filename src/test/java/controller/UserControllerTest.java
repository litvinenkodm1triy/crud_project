package controller;

import entities.User;
import exceptions.UserNotFoundException;
import exceptions.UserValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private static final Long VALID_ID = 1L;
    private static final String VALID_NAME = "Иван Петров";
    private static final String VALID_EMAIL = "ivan@example.com";
    private static final Integer VALID_AGE = 30;

    @Test
    void createUser_ValidData_ShouldCallService() {

        userController.createUser(VALID_NAME, VALID_EMAIL, VALID_AGE);


        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService, times(1)).createUser(captor.capture());
        User captured = captor.getValue();
        assertNull(captured.getId());
        assertEquals(VALID_NAME, captured.getName());
        assertEquals(VALID_EMAIL, captured.getEmail());
        assertEquals(VALID_AGE, captured.getAge());
    }

    @Test
    void createUser_InvalidName_ShouldThrowValidationExceptionAndNotCallService() {
        assertThrows(UserValidationException.class,
                () -> userController.createUser(null, VALID_EMAIL, VALID_AGE));
        assertThrows(UserValidationException.class,
                () -> userController.createUser("", VALID_EMAIL, VALID_AGE));
        assertThrows(UserValidationException.class,
                () -> userController.createUser("   ", VALID_EMAIL, VALID_AGE));
        assertThrows(UserValidationException.class,
                () -> userController.createUser("a".repeat(101), VALID_EMAIL, VALID_AGE));

        verify(userService, never()).createUser(any());
    }

    @Test
    void createUser_InvalidEmail_ShouldThrowValidationExceptionAndNotCallService() {
        String[] invalidEmails = {null, "", "   ", "ivan@", "ivan@example", "ivan.example.com"};
        for (String email : invalidEmails) {
            assertThrows(UserValidationException.class,
                    () -> userController.createUser(VALID_NAME, email, VALID_AGE));
        }
        verify(userService, never()).createUser(any());
    }

    @Test
    void createUser_InvalidAge_ShouldThrowValidationExceptionAndNotCallService() {
        assertThrows(UserValidationException.class,
                () -> userController.createUser(VALID_NAME, VALID_EMAIL, null));
        assertThrows(UserValidationException.class,
                () -> userController.createUser(VALID_NAME, VALID_EMAIL, -1));
        assertThrows(UserValidationException.class,
                () -> userController.createUser(VALID_NAME, VALID_EMAIL, 121));
        verify(userService, never()).createUser(any());
    }

    @Test
    void getUser_ValidId_ShouldReturnUser() {
        User expected = User.builder().id(VALID_ID).name(VALID_NAME).build();
        when(userService.getUser(VALID_ID)).thenReturn(expected);

        User actual = userController.getUser(VALID_ID);

        assertEquals(expected, actual);
        verify(userService, times(1)).getUser(VALID_ID);
    }

    @Test
    void getUser_InvalidId_ShouldThrowValidationExceptionAndNotCallService() {
        assertThrows(UserValidationException.class, () -> userController.getUser(null));
        assertThrows(UserValidationException.class, () -> userController.getUser(0L));
        assertThrows(UserValidationException.class, () -> userController.getUser(-1L));

        verify(userService, never()).getUser(any());
    }

    @Test
    void getUser_UserNotFound_ShouldPropagateServiceException() {
        when(userService.getUser(VALID_ID)).thenThrow(new UserNotFoundException("Пользователь не найден"));

        assertThrows(UserNotFoundException.class, () -> userController.getUser(VALID_ID));
        verify(userService, times(1)).getUser(VALID_ID);
    }

    @Test
    void getAllUser_ShouldReturnListFromService() {
        List<User> expected = List.of(
                User.builder().id(1L).name("A").build(),
                User.builder().id(2L).name("B").build()
        );
        when(userService.getAllUsers()).thenReturn(expected);

        List<User> actual = userController.getAllUser();

        assertEquals(expected, actual);
        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void updateUser_ValidData_ShouldCallService() {
        userController.updateUser(VALID_ID, VALID_NAME, VALID_EMAIL, VALID_AGE);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userService, times(1)).updateUser(captor.capture());
        User captured = captor.getValue();
        assertEquals(VALID_ID, captured.getId());
        assertEquals(VALID_NAME, captured.getName());
        assertEquals(VALID_EMAIL, captured.getEmail());
        assertEquals(VALID_AGE, captured.getAge());
    }

    @Test
    void updateUser_InvalidId_ServiceNotCalled() {
        assertThrows(UserValidationException.class,
                () -> userController.updateUser(0L, VALID_NAME, VALID_EMAIL, VALID_AGE));
        assertThrows(UserValidationException.class,
                () -> userController.updateUser(-1L, VALID_NAME, VALID_EMAIL, VALID_AGE));
        verify(userService, never()).updateUser(any());
    }

    @Test
    void updateUser_InvalidName_ShouldThrowValidationExceptionAndNotCallService() {
        assertThrows(UserValidationException.class,
                () -> userController.updateUser(VALID_ID, null, VALID_EMAIL, VALID_AGE));
        verify(userService, never()).updateUser(any());
    }

    @Test
    void updateUser_UserNotFound_ShouldPropagateServiceException() {
        doThrow(new UserNotFoundException("Не найден"))
                .when(userService).updateUser(any(User.class));

        assertThrows(UserNotFoundException.class,
                () -> userController.updateUser(VALID_ID, VALID_NAME, VALID_EMAIL, VALID_AGE));
        verify(userService, times(1)).updateUser(any());
    }

    @Test
    void deleteUser_ValidId_ShouldCallService() {
        userController.deleteUser(VALID_ID);
        verify(userService, times(1)).deleteUser(VALID_ID);
    }

    @Test
    void deleteUser_UserNotFound_ShouldPropagateServiceException() {
        doThrow(new UserNotFoundException("Не найден")).when(userService).deleteUser(VALID_ID);

        assertThrows(UserNotFoundException.class, () -> userController.deleteUser(VALID_ID));
        verify(userService, times(1)).deleteUser(VALID_ID);
    }
}