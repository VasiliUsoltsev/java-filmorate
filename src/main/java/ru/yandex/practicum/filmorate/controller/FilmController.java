package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAll() {
        return filmStorage.getAll();
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable Long id) {
        return filmStorage.getFilm(id);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilm(
            @RequestParam(defaultValue = "10")
            @Positive(message = "Недопустимое значение параметра") Long count
    ) {
        return filmService.getPopularFilm(count);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film newFilm) {
        return filmStorage.createFilm(newFilm);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film updateFilm) {
        return filmStorage.updateFilm(updateFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateLikeToFilm(@PathVariable Long id,
                                 @PathVariable Long userId
    ) {
        filmService.addLikeToFilm(id, userId);
    }

    @DeleteMapping("/{removeFilmId}")
    public Film delete(@PathVariable() Long removeFilmId) {
        return filmStorage.removeFilm(removeFilmId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLikeToFilm(@PathVariable Long id,
                                 @PathVariable Long userId
    ) {
        filmService.deleteLikeToFilm(id, userId);
    }
}