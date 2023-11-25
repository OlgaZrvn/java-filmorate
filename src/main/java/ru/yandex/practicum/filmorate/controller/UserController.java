package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int userId = 1;

    @PostMapping("/users")
    public User postUser(@RequestBody User user) {
        validate(user);
        user.setId(userId++);
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь " + user.getLogin() + user.getName());
        return user;
    }

    @GetMapping("/users")
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Integer id) {
        return users.get(id);
    }

    @PutMapping("/users")
    public User updateUser(@RequestBody User user) {
        validate(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Обновлен пользователь " + user.getLogin());
        } else if (user.getId() == null) {
            user.setId(userId++);
            users.put(user.getId(), user);
            log.info("Добавлен новый пользователь " + user.getLogin());
        } else {
            log.error("Некорректный id " + user.getId());
            throw new ValidationException("Некорректный id");
        }
        return user;
    }

    private static void validate(User user) {
        if (null == user.getEmail() || !user.getEmail().contains("@")) {
            log.error("Электронная почта пустая или не содержит символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (null == user.getLogin() || "".equals(user.getLogin()) || user.getLogin().contains(" ")) {
            log.error("Логин пустой или содержит пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы;");
        }
        if (null == user.getName()) {
            user.setName(user.getLogin());
            log.info("Присвоено имя " + user.getName());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Не верная дата рождения");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
