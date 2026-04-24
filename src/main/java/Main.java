import controller.UserController;
import dao.UserDaoImpl;
import exceptions.UserException;
import util.HibernateUtil;
import entities.User;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static UserController userController = new UserController(new UserDaoImpl());

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Консольное CRUD-приложение для управления пользователями ===");

        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                createUser(scanner);
            } else if (choice.equals("2")) {
                getUserById(scanner);
            } else if (choice.equals("3")) {
                getAllUsers();
            } else if (choice.equals("4")) {
                updateUser(scanner);
            } else if (choice.equals("5")) {
                deleteUser(scanner);
            } else if (choice.equals("0")) {
                System.out.println("Завершение работы...");
                HibernateUtil.shutdown();
                scanner.close();
                break;
            } else {
                System.out.println("Неверный ввод. Пожалуйста, выберите 0-5.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- Меню ---");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Найти пользователя по ID");
        System.out.println("3. Показать всех пользователей");
        System.out.println("4. Обновить пользователя");
        System.out.println("5. Удалить пользователя");
        System.out.println("0. Выход");
        System.out.print("Ваш выбор: ");
    }

    private static void createUser(Scanner scanner) {
        System.out.print("Введите имя (до 100 символов): ");
        String name = scanner.nextLine().trim();
        System.out.print("Введите email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Введите возраст (0–120): ");
        String ageStr = scanner.nextLine().trim();

        try {
            Integer age = null;
            if (!ageStr.isEmpty()) {
                age = Integer.parseInt(ageStr);
            }
            userController.createUser(name, email, age);
            System.out.println("Пользователь успешно создан.");
        } catch (UserException e) {
            System.err.println("Ошибка: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Ошибка: возраст должен быть целым числом.");
        }
    }

    private static void getUserById(Scanner scanner) {
        System.out.print("Введите ID пользователя: ");
        String idStr = scanner.nextLine().trim();
        try {
            Long id = Long.parseLong(idStr);
            User user = userController.getUser(id);
            System.out.println("Найден пользователь: " + user);
        } catch (NumberFormatException e) {
            System.err.println("Ошибка: ID должен быть числом.");
        } catch (UserException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    private static void getAllUsers() {
        List<User> users = userController.getAllUser();
        if (users.isEmpty()) {
            System.out.println("Список пользователей пуст.");
        } else {
            System.out.println("Список всех пользователей:");
            for (User u : users) {
                System.out.println(u);
            }
        }
    }

    private static void updateUser(Scanner scanner) {
        System.out.print("Введите ID пользователя для обновления: ");
        String idStr = scanner.nextLine().trim();
        Long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            System.err.println("Ошибка: ID должен быть числом.");
            return;
        }

        System.out.print("Введите новое имя: ");
        String name = scanner.nextLine().trim();
        System.out.print("Введите новый email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Введите новый возраст: ");
        String ageStr = scanner.nextLine().trim();

        try {
            Integer age = null;
            if (!ageStr.isEmpty()) {
                age = Integer.parseInt(ageStr);
            }
            userController.updateUser(id, name, email, age);
            System.out.println("Данные пользователя обновлены.");
        } catch (UserException e) {
            System.err.println("Ошибка: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Ошибка: возраст должен быть целым числом.");
        }
    }

    private static void deleteUser(Scanner scanner) {
        System.out.print("Введите ID пользователя для удаления: ");
        String idStr = scanner.nextLine().trim();
        try {
            Long id = Long.parseLong(idStr);
            userController.deleteUser(id);
            System.out.println("Пользователь удалён.");
        } catch (NumberFormatException e) {
            System.err.println("Ошибка: ID должен быть числом.");
        } catch (UserException e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}