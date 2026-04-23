package validation;

import exceptions.UserValidationException;

public class UserValidation {

    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty() || name.length() > 100) {
            throw new UserValidationException("Неправильный ввод имени!");
        }
    }

    public static void validateEmail(String email) {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        if (email == null || email.trim().isEmpty() || !email.matches(regex)) {
            throw new UserValidationException("Неправильный ввод email!");
        }
    }

    public static void validateAge(Integer age) {
        if (age == null || age < 0 || age > 120) {
            throw new UserValidationException("Неправильный ввод возраста!");
        }
    }
}
