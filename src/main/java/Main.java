import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.io.InputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        try {

            Properties props = new Properties();
            try (InputStream input = Main.class.getClassLoader().getResourceAsStream("hibernate.properties")) {
                if (input == null) {
                    System.err.println("Файл hibernate.properties не найден в src/main/resources");
                    return;
                }
                props.load(input);
            }

            Configuration cfg = new Configuration();
            cfg.setProperties(props);
            cfg.addAnnotatedClass(User.class);

            SessionFactory factory = cfg.buildSessionFactory();
            Session session = factory.openSession();
            session.close();

            System.out.println(" Подключение к БД успешно!");
            factory.close();

        } catch (Exception e) {
            System.err.println(" Ошибка подключения:");
            e.printStackTrace();
        }
    }
}