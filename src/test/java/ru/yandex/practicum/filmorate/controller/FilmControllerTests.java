package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilmControllerTests {

    private FilmController filmController;
    private UserController userController;

    @BeforeEach
    public void setUp() {
        FilmStorage filmStorage = new InMemoryFilmStorage();
        UserStorage userStorage = new InMemoryUserStorage();
        filmController = new FilmController(
                filmStorage,
                new FilmService(filmStorage, userStorage)
                );
        userController = new UserController(
                userStorage,
                new UserService(userStorage)
        );
    }

    @Test
    public void shouldNotAddEmptyFilmName() {
        Film film = new Film(0, "", "desc",
                LocalDate.of(1999, 12, 28), 90L);
        Assertions.assertThrows(ValidationException.class, () -> filmController.postFilm(film));
    }

    @Test
    public void shouldNotAddDescriptionLength201() {
        Film film = new Film(0, "Титаник", "В первом и последнем плавании шикарного «Титаника» " +
                "встречаются двое. Пассажир нижней палубы Джек выиграл билет в карты, а богатая наследница Роза " +
                "отправляется в Америку, чтобы выйти замуж по расчёту. Ч",
                LocalDate.of(1997, 11, 1), 194L);
        Assertions.assertThrows(ValidationException.class, () -> filmController.postFilm(film));
    }

    @Test
    public void shouldAddDescriptionLength200() {
        Film film = new Film(0, "Титаник", "В первом и последнем плавании шикарного «Титаника» " +
                "встречаются двое. Пассажир нижней палубы Джек выиграл билет в карты, а богатая наследница Роза " +
                "отправляется в Америку, чтобы выйти замуж по расчёту. ",
                LocalDate.of(1997, 11, 1), 194L);
        filmController.postFilm(film);
    }

    @Test
    public void shouldNotAddDate1797() {
        Film film = new Film(0, "Титаник", "Desc",
                LocalDate.of(1797, 11, 1), 194L);
        Assertions.assertThrows(ValidationException.class, () -> filmController.postFilm(film));
    }

    @Test
    public void shouldNotAddNegativeDuration() {
        Film film = new Film(0, "Титаник", "Desc",
                LocalDate.of(1997, 11, 1), -194L);
        Assertions.assertThrows(ValidationException.class, () -> filmController.postFilm(film));
    }

    @Test
    public void shouldAddLike() {
        Film film = new Film(1, "Титаник", "Desc",
                LocalDate.of(1997, 11, 1), 194L);
        filmController.postFilm(film);
        User user1 = new User(1, "Email1@yandex.ru", "Login1", "Name1",
                LocalDate.of(1999, 12, 28));
        userController.postUser(user1);
        filmController.likeFilm(1, 1);
        Set<Integer> likes = new HashSet<>();
        likes.add(1);
        assertEquals(likes, film.getLikes(), "Лайк не добавлен");
    }

}
