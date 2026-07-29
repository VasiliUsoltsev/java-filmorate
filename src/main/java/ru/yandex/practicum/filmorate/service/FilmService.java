package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.swing.*;
import java.util.Collection;
import java.util.Comparator;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage, @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLikeToFilm(Long filmId, Long userId) {
        Film film = filmStorage.getFilmModel(filmId);
        User user = userStorage.getUserModel(userId);

        filmStorage.addLike(filmId, userId);
    }

    public void deleteLikeToFilm(Long filmId, Long userId) {
        Film film = filmStorage.getFilmModel(filmId);
        User user = userStorage.getUserModel(userId);

        filmStorage.delLike(filmId, userId);
    }

    public Collection<FilmDto> getPopularFilm(Long count) {
        return filmStorage.getAllModel()
                .stream()
                .sorted(Comparator.reverseOrder())
                .limit(count)
                .map(FilmMapper::mapToUserDto)
                .toList();
    }
}