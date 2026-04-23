package controller;

import dao.UserDao;
import entities.User;
import exceptions.UserNotFoundException;
import exceptions.UserValidationException;
import lombok.extern.slf4j.Slf4j;
import validation.UserValidation;

import java.util.List;

@Slf4j
public class UserController {

    private final UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
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

        userDao.save(user);
        log.info("Пользователь успешно сохранен!");
    }

    public User getUser(Long id) {
        if (id == null || id <= 0) {
            throw new UserValidationException("Некорректный id");
        }
        User user = userDao.findById(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");
        }
        log.info("Найден пользователь с id = {}", id);
        return user;
    }

    public List<User> getAllUser() {

        List<User> all = userDao.findAll();
        log.info("Получен список всех пользователей!");
        return all;
    }

    public void updateUser(Long id, String name, String email, Integer age) {

        User existing = userDao.findById(id);
        if (existing == null) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");
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

        userDao.update(user);
        log.info("Данные пользователя успешно обновлены!");
    }

    public void deleteUser(Long id) {

        User existing = userDao.findById(id);
        if (existing == null) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");
        }

        userDao.delete(id);
        log.info("Удален пользователь с id ={}", id);
    }

}
