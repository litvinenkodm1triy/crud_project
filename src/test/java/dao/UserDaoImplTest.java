package dao;

import entities.User;
import exceptions.UserNotFoundException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoImplTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static SessionFactory sessionFactory;
    private UserDaoImpl userDao;

    @BeforeAll
    static void setUpContainer() {
        postgres.start();

        Properties props = new Properties();
        props.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        props.setProperty("hibernate.connection.username", postgres.getUsername());
        props.setProperty("hibernate.connection.password", postgres.getPassword());
        props.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
        props.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        props.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        props.setProperty("hibernate.show_sql", "true");

        sessionFactory = new Configuration()
                .addAnnotatedClass(User.class)
                .addProperties(props)
                .buildSessionFactory();
    }

    @AfterAll
    static void tearDownContainer() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
        postgres.stop();
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl(sessionFactory);
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void save_ShouldPersistUser() {
        User user = User.builder()
                .name("Test User")
                .email("test@example.com")
                .age(25)
                .build();

        userDao.save(user);

        assertNotNull(user.getId());
        User found = userDao.findById(user.getId()); // теперь не бросает исключение, т.к. найден
        assertEquals("Test User", found.getName());
    }

    @Test
    void findById_WhenUserExists_ShouldReturnUser() {
        User user = User.builder().name("John").email("john@example.com").age(30).build();
        userDao.save(user);

        User found = userDao.findById(user.getId());

        assertEquals(user.getId(), found.getId());
        assertEquals("John", found.getName());
    }

    @Test
    void findById_WhenUserDoesNotExist_ShouldThrowUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> userDao.findById(999L));
    }

    @Test
    void findAll_ShouldReturnAllUsers() {
        User user1 = User.builder().name("User1").email("u1@example.com").age(20).build();
        User user2 = User.builder().name("User2").email("u2@example.com").age(25).build();
        userDao.save(user1);
        userDao.save(user2);

        List<User> users = userDao.findAll();

        assertEquals(2, users.size());
        assertTrue(users.stream().anyMatch(u -> "User1".equals(u.getName())));
        assertTrue(users.stream().anyMatch(u -> "User2".equals(u.getName())));
    }

    @Test
    void update_ShouldMergeChanges() {
        User user = User.builder().name("Original").email("original@example.com").age(30).build();
        userDao.save(user);

        user.setName("Updated");
        user.setAge(31);
        userDao.update(user);

        User updated = userDao.findById(user.getId());
        assertEquals("Updated", updated.getName());
        assertEquals(31, updated.getAge());
        assertEquals("original@example.com", updated.getEmail());
    }

    @Test
    void update_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        User nonExistent = User.builder().id(999L).name("Ghost").email("ghost@example.com").age(99).build();
        assertThrows(UserNotFoundException.class, () -> userDao.update(nonExistent));
    }

    @Test
    void delete_ShouldRemoveUser() {
        User user = User.builder().name("ToDelete").email("delete@example.com").age(40).build();
        userDao.save(user);
        assertNotNull(userDao.findById(user.getId()));

        userDao.delete(user.getId());

        assertThrows(UserNotFoundException.class, () -> userDao.findById(user.getId()));
    }

    @Test
    void delete_WhenUserNotFound_ShouldThrowUserNotFoundException() {
        assertThrows(UserNotFoundException.class, () -> userDao.delete(999L));
    }
}