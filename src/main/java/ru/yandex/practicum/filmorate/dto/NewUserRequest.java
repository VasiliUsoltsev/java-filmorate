package ru.yandex.practicum.filmorate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NewUserRequest {
    @NotBlank(message = "Электронная почта пользователя пуста")
    @Email(message = "Электронная почта указана некорректно")
    private String email;

    @NotBlank(message = "Логин пользователя пуст")
    private String login;
    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
