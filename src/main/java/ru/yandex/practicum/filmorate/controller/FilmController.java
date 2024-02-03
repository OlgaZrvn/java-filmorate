package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.util.*;

@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {

    private final FilmService filmService;

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        validate(film);
        filmService.save(film);
        log.info("Добавлен новый фильм {}", film.getName());
        return film;
    }

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable Integer id) {
            return filmService.getById(id);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        validate(film);
        if (filmService.getById(film.getId()) != null) {
            return filmService.update(film);
        } else {
            log.error("Не удалось обновить фильм");
            throw new NotFoundException("Не удалось обновить. Фильм не найден");
        }
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeFilm(@PathVariable("id") @PositiveOrZero Integer filmId,
                         @PathVariable("userId") @PositiveOrZero Integer userId) {
        if (filmId < 0 || userId < 0) {
            log.error("Неверный id");
            throw new NotFoundException("Неверный id");
        }
        log.info("Пользователь лайкнул фильм");
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlikeFilm(@PathVariable("id") @PositiveOrZero Integer filmId,
                           @PathVariable("userId") @PositiveOrZero Integer userId) {
         if (filmId < 0 || userId < 0) {
            log.error("Неверный id");
            throw new NotFoundException("Неверный id");
        }
        log.info("Пользователь отменил лайк фильму");
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count,
                                            @RequestParam(required = false) String genreId,
                                            @RequestParam(required = false) String year) {
        log.info("Выдан список {} популярных фильмов", count);
        return filmService.getPopular(count, genreId, year);
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