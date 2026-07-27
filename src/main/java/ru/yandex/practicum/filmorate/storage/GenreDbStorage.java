package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.exception.ExceptionObjectNotFound;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GenreDbStorage {
    private final GenreRepository genreRepository;

    public static final String EXCEPTION_TEXT_ID_GENRE_NOT_FOUND = "Жанр не найден по идентификатору: ";

    public Genre findGenreById(Long genreId) {
        return genreRepository.findGenreById(genreId)
                .orElseThrow(() -> new ExceptionObjectNotFound(EXCEPTION_TEXT_ID_GENRE_NOT_FOUND, genreId));
    }

    public List<Genre> findGenresAll() {
        return genreRepository.findGenresAll();
    }
}
