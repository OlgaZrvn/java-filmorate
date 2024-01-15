package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Component
@Qualifier("UserDbStorage")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<User> getAll() {
        log.info("Запрошены все пользователи");
        return jdbcTemplate.query("SELECT * FROM users", this::mapRowToUser);
    }

    @Override
    public User getById(Integer id) {
        log.info("Запрошен пользователь с id {}", id);
        User user;
        try {
            user = jdbcTemplate.queryForObject(
                "SELECT * FROM users WHERE id = ?",
                    this::mapRowToUser, id);
        } catch (NotFoundException e) {
            log.error("Пользователь c id {} не найден", id);
            throw new NotFoundException("Пользователь не найден");
        }
        return user;
    }

    @Override
    public User save(User user) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("id");
        int id = insert.executeAndReturnKey(userToMap(user)).intValue();
        user.setId(id);
        return user;
    }

    @Override
    public User update(User user) {
        jdbcTemplate.update(
                "UPDATE users SET email=?, login=?, name=?, birthday=? WHERE id = ?",
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                java.sql.Timestamp.valueOf(user.getBirthday().atStartOfDay()),
                user.getId());
        log.info("Пользователь успешно обновлен");
        return user;
    }

    @Override
    public List<User> getFriendsByUser(Integer userId) {
        return jdbcTemplate.query("SELECT * FROM users JOIN friendship ON users.id = friendship.friend_id " +
                "WHERE friendship.user_id = ?", this::mapRowToUser, userId);
    }

    @Override
    public boolean contains(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE id = ?", id);
        return userRows.next();
    }

    private static Map<String, Object> userToMap(User user) {
        return Map.of(
                "email", user.getEmail(),
                "login", user.getLogin(),
                "name", user.getName(),
                "birthday", user.getBirthday()
        );
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }
}