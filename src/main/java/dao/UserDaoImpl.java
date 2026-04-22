package dao;

import entities.User;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

import java.util.List;

@Slf4j
public class UserDaoImpl implements UserDao {

    @Override
    public void save(User user) {
        log.debug("Сохранение пользователя: {}", user);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            log.info("Пользователь сохранён с id={}", user.getId());
        } catch (Exception e) {
            log.error("Ошибка при сохранении пользователя", e);
            throw new RuntimeException("Не удалось сохранить пользователя", e);
        }
    }

    @Override
    public User findById(Long id) {
        log.debug("Поиск пользователя по id={}", id);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            User user = session.find(User.class, id);
            tx.commit();
            log.info("Найден пользователь: {}", user);
            return user;
        } catch (Exception e) {
            log.error("Ошибка при поиске пользователя id={}", id, e);
            throw new RuntimeException("Не удалось найти пользователя", e);
        }
    }

    @Override
    public List<User> findAll() {
        log.debug("Запрос всех пользователей");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            List<User> users = session.createQuery("FROM User", User.class).list();
            tx.commit();
            log.info("Найдено {} пользователей", users.size());
            return users;
        } catch (Exception e) {
            log.error("Ошибка при получении списка пользователей", e);
            throw new RuntimeException("Не удалось получить список пользователей", e);
        }
    }

    @Override
    public void update(User user) {
        log.debug("Обновление пользователя: {}", user);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            User existing = session.find(User.class, user.getId());
            if (existing == null) {
                throw new RuntimeException("Пользователь с id " + user.getId() + " не найден для обновления");
            }
            session.merge(user);
            tx.commit();
            log.info("Пользователь с id={} обновлён", user.getId());
        } catch (Exception e) {
            log.error("Ошибка при обновлении пользователя id={}", user.getId(), e);
            throw new RuntimeException("Не удалось обновить пользователя", e);
        }
    }

    @Override
    public void delete(Long id) {
        log.debug("Удаление пользователя с id={}", id);
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            User user = session.find(User.class, id);
            if (user == null) {
                log.warn("Пользователь с id={} не найден, удаление невозможно", id);
                throw new RuntimeException("Пользователь с id " + id + " не найден");
            }
            session.remove(user);
            tx.commit();
            log.info("Пользователь с id={} удалён", id);
        } catch (Exception e) {
            log.error("Ошибка при удалении пользователя id={}", id, e);
            throw new RuntimeException("Не удалось удалить пользователя", e);
        }
    }
}
