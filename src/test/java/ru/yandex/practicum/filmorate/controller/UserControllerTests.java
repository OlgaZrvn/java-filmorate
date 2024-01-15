package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.db.FriendshipDbStorage;
import ru.yandex.practicum.filmorate.storage.db.UserDbStorage;

import java.time.LocalDate;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserControllerTests {
    private final JdbcTemplate jdbcTemplate;
    private UserController userController;

    @BeforeEach
    public void setUp() {
        UserStorage userStorage = new UserDbStorage(jdbcTemplate);
        FriendshipStorage friendshipStorage = new FriendshipDbStorage(jdbcTemplate);
        userController = new UserController(
                new UserService(userStorage, friendshipStorage)
        );
        User user1 = new User(1, "Email1@yandex.ru", "Login1", "Name1",
                LocalDate.of(1999, 12, 28));
        userController.postUser(user1);
        User user2 = new User(2, "Email2@yandex.ru", "Login2", "Name2",
                LocalDate.of(1999, 12, 29));
        userController.postUser(user2);
        User user3 = new User(3, "Email3@yandex.ru", "Login3", "Name3",
                LocalDate.of(1999, 12, 30));
        userController.postUser(user3);
    }

    @Test
    public void shouldNotAddEmptyLogin() {
        User user = new User(0, "Email@yandex.ru", "", "Name",
                LocalDate.of(1999, 12, 28));
        Assertions.assertThrows(ValidationException.class, () -> userController.postUser(user));
    }

    @Test
    public void shouldNotAddIncorrectLogin() {
        User user = new User(0, "Email@yandex.ru", "Log in", "Name",
                LocalDate.of(1999, 12, 28));
        Assertions.assertThrows(ValidationException.class, () -> userController.postUser(user));
    }

    @Test
    public void shouldNotAddFutureDate() {
        User user = new User(0, "Email@yandex.ru", "Login", "Name",
                LocalDate.of(2999, 12, 28));
        Assertions.assertThrows(ValidationException.class, () -> userController.postUser(user));
    }

    @Test
    public void shouldNotGetUserById9999() {
        Assertions.assertThrows(NotFoundException.class, () -> userController.getUserById(9999));
    }

    @Test
    public void shouldNotGetUserByNegativeId() {
        Assertions.assertThrows(NotFoundException.class, () -> userController.getUserById(-1));
    }
}