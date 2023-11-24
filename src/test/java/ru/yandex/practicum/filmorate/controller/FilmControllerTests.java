package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

public class FilmControllerTests {

    private FilmController filmController;

    @BeforeEach
    public void setUp() {
        filmController = new FilmController();
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

}
