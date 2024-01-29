package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserService(UserStorage userStorage, FriendshipStorage friendshipStorage) {
        this.userStorage = userStorage;
        this.friendshipStorage = friendshipStorage;
    }

    public User save(User user) {
        return userStorage.save(user);
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(Integer id) {
        if (!userStorage.contains(id)) {
            log.error("Пользователь c id {} не найден", id);
            throw new NotFoundException("Пользователь не найден");
        }
        return userStorage.getById(id);
    }

    public User update(User user) {
        if (!userStorage.contains(user.getId())) {
            log.error("Пользователь c id {} не найден", user.getId());
            throw new NotFoundException("Пользователь не найден");
        }
        return userStorage.update(user);
    }

    public void addAFriend(Integer id1, Integer id2) {
        log.info("Получен запрос на добавление в друзья");
        if (!userStorage.contains(id1) || !userStorage.contains(id2)) {
            log.error("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        boolean accept = friendshipStorage.isAccepted(id1, id2);
        friendshipStorage.addFriend(id1, id2, accept);
    }

    public void deleteFriend(Integer id1, Integer id2) {
        log.info("Получен запрос на удаление из друзей");
        if (!userStorage.contains(id1) || !userStorage.contains(id2)) {
            log.error("Друг не найден");
            throw new NotFoundException("Друг не найден");
        }
        friendshipStorage.deleteFriend(id1, id2);
    }

    public List<User> getAllFriends(Integer id) {
        log.info("Получен запрос на получение всех друзей {}", getById(id).getName());
        log.info("Его друзья: {} ", userStorage.getFriendsByUser(id));
        return userStorage.getFriendsByUser(id);
    }

    public List<User> getCommonFriends(Integer id1, Integer id2) {
        log.info("Получен запрос на поиск общих друзей");
        if (!userStorage.contains(id1) || !userStorage.contains(id2)) {
            log.error("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        return getAllFriends(id1).stream().filter(getAllFriends(id2)::contains).collect(Collectors.toList());
    }
}