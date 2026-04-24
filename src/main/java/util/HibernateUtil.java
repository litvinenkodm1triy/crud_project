package util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import entities.User;

import java.io.InputStream;
import java.util.Properties;

public class HibernateUtil {

    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            logger.info("Инициализация SessionFactory из hibernate.properties...");

            Properties properties = new Properties();
            try (InputStream input = HibernateUtil.class.getClassLoader().getResourceAsStream("hibernate.properties")) {
                if (input == null) {
                    throw new RuntimeException("hibernate.properties not found in classpath");
                }
                properties.load(input);
            }

            Configuration configuration = new Configuration();
            configuration.setProperties(properties);
            configuration.addAnnotatedClass(User.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties())
                    .build();

            SessionFactory factory = configuration.buildSessionFactory(serviceRegistry);
            logger.info("SessionFactory успешно создана.");
            return factory;
        } catch (Throwable ex) {
            logger.error("Ошибка при создании SessionFactory: {}", ex.getMessage(), ex);
            throw new ExceptionInInitializerError("Initial SessionFactory creation failed: " + ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            logger.info("Закрытие SessionFactory...");
            sessionFactory.close();
            logger.info("SessionFactory закрыта.");
        }
    }
}