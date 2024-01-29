package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;

public interface LikesStorage {

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    Collection<Integer> getFilmLikes(int filmId);
}