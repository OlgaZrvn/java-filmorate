package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Component
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Genre> getAllGenres() {
        log.info("Запрошены все жанры");
        return jdbcTemplate.query("SELECT * FROM genres ORDER BY id", this::mapRowGenre);
    }

    @Override
    public Genre getGenreById(Integer id) {
        log.info("Запрошен жанр с id {}", id);
        return jdbcTemplate.queryForObject("SELECT * FROM genres WHERE id = ?", this::mapRowGenre, id);
    }

    @Override
    public Set<Genre> getGenreByFilmId(int filmId) {
        log.info("Запрошен жанр фильма с id {}", filmId);
        return new HashSet<>(jdbcTemplate.query("SELECT * FROM genres INNER JOIN film_genre " +
                    "ON genres.id = film_genre.genre_id " +
                    "WHERE film_genre.film_id = ? ORDER BY genres.id", this::mapRowGenre, filmId));
    }

    @Override
    public void addGenres(int filmId, Set<Genre> genres) {
        for (Genre genre : genres) {
            jdbcTemplate.update("INSERT INTO film_genre VALUES (?, ?)", filmId, genre.getId());
        }
    }

    @Override
    public boolean contains(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM genres WHERE id = ?", id);
        return userRows.next();
    }

    @Override
    public void updateGenres(Integer filmId, Set<Genre> genres) {
        jdbcTemplate.update("DELETE FROM film_genre WHERE film_id = ?", filmId);
        for (Genre genre : genres) {
            jdbcTemplate.update("INSERT INTO film_genre VALUES (?, ?)", filmId, genre.getId());
        }
    }

    private Genre mapRowGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("id"))
                .name(rs.getString("genre"))
                .build();
    }
}