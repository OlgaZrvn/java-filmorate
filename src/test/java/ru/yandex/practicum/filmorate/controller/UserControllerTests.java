package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTests {
    private UserController userController;

    @BeforeEach
    public void setUp() {
        UserStorage userStorage = new InMemoryUserStorage();
        userController = new UserController(
                userStorage,
                new UserService(userStorage)
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

    @Test
    public void shouldAddAFriend() {
        userController.addFriend(1, 2);
        List<User> friendsOfUser1 = new ArrayList<>();
        friendsOfUser1.add(userController.getUserById(2));
        List<User> friendsOfUser2 = new ArrayList<>();
        friendsOfUser2.add(userController.getUserById(1));
        assertEquals(friendsOfUser1, userController.getFriends(1), "Друг не добавлен");
        assertEquals(friendsOfUser2, userController.getFriends(2), "Друг не добавлен");
    }
    @Test
    public void shouldDeleteAFriend() {
        userController.addFriend(1, 2);
        userController.deleteFriend(1, 2);
        List<User> noFriends = new ArrayList<>();
        assertEquals(noFriends, userController.getFriends(1), "Друг не удален");
        assertEquals(noFriends, userController.getFriends(2), "Друг не удален");
    }

    @Test
    public void shouldGetAllFriends() {
        userController.addFriend(1, 2);
        userController.addFriend(1, 3);
        List<User> friendsOfUser1 = new ArrayList<>();
        friendsOfUser1.add(userController.getUserById(2));
        friendsOfUser1.add(userController.getUserById(3));
        assertEquals(friendsOfUser1, userController.getFriends(1), "Ошибка при вызове всех друзей");
    }

    @Test
    public void shouldGetCommonFriends() {
        userController.addFriend(1, 3);
        userController.addFriend(2, 3);
        List<User> commonFriends = new ArrayList<>();
        commonFriends.add(userController.getUserById(3));
        assertEquals(commonFriends, userController.getCommonFriends(1, 2), "Ошибка при вызове общих друзей");
    }
}
