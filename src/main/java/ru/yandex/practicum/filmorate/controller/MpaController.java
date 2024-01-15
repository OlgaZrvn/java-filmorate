package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import javax.validation.constraints.Positive;
import java.util.Collection;
import java.util.Optional;

@RestController
@Slf4j
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @GetMapping("/mpa")
    public Optional<Collection<Mpa>> getAllMpa() {
        return Optional.ofNullable(mpaService.getAllMpa());
    }

    @GetMapping("/mpa/{id}")
    public Optional<Mpa> getRatingById(@PathVariable @Positive Integer id) {
        if (mpaService.getMpaById(id) != null) {
            return Optional.ofNullable(mpaService.getMpaById(id));
        } else {
            log.error("Mpa c id {} не найден", id);
            throw new NotFoundException("Mpa не найден");
        }
    }
}