package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public User postUser(@Valid @RequestBody User user) {
        validate(user);
        userService.save(user);
        log.info("Добавлен новый пользователь {}", user.getName());
        return user;
    }

    @GetMapping
    public Optional<Collection<User>> getUsers() {
        return Optional.ofNullable(userService.getAll());
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Integer id) {
        if (userService.getById(id) != null) {
            return Optional.ofNullable(userService.getById(id));
        } else {
            log.error("Пользователь c id {} не найден", id);
            throw new NotFoundException("Пользователь не найден");
        }
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        validate(user);
        if (getUserById(user.getId()) != null) {
            return userService.update(user);
        } else {
            log.error("Не удалось обновить пользователя");
            throw new NotFoundException("Не удалось обновить пользователя. Пользователь не найден");
        }
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable @Positive Integer id, @PathVariable @Positive Integer friendId) {
        userService.addAFriend(id, friendId);
        log.info("{} подружился с {}", userService.getById(id).getName(), userService.getById(friendId).getName());
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable @Positive Integer id, @PathVariable @Positive Integer friendId) {
        User user = userService.getById(id);
        User friend = userService.getById(friendId);
        if (user == null || friend == null) {
            log.error("Пользователь с id {} не надйен", id);
            throw new NotFoundException("Пользователь не надйен");
        }
        userService.deleteFriend(id, friendId);
        log.info("{} больше не дружит с {}", user.getName(), friend.getName());
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable @Positive Integer id) {
        if (userService.getById(id) == null) {
            log.error("Пользователь с id {} не надйен", id);
            throw new NotFoundException("Пользователь не надйен");
        }
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable @Positive Integer id, @PathVariable @Positive Integer otherId) {
        User user = userService.getById(id);
        User friend = userService.getById(otherId);
        if (user == null || friend == null) {
            log.error("Пользователь с id {} не надйен", id);
            throw new NotFoundException("Пользователь не надйен");
        }
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