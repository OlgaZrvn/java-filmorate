package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@Slf4j
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @Autowired
    public UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @PostMapping("/users")
    public User postUser(@Valid @RequestBody User user) {
        validate(user);
        userStorage.save(user);
        log.info("Добавлен новый пользователь {}", user.getName());
        return user;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return userStorage.getAll();
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Integer id) {
        return userStorage.getById(id);
    }

    @PutMapping("/users")
    public User updateUser(@Valid @RequestBody User user) {
        validate(user);
        userStorage.update(user);
        return user;
    }

    @PutMapping("/users/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        if (id < 0 || friendId < 0) {
            log.error("Отрицательный id");
            throw new NotFoundException("Отрицательный id");
        }
        userService.addAFriend(id, friendId);
        log.info("{} подружился с {}", getUserById(id).getName(), getUserById(friendId).getName());
    }

    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        userService.deleteFriend(id, friendId);
        log.info("{} больше не дружит с {}", getUserById(id).getName(), getUserById(friendId).getName());
    }

    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable Integer id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    private static void validate(User user) {
        if (null == user.getLogin() || "".equals(user.getLogin()) || user.getLogin().contains(" ")) {
            log.error("Логин пустой или содержит пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы;");
        }
        if (null == user.getName() || "".equals(user.getName())) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Не верная дата рождения");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
