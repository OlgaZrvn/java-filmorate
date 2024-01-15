package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbStorageTests {

    private final JdbcTemplate jdbcTemplate;

   @Test
    public void shouldGetFilmById() {
        FilmDbStorage filmStorage = new FilmDbStorage(jdbcTemplate, new GenreDbStorage(jdbcTemplate));
        Film newFilm = new Film("Титаник", "В первом и последнем плавании шикарного «Титаника» " +
                "встречаются двое. Пассажир нижней палубы Джек выиграл билет в карты, а богатая наследница Роза " +
                "отправляется в Америку, чтобы выйти замуж по расчёту. ",
                LocalDate.of(1997, 11, 1), 194L, new Mpa(1, "G"));
        newFilm.setGenres(null);
        newFilm.setId(1);
        filmStorage.save(newFilm);
        Film savedFilm = filmStorage.getById(1);
        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(newFilm);
    }
}