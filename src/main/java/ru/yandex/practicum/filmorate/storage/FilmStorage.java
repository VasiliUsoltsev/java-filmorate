package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    public Film createFilm(Film newFilm);

    public Film updateFilm(Film updateFilm);

    public Film removeFilm(Long removeFilmId);

    public Collection<Film> getAll();

    public Film getFilm(Long id);
}
