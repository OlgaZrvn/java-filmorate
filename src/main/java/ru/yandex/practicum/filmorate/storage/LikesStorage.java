package ru.yandex.practicum.filmorate.storage;

import java.util.Collection;
import java.util.Set;

public interface LikesStorage {

    void addLike(int filmId, int userId);

    void removeLike(int filmId, int userId);

    Collection<Integer> getFilmLikes (int filmId);
}
