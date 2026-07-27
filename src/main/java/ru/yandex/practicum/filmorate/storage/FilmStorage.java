package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    public FilmDto createFilm(NewFilmRequest request);

    public FilmDto updateFilm(UpdateFilmRequest request);

    public void removeFilm(Long removeFilmId);

    public Collection<FilmDto> getAll();

    public Collection<Film> getAllModel();

    public FilmDto getFilm(Long id);

    public Film getFilmModel(Long id);

    public void addLike(Long filmId, Long userId);

    public void delLike(Long filmId, Long userId);

}
