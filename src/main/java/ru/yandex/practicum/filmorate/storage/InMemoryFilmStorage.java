package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ExceptionObjectNotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    // Хранение созданных фильмов
    private final Map<Long, Film> films = new HashMap<>();

    private static final String EXCEPTION_TEXT_ID_FILM_NOT_FOUND = "Фильм не найден по идентификатору: ";


    @Override
    public Film createFilm(Film newFilm) {
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

    @Override
    public Film updateFilm(Film updateFilm) {
        log.info("Изменение фильма: {}", updateFilm.getName() + "(" + updateFilm.getReleaseDate() + ")");
        log.debug("К нам пришло: {}", updateFilm);

        if (updateFilm.getId() == null) {
            throw new ValidationException("Не указан идентификатор фильма");
        }

        Film oldFilm = films.get(updateFilm.getId());

        log.debug("У нас хранилось: {}", oldFilm);

        if (oldFilm != null && films.containsKey(updateFilm.getId())) {
            // Дата релиза не может быть раньше 28 декабря 1895 года
            isValidReleaseDate(updateFilm.getReleaseDate());

            // Редактирование названия фильма
            oldFilm.setName(updateFilm.getName());

            // Редактирование даты релиза фильма
            oldFilm.setReleaseDate(updateFilm.getReleaseDate());

            // Редактирование описания фильма
            oldFilm.setDescription(updateFilm.getDescription());

            // Редактирование продолжительности фильма
            oldFilm.setDuration(updateFilm.getDuration());
        } else {
            throw new ExceptionObjectNotFound(EXCEPTION_TEXT_ID_FILM_NOT_FOUND, updateFilm.getId());
        }

        log.debug("Измененные данные фильма: {}", oldFilm);

        return oldFilm;
    }

    @Override
    public Film removeFilm(Long removeFilmId) {
        Film removeFilm = films.get(removeFilmId);

        if (removeFilm != null) {
            films.remove(removeFilmId);
            return removeFilm;
        } else {
            throw new ExceptionObjectNotFound(EXCEPTION_TEXT_ID_FILM_NOT_FOUND, removeFilm.getId());
        }
    }

    @Override
    public Collection<Film> getAll() {
        return films.values();
    }

    @Override
    public Film getFilm(Long id) {
        Film film = films.get(id);

        if (film == null) {
            throw new ExceptionObjectNotFound(EXCEPTION_TEXT_ID_FILM_NOT_FOUND, id);
        }

        return film;
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
}