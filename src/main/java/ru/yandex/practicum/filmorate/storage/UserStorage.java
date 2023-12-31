package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {

    List<User> getAll();

    User getById(Integer id);

    User save(User user);

    User update(User user);
}
