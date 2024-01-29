package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface FriendshipStorage {

    void addFriend(int userId, int friendId, boolean accept);

    void deleteFriend(int userId, int friendId);

    Collection<Integer> getFriends(int userId);

    boolean isAccepted(int userId, int friendId);
}
