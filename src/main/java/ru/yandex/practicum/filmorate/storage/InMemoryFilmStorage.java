package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int filmId = 1;

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getById(Integer id) {
        if (id < 0) {
            log.error("Отрицательный id");
            throw new NotFoundException("Отрицательный id");
        }
        if (!films.containsKey(id)) {
            log.error("Пользователь не найден");
            throw new NotFoundException("Пользователь не найден");
        }
        return films.get(id);
    }

    @Override
    public Film save(Film film) {
        film.setId(filmId++);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм обновлен {}", film.getName());
        } else if (film.getId() == null) {
            film.setId(filmId++);
            films.put(film.getId(), film);
            log.info("Добавлен новый фильм {}", film.getName());
        } else {
            throw new NotFoundException("Некорректный id");
        }
        return film;
    }
}