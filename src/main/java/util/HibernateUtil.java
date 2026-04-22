package util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import entities.User;

public class HibernateUtil {

    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            logger.info("Инициализация SessionFactory...");
            Configuration configuration = new Configuration();
            configuration.configure();
            configuration.addAnnotatedClass(User.class);
            SessionFactory factory = configuration.buildSessionFactory();
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