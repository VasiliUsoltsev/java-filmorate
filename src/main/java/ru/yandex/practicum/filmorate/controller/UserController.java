package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    // Хранение созданных пользователей
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {

        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User newUser) {
        log.debug("Создание пользователя: {}", newUser.getLogin() + " (email: " + newUser.getEmail() + ")");

        // Проверка логина на пробелы
        isValidLogin(newUser.getLogin());

        // Генерация идентификатора пользователя
        newUser.setId(getNextId());

        // имя для отображения может быть пустым — в таком случае будет использован логин
        if (isEmptyNameUser(newUser.getName())) {
            newUser.setName(newUser.getLogin());
        }

        log.debug("Данные созданного пользователя: {}", newUser);

        // Добавляем нового пользователя
        users.put(newUser.getId(), newUser);

        return newUser;
    }

    // Генерация идетификатора пользователя
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    // Проверка корректности логина пользователя
    private boolean isValidLogin(String login) {
        if (login.contains(" ")) {
            throw new ValidationException("Логин пользователя содержит пробелы");
        }
        return true;
    }

    // Обработка имени пользователя
    public boolean isEmptyNameUser(String name) {
        return name == null || name.isBlank();
    }


    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.debug("Изменение пользователя: {}", user.getLogin() + " (email: " + user.getEmail() + ")");

        if (user.getId() == null) {
            throw new ValidationException("Не указан идентификатор пользователя");
        }

        User oldUser = users.get(user.getId());

        if (oldUser != null && users.containsKey(user.getId())) {
            // Проверка логина на пробелы
            isValidLogin(user.getLogin());

            oldUser.setLogin(user.getLogin());

            // имя для отображения может быть пустым — в таком случае будет использован логин
            if (isEmptyNameUser(user.getName())) {
                oldUser.setName(user.getLogin());
            } else {
                oldUser.setName(user.getName());
            }

            oldUser.setEmail(user.getEmail());

            if (user.getBirthday() != null) {
                oldUser.setBirthday(user.getBirthday());
            }
        } else {
            throw new ValidationException("Идентификатор пользователя не найден");
        }
        log.debug("Измененные данные пользователя: {}", user);
        return oldUser;
    }
}
