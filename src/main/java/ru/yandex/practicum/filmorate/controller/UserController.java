package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
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
    private int generatorId = 0;
    @PostMapping("/users")
    public User postUser(@RequestBody User user) {
        validate(user);
        user.setId(generatorId++);
        users.put(user.getId(), user);
        log.info("Добавлен новый пользователь " + user.getLogin());
        return user;
    }
    @GetMapping("/users")
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
    @GetMapping("/users/{id}")
    public User getUsers(@PathVariable Integer id) {
        return users.get(id);
    }

    @PutMapping("/users/{id}")
    public User updateUser(@RequestBody User user) {
        validate(user);
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            log.info("Обновлен пользователь " + user.getLogin());
        } else {
            user.setId(generatorId++);
            users.put(user.getId(), user);
            log.info("Добавлен новый пользователь " + user.getLogin());
        }
        return user;
    }
    private static void validate(User user) {
        if ("".equals(user.getEmail()) || !user.getEmail().contains("@")) {
            log.error("Электронная почта пустая или не содержит символ @");
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if ("".equals(user.getLogin()) || user.getLogin().contains(" ")) {
            log.error("Логин пустой или содержит пробелы");
            throw new ValidationException("Логин не может быть пустым и содержать пробелы;");
        }
        if ("".equals(user.getName())) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Не верная дата рождения");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
