package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.*;

@RestController
@Slf4j
public class FilmController {

    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @PostMapping("/films")
    public Film postFilm(@RequestBody Film film) {
        validate(film);
        filmStorage.save(film);
        log.info("Добавлен новый фильм {}", film.getName());
        return film;
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        return filmStorage.getAll();
    }

    @GetMapping("/films/{id}")
    public Film getFilmById(@PathVariable Integer id) {
        return filmStorage.getById(id);
    }

    @PutMapping("/films")
    public Film updateFilm(@RequestBody Film film) {
        validate(film);
        filmStorage.update(film);
        return film;
    }

    @PutMapping("/films/{id}/like/{userId}")
    public void likeFilm(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        log.info("Пользователь лайкнул фильм {}", filmStorage.getById(filmId).getName());
        validate(filmStorage.getById(filmId));
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/films/{id}/like/{userId}")
    public void unlikeFilm(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        log.info("Пользователь отменил лайк фильму {}", filmStorage.getById(filmId).getName());
        validate(filmStorage.getById(filmId));
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/films/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        log.info("Выдан список {} популярных фильмов", count);
        return filmService.getPopular(count);
    }

    private static void validate(Film film) {
        LocalDate date = LocalDate.of(1895, 12, 28);
        if (film.getName().isEmpty()) {
            log.error("Название фильма пустое");
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200L) {
            log.error("Превышена максимальная длина описания");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(date)) {
            log.error("Не верная дата релиза");
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.error("Отрицательная продолжительность фильма");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }
}
