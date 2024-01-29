package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
public class User {
    private Integer id;
    @Email
    private String email;
    @NotBlank
    private String login;
    @NotBlank
    private String name;
    @PastOrPresent
    private LocalDate birthday;
}
