package validation;

import exceptions.UserValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserValidationTest {

    @Test
    void validateName_WithValidName_ShouldNotThrow() {
        assertDoesNotThrow(() -> UserValidation.validateName("John Doe"));
        assertDoesNotThrow(() -> UserValidation.validateName("A"));
    }

    @Test
    void validateName_WithNullName_ShouldThrow() {
        assertThrows(UserValidationException.class, () -> UserValidation.validateName(null));
    }

    @Test
    void validateName_WithEmptyName_ShouldThrow() {
        assertThrows(UserValidationException.class, () -> UserValidation.validateName(""));
        assertThrows(UserValidationException.class, () -> UserValidation.validateName("   "));
    }

    @Test
    void validateName_WithTooLongName_ShouldThrow() {
        String longName = "a".repeat(101);
        assertThrows(UserValidationException.class, () -> UserValidation.validateName(longName));
    }

    @Test
    void validateEmail_WithValidEmail_ShouldNotThrow() {
        assertDoesNotThrow(() -> UserValidation.validateEmail("user@example.com"));
        assertDoesNotThrow(() -> UserValidation.validateEmail("a.b@c.co.uk"));
    }

    @Test
    void validateEmail_WithInvalidEmail_ShouldThrow() {
        assertThrows(UserValidationException.class, () -> UserValidation.validateEmail("invalid"));
        assertThrows(UserValidationException.class, () -> UserValidation.validateEmail("missing@domain"));
        assertThrows(UserValidationException.class, () -> UserValidation.validateEmail("@missing.com"));
    }

    @Test
    void validateAge_WithValidAge_ShouldNotThrow() {
        assertDoesNotThrow(() -> UserValidation.validateAge(0));
        assertDoesNotThrow(() -> UserValidation.validateAge(50));
        assertDoesNotThrow(() -> UserValidation.validateAge(120));
    }

    @Test
    void validateAge_WithNullAge_ShouldThrow() {
        assertThrows(UserValidationException.class, () -> UserValidation.validateAge(null));
    }

    @Test
    void validateAge_WithNegativeAge_ShouldThrow() {
        assertThrows(UserValidationException.class, () -> UserValidation.validateAge(-1));
    }

    @Test
    void validateAge_WithAgeAbove120_ShouldThrow() {
        assertThrows(UserValidationException.class, () -> UserValidation.validateAge(121));
    }
}