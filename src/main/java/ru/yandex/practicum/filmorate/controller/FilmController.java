package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    // Хранение созданных фильмов
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film newFilm) {
        log.debug("Создание фильма: {}", newFilm.getName() + "(" + newFilm.getReleaseDate() + ")");

        // Дата релиза не может быть раньше 28 декабря 1895 года
        isValidReleaseDate(newFilm.getReleaseDate());

        // Генерация идентификатора фильма
        newFilm.setId(getNextId());

        log.debug("Данные созданного фильма: {}", newFilm);

        // Добавляем новый фильм
        films.put(newFilm.getId(), newFilm);

        return newFilm;
    }

    // Генератор идетификатора фильма
    private long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    // Проверка валидности даты релиза фильма
    private boolean isValidReleaseDate(LocalDate releaseDate) {
        if (releaseDate != null && !releaseDate.isAfter(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        return true;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Изменение фильма: {}", film.getName() + "(" + film.getReleaseDate() + ")");
        log.debug("К нам пришло: {}", film);

        if (film.getId() == null) {
            throw new ValidationException("Не указан идентификатор фильма");
        }

        Film oldFilm = films.get(film.getId());

        log.debug("У нас хранилось: {}", oldFilm);

        if (oldFilm != null && films.containsKey(film.getId())) {
            // Дата релиза не может быть раньше 28 декабря 1895 года
            isValidReleaseDate(film.getReleaseDate());

            // Редактирование названия фильма
                oldFilm.setName(film.getName());

            // Редактирование даты релиза фильма
                oldFilm.setReleaseDate(film.getReleaseDate());

            // Редактирование описания фильма
                oldFilm.setDescription(film.getDescription());

            // Редактирование продолжительности фильма
                oldFilm.setDuration(film.getDuration());
        } else {
            throw new ValidationException("Фильм под идентификатором " + film.getId() + " не найден");
        }

        log.debug("Измененные данные фильма: {}", oldFilm);

        return oldFilm;
    }
}
