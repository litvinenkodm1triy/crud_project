package controller;

import entities.User;
import exceptions.UserNotFoundException;
import exceptions.UserValidationException;
import lombok.extern.slf4j.Slf4j;
import service.UserService;
import validation.UserValidation;

import java.util.List;

@Slf4j
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    public void createUser(String name, String email, Integer age) {
        UserValidation.validateName(name);
        UserValidation.validateEmail(email);
        UserValidation.validateAge(age);

        User user = User.builder()
                .name(name)
                .email(email)
                .age(age)
                .build();

        userService.createUser(user);
        log.info("Пользователь успешно сохранен!");
    }

    public User getUser(Long id) {
        if (id == null || id <= 0) {
            throw new UserValidationException("Некорректный id");
        }
        User user = userService.getUser(id);
        log.info("Найден пользователь с id = {}", id);
        return user;
    }

    public List<User> getAllUser() {
        List<User> all = userService.getAllUsers();
        log.info("Получен список всех пользователей!");
        return all;
    }

    public void updateUser(Long id, String name, String email, Integer age) {
        if (id == null || id <= 0) {
            throw new UserValidationException("Некорректный id");
        }
        UserValidation.validateName(name);
        UserValidation.validateEmail(email);
        UserValidation.validateAge(age);

        User user = User.builder()
                .id(id)
                .name(name)
                .email(email)
                .age(age)
                .build();

        userService.updateUser(user);
        log.info("Данные пользователя успешно обновлены!");
    }

    public void deleteUser(Long id) {
        if (id == null || id <= 0) {
            throw new UserValidationException("Некорректный id");
        }
        userService.deleteUser(id);
        log.info("Удален пользователь с id = {}", id);
    }
}