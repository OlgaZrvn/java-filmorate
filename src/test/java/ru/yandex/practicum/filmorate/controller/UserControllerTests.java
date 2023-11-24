package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

public class UserControllerTests {
    private UserController userController;

    @BeforeEach
    public void setUp() {
        userController = new UserController();
    }

    @Test
    public void shouldNotAddEmptyEmail() {
        User user = new User(0, "", "Login", "Name", LocalDate.of(1999, 12, 28));
        Assertions.assertThrows(ValidationException.class, () -> userController.postUser(user));
    }

    @Test
    public void shouldNotAddIncorrectEmail() {
        User user = new User(0, "Email.yandex.ru", "Login", "Name", LocalDate.of(1999, 12, 28));
        Assertions.assertThrows(ValidationException.class, () -> userController.postUser(user));
    }

    @Test
    public void shouldNotAddEmptyLogin() {
        User user = new User(0, "Email@yandex.ru", "", "Name", LocalDate.of(1999, 12, 28));
        Assertions.assertThrows(ValidationException.class, () -> userController.postUser(user));
    }

    @Test
    public void shouldNotAddIncorrectLogin() {
        User user = new User(0, "Email@yandex.ru", "Log in", "Name", LocalDate.of(1999, 12, 28));
        Assertions.assertThrows(ValidationException.class, () -> userController.postUser(user));
    }

    @Test
    public void shouldNotAddFutureDate() {
        User user = new User(0, "Email@yandex.ru", "Login", "Name", LocalDate.of(2999, 12, 28));
        Assertions.assertThrows(ValidationException.class, () -> userController.postUser(user));
    }
}
