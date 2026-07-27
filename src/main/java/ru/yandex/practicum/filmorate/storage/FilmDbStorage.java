package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.exception.ExceptionObjectNotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Component("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final FilmRepository filmRepository;
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;

    private static final String EXCEPTION_TEXT_ID_FILM_NOT_FOUND = "Фильм не найден по идентификатору: ";
    private static final String EXCEPTION_TEXT_ID_MPA_NOT_FOUND = "Возрастной рейтинг не найден по идентификатору: ";
    private static final String EXCEPTION_TEXT_ID_GENRE_NOT_FOUND = "Жанр не найден по идентификатору: ";

    @Override
    public FilmDto createFilm(NewFilmRequest request) {
        Film newFilm = FilmMapper.mapFromNewFilmRequestToFilm(request);
        log.debug("Создание фильма: {}", newFilm.getName() + "(" + newFilm.getReleaseDate() + ")");

        // Дата релиза не может быть раньше 28 декабря 1895 года
        isValidReleaseDate(newFilm.getReleaseDate());

        // Добавление возрастного рейтинга
        if (newFilm.getMpa() != null) {
            Long mpaId = newFilm.getMpa().getId();
            Mpa newMpa = mpaRepository.findMpaById(mpaId)
                    .orElseThrow(() -> new ExceptionObjectNotFound(EXCEPTION_TEXT_ID_MPA_NOT_FOUND, mpaId));
            newFilm.setMpa(newMpa);
        }

        // Добавляем новый фильм
        newFilm = filmRepository.createFilm(newFilm);

        // Добавление жанров
        Long filmId = newFilm.getId();
        List<Genre> listGenre = newFilm.getGenres();
        // Также идет чистка фильма от дубликатов жанров
        listGenre = addAllGenreByFilm(filmId, GenreMapper.mapToListLong(listGenre));
        newFilm.setGenres(listGenre);

        return FilmMapper.mapToUserDto(newFilm);
    }

    @Override
    public FilmDto updateFilm(UpdateFilmRequest request) {
        Film updateFilm = FilmMapper.mapFromUpdateFilmRequestToFilm(request);

        log.info("Изменение фильма: {}", updateFilm.getName() + "(" + updateFilm.getReleaseDate() + ")");


        if (updateFilm.getId() == null) {
            throw new ValidationException("Не указан идентификатор фильма");
        }

        Long filmId = updateFilm.getId();
        Film oldFilm = filmRepository.findFilmById(filmId)
                .orElseThrow(() -> new ExceptionObjectNotFound(EXCEPTION_TEXT_ID_FILM_NOT_FOUND, filmId));

        log.debug("У нас хранилось: {}", oldFilm);

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

        // Редактирование возрастного рейтинга
        oldFilm.setMpa(updateFilm.getMpa());

        // Редактирование списка жанров и проверка на дубликаты жанров
        // Очищаем старые жанры
        delAllGenreByFilm(filmId);
        //Добавляем новые жанры
        List<Genre> listGenre = addAllGenreByFilm(filmId, GenreMapper.mapToListLong(updateFilm.getGenres()));
        oldFilm.setGenres(listGenre);

        updateFilm = filmRepository.updateFilm(oldFilm);

        log.debug("Измененные данные фильма: {}", updateFilm);

        return FilmMapper.mapToUserDto(updateFilm);
    }

    @Override
    public void removeFilm(Long removeFilmId) {
        boolean isRemove = filmRepository.delFilmByID(removeFilmId);
        if (isRemove) {
            throw new ExceptionObjectNotFound(EXCEPTION_TEXT_ID_FILM_NOT_FOUND, removeFilmId);
        }
    }

    @Override
    public Collection<FilmDto> getAll() {
        return getAllModel()
                .stream()
                .map(FilmMapper::mapToUserDto)
                .toList();
    }

    @Override
    public Collection<Film> getAllModel() {
        List<Film> films = filmRepository.findFilmsAll();

        for (Film film : films) {
            // Обогащаем данными о жанрах
            List<Genre> genres = genreRepository.findGenresByFilmId(film.getId());
            film.setGenres(genres);

            // Обогащаем данными о возрастном рейтинге
            Long mpaId = film.getMpa().getId();
            Mpa mpa = mpaRepository.findMpaById(mpaId)
                    .orElseThrow(() -> new ExceptionObjectNotFound(EXCEPTION_TEXT_ID_MPA_NOT_FOUND, mpaId));
            film.setMpa(mpa);

            // Обогащаем данными о лайках
            Set<Long> listLike = filmRepository.getLikeFilmById(film.getId());
            film.setLikes(listLike);
        }

        return films;
    }

    @Override
    public FilmDto getFilm(Long id) {
        Film film = getFilmModel(id);

        return FilmMapper.mapToUserDto(film);
    }

    @Override
    public Film getFilmModel(Long id) {
        Film film = filmRepository.findFilmById(id).orElseThrow(
                () -> new ExceptionObjectNotFound(EXCEPTION_TEXT_ID_FILM_NOT_FOUND, id));

        List<Genre> genres = genreRepository.findGenresByFilmId(id);
        film.setGenres(genres);

        Long mpaId = film.getMpa().getId();
        Mpa mpa = mpaRepository.findMpaById(mpaId).
                orElseThrow(() -> new ExceptionObjectNotFound(EXCEPTION_TEXT_ID_MPA_NOT_FOUND, mpaId));
        film.setMpa(mpa);

        // Обогащаем данными о лайках
        Set<Long> listLike = filmRepository.getLikeFilmById(film.getId());
        film.setLikes(listLike);
        return film;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        filmRepository.addLike(filmId, userId);
    }

    @Override
    public void delLike(Long filmId, Long userId) {
        filmRepository.delLike(filmId, userId);
    }

    // Проверка валидности даты релиза фильма
    private boolean isValidReleaseDate(LocalDate releaseDate) {
        if (releaseDate != null && !releaseDate.isAfter(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        return true;
    }

    // Добавление связи фильма и жанра во вспомогательной таблице
    private List<Genre> addAllGenreByFilm(Long filmId, List<Long> list) {
        if (list != null) {
            List<Genre> result = new ArrayList<>();
            // Убираем дубликаты
            list = list.stream()
                    .distinct()
                    .toList();
            for (Long curGenreId : list) {
                // Наполняем массив уникальными жанрами данного фильма
                Genre genre = genreRepository.findGenreById(curGenreId)
                        .orElseThrow(() -> new ExceptionObjectNotFound(EXCEPTION_TEXT_ID_GENRE_NOT_FOUND, curGenreId));

                // вносим данные во вспомогательную таблицу, только после наполнения массива
                genreRepository.createFilmMultiGenre(filmId, curGenreId);

                result.add(genre);
            }
            return result;
        } else {
            return new ArrayList<>();
        }
    }

    private void delAllGenreByFilm(Long filmId) {
        genreRepository.deleteFilmMultiGenreAll(filmId);
    }
}
