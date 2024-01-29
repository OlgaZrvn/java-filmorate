package ru.yandex.practicum.filmorate.storage.db;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@AllArgsConstructor
@Slf4j
@Component
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Mpa> getAllMpa() {
        log.info("Запрошены все mpa");
        return jdbcTemplate.query("SELECT * FROM mpa ORDER BY id", this::mapRowToMpa);
    }

    @Override
    public Mpa getMpaById(Integer id) {
        log.info("Запрошен mpa с id {}", id);
        Mpa mpa;
        try {
            mpa = jdbcTemplate.queryForObject("SELECT * FROM mpa WHERE id = ?", this::mapRowToMpa, id);
        } catch (Throwable e) {
            log.error("Mpa c id {} не найден", id);
            throw new NotFoundException("Mpa не найден");
        }
        return mpa;
    }

    @Override
    public boolean contains(int id) {
        try {
            getMpaById(id);
            return true;
        } catch (NotFoundException ex) {
            return false;
        }
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getInt("id"))
                .name(resultSet.getString("mpa"))
                .build();
    }
}