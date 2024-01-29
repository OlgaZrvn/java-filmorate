package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addFriend(int userId, int friendId, boolean accept) {
        jdbcTemplate.update("INSERT INTO friendship VALUES (?, ?, ?)", userId, friendId, accept);
        log.info("Друг успешно добавлен");
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        jdbcTemplate.update("DELETE FROM friendship WHERE user_id = ? AND friend_id = ?", userId, friendId);
        log.info("Друг успешно удален");
    }

    @Override
    public Collection<Integer> getFriends(int userId) {
        return jdbcTemplate.query("SELECT * FROM friendship WHERE user_id=?", this::mapRowFriendship, userId)
                .stream()
                .map(Friendship::getUserId)
                .collect(Collectors.toList());
    }

        @Override
    public boolean isAccepted(int userId, int friendId) {
        boolean isAccepted;
        try {
            jdbcTemplate.queryForObject("SELECT * FROM friendship WHERE user_id=? AND friend_id=?",
                    this::mapRowFriendship, userId, friendId);
            isAccepted = true;
        } catch (RuntimeException e) {
            isAccepted = false;
        }
        return isAccepted;
    }

    private Friendship mapRowFriendship(ResultSet rs, int rowNum) throws SQLException {
        Friendship friendship = new Friendship();
        friendship.setUserId(rs.getInt("user_id"));
        friendship.setFriendId(rs.getInt("friend_id"));
        friendship.setAccept(rs.getBoolean("accept"));
        return friendship;
    }
}