package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder.Default;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@AllArgsConstructor
@Data
public class User {
    private Integer id;
    @Email
    private String email;
    @NotNull
    private String login;
    @Default
    private String name = login;
    private LocalDate birthday;
}
