package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Component
@Qualifier("FilmDbStorage")
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;

    @Override
    public Collection<Film> getAll() {
        log.info("Запрошены все фильмы");
        return jdbcTemplate.query(
                "SELECT * FROM films", this::mapRowToFilm);
    }

    @Override
    public Film getById(Integer id) {
        log.info("Запрошен фильм с id {}", id);
        Film film;
        try {
            film = jdbcTemplate.queryForObject(
                    "SELECT * FROM films WHERE id = ?",
                    this::mapRowToFilm, id);
            film.setGenres(genreStorage.getGenreByFilmId(id));
        } catch (NotFoundException e) {
            log.error("Фильм c id {} не найден", id);
            throw new NotFoundException("Фильм не найден");
        }
        return film;
    }

    @Override
    public Film save(Film film) {
        log.info("Добавляем фильм {}", film.getName());
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingColumns("name", "description", "release_date", "duration", "mpa_id")
                .usingGeneratedKeyColumns("id");
        int id = insert.executeAndReturnKey(filmToMap(film)).intValue();
        film.setId(id);
        if (null != film.getGenres()) {
            genreStorage.addGenres(film.getId(), film.getGenres());
        } else {
            film.setGenres(new HashSet<>());
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        jdbcTemplate.update(
                "UPDATE films SET name=?, description=?, release_date=?, duration=?, mpa_id=? WHERE id = ?",
                film.getName(),
                film.getDescription(),
                java.sql.Timestamp.valueOf(film.getReleaseDate().atStartOfDay()),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        if (film.getGenres() != null) {
            genreStorage.updateGenres(film.getId(), film.getGenres());
        } else {
            film.setGenres(new HashSet<>());
        }
        log.info("Фильм успешно обновлен");
        return getById(film.getId());
    }

    @Override
    public boolean contains(int id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM films WHERE id = ?", id);
        return userRows.next();
    }

    @Override
    public Collection<Film> getPopular(int count, String genreId, String year) {
        Collection<Film> films = null;
        int id = 0;
        int y = 0;
        if (genreId == null) {
            if (year == null) {
                films = jdbcTemplate.query("SELECT *, COUNT(likes.film_id) as count FROM films " +
                        "LEFT JOIN likes ON films.id=likes.film_id " +
                        "GROUP BY films.id " +
                        "ORDER BY count DESC " +
                        "LIMIT ?", this::mapRowToFilm, count);
            } else {
                y = Integer.parseInt(year);
                log.info("Запрошены популярные фильмы {} года", y);
                films = jdbcTemplate.query("SELECT *, COUNT(likes.film_id) as count FROM films " +
                        "LEFT JOIN likes ON films.id=likes.film_id " +
                        "WHERE YEAR(release_date) = ?" +
                        "GROUP BY films.id " +
                        "ORDER BY count DESC " +
                        "LIMIT ?", this::mapRowToFilm, y, count);
            }
        } else {
            if (year == null) {
                id = Integer.parseInt(genreId);
                log.info("Запрошены популярные фильмы {} жанра", id);
                films = jdbcTemplate.query("SELECT *, COUNT(likes.film_id) as count FROM films " +
                        "LEFT JOIN likes ON films.id=likes.film_id " +
                        "LEFT JOIN film_genre ON films.id=film_genre.film_id " +
                        "WHERE film_genre.genre_id = ? " +
                        "GROUP BY films.id " +
                        "ORDER BY count DESC " +
                        "LIMIT ?", this::mapRowToFilm, id, count);
            } else {
                y = Integer.parseInt(year);
                log.info("Запрошены популярные фильмы {} года и {} жанра", y, id);
                films = jdbcTemplate.query("SELECT *, COUNT(likes.film_id) as count FROM films " +
                        "LEFT JOIN likes ON films.id=likes.film_id " +
                        "LEFT JOIN film_genre ON films.id=film_genre.film_id " +
                        "WHERE YEAR(release_date) = ? AND film_genre.genre_id = ? " +
                        "GROUP BY films.id " +
                        "ORDER BY count DESC " +
                        "LIMIT ?", this::mapRowToFilm, y, id, count);
            }
        }
        return films;
    }

    private static Map<String, Object> filmToMap(Film film) {
        return Map.of(
                "name", film.getName(),
                "description", film.getDescription(),
                "release_date", film.getReleaseDate(),
                "duration", film.getDuration(),
                "mpa_id", film.getMpa().getId()
        );
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        MpaStorage mpaStorage = new MpaDbStorage(jdbcTemplate);
        return Film.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(resultSet.getDate("release_date").toLocalDate())
                .duration(resultSet.getLong("duration"))
                .mpa(mpaStorage.getMpaById(resultSet.getInt("mpa_id")))
                .genres(genreStorage.getGenreByFilmId(resultSet.getInt("id")))
                .build();
    }
}