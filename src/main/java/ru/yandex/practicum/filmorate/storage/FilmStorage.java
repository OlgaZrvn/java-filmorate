package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> getAll();

    Film getById(Integer id);

    Film save(Film film);

    Film update(Film film);

    boolean contains(int id);

    Collection<Film> getPopular(int count);
}