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

        if (newFilm.getReleaseDate() != null && isValidReleaseDate(newFilm.getReleaseDate())) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }

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

    // Проверка вадности даты релиза фильма
    private boolean isValidReleaseDate(LocalDate releaseDate) {
        return releaseDate.isBefore(LocalDate.of(1895, 12, 28));
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
            if (film.getReleaseDate() != null && isValidReleaseDate(film.getReleaseDate())) {
                throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
            }

            // Редактирование названия фильма
            if (film.getName() != null) {
                oldFilm.setName(film.getName());
            }

            // Редактирование даты релиза фильма
            if (film.getReleaseDate() != null) {
                oldFilm.setReleaseDate(film.getReleaseDate());
            }

            // Редактирование описания фильма
            if (film.getDescription() != null) {
                oldFilm.setDescription(film.getDescription());
            }

            // Редактирование продолжительности фильма
            if (film.getDuration() != null) {
                oldFilm.setDuration(film.getDuration());
            }
        } else {
            throw new ValidationException("Фильм под идентификатором " + film.getId() + " не найден");
        }

        log.debug("Измененные данные фильма: {}", oldFilm);

        return oldFilm;
    }
}
