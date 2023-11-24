package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;

@RestController
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int generatorId = 0;

    @PostMapping("/films")
    public Film postFilm(@RequestBody Film film) {
        validate(film);
        film.setId(generatorId++);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм " + film.getName());
        return film;
    }

    @GetMapping("/films")
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @GetMapping("/films/{id}")
    public Film getFilms(@PathVariable Integer id) {
        return films.get(id);
    }

    @PutMapping("/films/{id}")
    public Film updateFilm(@RequestBody Film film) {
        validate(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Обновлен фильм " + film.getName());
        } else {
            film.setId(generatorId++);
            films.put(film.getId(), film);
            log.info("Добавлен новый фильм " + film.getName());
        }
        return film;
    }

    private static void validate(Film film) {
        LocalDate date = LocalDate.of(1895, 12, 28);
        if ("".equals(film.getName())) {
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
