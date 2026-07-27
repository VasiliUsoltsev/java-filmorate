package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    Long id;

    @NotNull(message = "Электронная почта не указана")
    @NotBlank(message = "Электронная почта пользователя пуста")
    @Email(message = "Электронная почта указана некорректно")
    String email;

    @NotBlank(message = "Логин пользователя пуст")
    String login;

    String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    LocalDate birthday;
    Set<Long> friends;

    User() {
        friends = new HashSet<>();
    }
}