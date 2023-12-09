package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addAFriend(Integer id1, Integer id2) {
        User user1 = userStorage.getById(id1);
        User user2 = userStorage.getById(id2);
        user1.addFriend(id2);
        user2.addFriend(id1);
    }

    public void deleteFriend(Integer id1, Integer id2) {
        User user1 = userStorage.getById(id1);
        User user2 = userStorage.getById(id2);
        user1.deleteFriend(id2);
        user2.deleteFriend(id1);
    }

    public List<User> getAllFriends(Integer id) {
        User user = userStorage.getById(id);
        return user.getFriends().stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Integer id1, Integer id2) {
        User user1 = userStorage.getById(id1);
        User user2 = userStorage.getById(id2);
        return user1.getFriends().stream()
                .filter(user2.getFriends()::contains)
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }
}
