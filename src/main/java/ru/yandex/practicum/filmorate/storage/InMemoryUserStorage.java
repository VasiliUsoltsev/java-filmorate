package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ExceptionObjectNotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    // Хранение созданных пользователей
    private final Map<Long, User> users = new HashMap<>();

    public static final String EXCEPTION_TEXT_ID_USER_NOT_FOUND = "Пользователь не найден по идентификатору: ";

    @Override
    public User createUser(User newUser) {
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

    @Override
    public User updateUser(User updateUser) {
        log.debug("Изменение пользователя: {}", updateUser.getLogin() + " (email: " + updateUser.getEmail() + ")");

        if (updateUser.getId() == null) {
            throw new ValidationException("Не указан идентификатор пользователя");
        }

        User oldUser = users.get(updateUser.getId());

        if (oldUser != null && users.containsKey(updateUser.getId())) {
            // Проверка логина на пробелы
            isValidLogin(updateUser.getLogin());

            oldUser.setLogin(updateUser.getLogin());

            // имя для отображения может быть пустым — в таком случае будет использован логин
            if (isEmptyNameUser(updateUser.getName())) {
                oldUser.setName(updateUser.getLogin());
            } else {
                oldUser.setName(updateUser.getName());
            }

            oldUser.setEmail(updateUser.getEmail());

            oldUser.setBirthday(updateUser.getBirthday());
        } else {
            throw new ExceptionObjectNotFound(EXCEPTION_TEXT_ID_USER_NOT_FOUND, updateUser.getId());
        }
        log.debug("Измененные данные пользователя: {}", oldUser);
        return oldUser;
    }

    @Override
    public User removeUser(Long removeUserId) {
        User removeUser = users.get(removeUserId);

        if (removeUser != null) {
            users.remove(removeUserId);
            return removeUser;
        } else {
            throw new ExceptionObjectNotFound(EXCEPTION_TEXT_ID_USER_NOT_FOUND, removeUser.getId());
        }
    }

    @Override
    public Collection<User> getAll() {
        return users.values();
    }

    @Override
    public User getUser(Long id) {
        User user = users.get(id);

        if (user == null) {
            throw new ExceptionObjectNotFound(EXCEPTION_TEXT_ID_USER_NOT_FOUND, id);
        }

        return users.get(id);
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
}