package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {

    Collection<User> getAll();

    User getById(Integer id);

    User save(User user);

    User update(User user);

    List<User> getFriendsByUser(Integer id);

    boolean contains(int id);
}
