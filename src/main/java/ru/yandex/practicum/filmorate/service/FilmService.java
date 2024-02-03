package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.*;

import java.util.*;

@Slf4j
@Service
@AllArgsConstructor
public class FilmService {

    private final FilmStorage filmStorage;
    private final LikesStorage likesStorage;

    public Film save(Film film) {
        return filmStorage.save(film);
    }

    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(Integer id) {
        if (!filmStorage.contains(id)) {
            log.error("Фильм c id {} не найден", id);
            throw new NotFoundException("Фильм не найден");
        }
        return filmStorage.getById(id);
    }

    public Film update(Film film) {
        if (!filmStorage.contains(film.getId())) {
            log.error("Фильм c id {} не найден", film.getId());
            throw new NotFoundException("Фильм не найден");
        }
        return filmStorage.update(film);
    }

    public void addLike(Integer filmId, Integer userId) {
        likesStorage.addLike(filmId, userId);
    }

    public void deleteLike(Integer filmId, Integer userId) {
        likesStorage.removeLike(filmId, userId);
    }

    public Collection<Film> getPopular(Integer count, String genreId, String year) {
        return filmStorage.getPopular(count, genreId, year);
    }
}
