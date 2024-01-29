package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.storage.db.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmControllerTests {

    private final JdbcTemplate jdbcTemplate;
    private FilmController filmController;

    @BeforeEach
    public void setUp() {
        LikesStorage likesStorage = new LikesDbStorage(jdbcTemplate);
        filmController = new FilmController(
                new FilmService(new FilmDbStorage(jdbcTemplate, new GenreDbStorage(jdbcTemplate)),
                        likesStorage));
    }

    @Test
    public void shouldAddFilm() {
        Film film = new Film("Титаник", "desc",
                LocalDate.of(1999, 12, 28), 90L, new Mpa(1, "G"));
        film.setGenres(Set.of(new Genre(1, "Комедия")));
        filmController.postFilm(film);
        assertThat(film)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    public void shouldAddTestFilm() {
        Film film = new Film("nisi eiusmod", "adipisicing",
                LocalDate.of(1967, 03, 25), 100L, new Mpa(1, "G"));
        film.setGenres(new HashSet<>());
        filmController.postFilm(film);
        assertEquals(film, filmController.getFilmById(film.getId()), "Фильм не добавлен");
    }

    @Test
    public void shouldNotAddEmptyFilmName() {
        Film film = new Film("", "desc",
                LocalDate.of(1999, 12, 28), 90L, new Mpa(1, "G"));
        Assertions.assertThrows(ValidationException.class, () -> filmController.postFilm(film));
    }

    @Test
    public void shouldNotAddDescriptionLength201() {
        Film film = new Film("Титаник", "В первом и последнем плавании шикарного «Титаника» " +
                "встречаются двое. Пассажир нижней палубы Джек выиграл билет в карты, а богатая наследница Роза " +
                "отправляется в Америку, чтобы выйти замуж по расчёту. Ч",
                LocalDate.of(1997, 11, 1), 194L, new Mpa(1, "G"));
        Assertions.assertThrows(ValidationException.class, () -> filmController.postFilm(film));
    }

    @Test
    public void shouldAddDescriptionLength200() {
        Film film = new Film("Титаник", "В первом и последнем плавании шикарного «Титаника» " +
                "встречаются двое. Пассажир нижней палубы Джек выиграл билет в карты, а богатая наследница Роза " +
                "отправляется в Америку, чтобы выйти замуж по расчёту. ",
                LocalDate.of(1997, 11, 1), 194L, new Mpa(1, "G"));
        filmController.postFilm(film);
    }

    @Test
    public void shouldNotAddDate1797() {
        Film film = new Film("Титаник", "Desc",
                LocalDate.of(1797, 11, 1), 194L, new Mpa(1, "G"));
        Assertions.assertThrows(ValidationException.class, () -> filmController.postFilm(film));
    }

    @Test
    public void shouldNotAddNegativeDuration() {
        Film film = new Film("Титаник", "Desc",
                LocalDate.of(1997, 11, 1), -194L, new Mpa(1, "G"));
        Assertions.assertThrows(ValidationException.class, () -> filmController.postFilm(film));
    }
}