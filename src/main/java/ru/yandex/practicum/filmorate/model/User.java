package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class User {
    private Integer id;
    @Email
    private String email;
    @NotNull
    private String login;
    private String name;
    private LocalDate birthday;
}
