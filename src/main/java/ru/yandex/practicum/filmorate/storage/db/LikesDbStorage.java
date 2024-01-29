package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.LikesStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Component
public class LikesDbStorage implements LikesStorage {

    @Autowired
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addLike(int filmId, int userId) {
        jdbcTemplate.update(
                "INSERT INTO likes SET film_id = ?, user_id = ?", filmId, userId);
        log.info("Лайк успешно добавлен");
    }

    @Override
    public void removeLike(int filmId, int userId) {
            jdbcTemplate.update(
                    "DELETE FROM likes WHERE film_id = ? AND user_id = ?", filmId, userId);
            log.info("Лайк успешно удален");
    }

    @Override
    public Collection<Integer> getFilmLikes(int filmId) {
        return jdbcTemplate.query("SELECT user_id FROM likes WHERE film_id=?", this::mapRowToLike, filmId)
                .stream()
                .map(Like::getFilmId)
                .collect(Collectors.toList());
    }

    private Like mapRowToLike(ResultSet rs, int rowNum) throws SQLException {
        Like like = new Like();
        like.setFilmId(rs.getInt("film_id"));
        like.setUserId(rs.getInt("user_id"));
        return like;
    }
}