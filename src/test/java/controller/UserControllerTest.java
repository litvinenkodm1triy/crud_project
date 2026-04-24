package controller;

import dao.UserDao;
import entities.User;
import exceptions.UserNotFoundException;
import exceptions.UserValidationException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private static UserController controller;
    private static UserDao testDao;

    static class InMemoryUserDao implements UserDao {
        private Long nextId = 1L;
        private final java.util.Map<Long, User> storage = new java.util.HashMap<>();

        @Override
        public void save(User user) {
            user.setId(nextId++);
            storage.put(user.getId(), user);
        }

        @Override
        public User findById(Long id) {
            return storage.get(id);
        }

        @Override
        public java.util.List<User> findAll() {
            return new java.util.ArrayList<>(storage.values());
        }

        @Override
        public void update(User user) {
            if (!storage.containsKey(user.getId()))
                throw new RuntimeException("User not found");
            storage.put(user.getId(), user);
        }

        @Override
        public void delete(Long id) {
            storage.remove(id);
        }
    }

    @BeforeEach
    void setUp() {
        testDao = new InMemoryUserDao();
        controller = new UserController(testDao);
    }

    @Test
    void testCreateUserSuccess() {
        controller.createUser("Иван", "ivan@example.com", 25);
        User user = testDao.findAll().get(0);
        assertNotNull(user.getId());
        assertEquals("Иван", user.getName());
        assertEquals("ivan@example.com", user.getEmail());
        assertEquals(25, user.getAge());
    }

    @Test
    void testCreateUserInvalidName() {
        assertThrows(UserValidationException.class, () ->
                controller.createUser("", "valid@email.com", 25)
        );
    }

    @Test
    void testFindUserByIdNotFound() {
        assertThrows(UserNotFoundException.class, () ->
                controller.getUser(999L)
        );
    }

    @Test
    void testUpdateUserSuccess() {
        controller.createUser("Старый", "old@mail.com", 20);
        User saved = testDao.findAll().get(0);
        controller.updateUser(saved.getId(), "Новый", "new@mail.com", 30);
        User updated = testDao.findById(saved.getId());
        assertEquals("Новый", updated.getName());
        assertEquals("new@mail.com", updated.getEmail());
        assertEquals(30, updated.getAge());
    }

    @Test
    void testDeleteUserSuccess() {
        controller.createUser("Коля", "kolya@mail.com", 22);
        User user = testDao.findAll().get(0);
        controller.deleteUser(user.getId());
        assertNull(testDao.findById(user.getId()));
    }
}