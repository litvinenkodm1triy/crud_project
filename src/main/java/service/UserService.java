package service;

import dao.UserDao;
import entities.User;
import exceptions.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
public class UserService {

    private final UserDao userDao;

    public UserService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void createUser(User user) {
        log.debug("Сервис: сохранение пользователя {}", user);
        userDao.save(user);
        log.info("Пользователь сохранён через сервис: id={}", user.getId());
    }

    public User getUser(Long id) {
        User user = userDao.findById(id);
        if (user == null) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден");
        }
        return user;
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public void updateUser(User user) {
        User existing = userDao.findById(user.getId());
        if (existing == null) {
            throw new UserNotFoundException("Пользователь с id " + user.getId() + " не найден для обновления");
        }
        userDao.update(user);
        log.info("Пользователь с id={} обновлён через сервис", user.getId());
    }

    public void deleteUser(Long id) {
        User existing = userDao.findById(id);
        if (existing == null) {
            throw new UserNotFoundException("Пользователь с id " + id + " не найден для удаления");
        }
        userDao.delete(id);
        log.info("Пользователь с id={} удалён через сервис", id);
    }
}