package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class FilmRepository extends BaseRepository<Film> {
    private final GenreRepository genreRepository;

    private static final String FIND_FILMS_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_FILM_BY_ID_QUERY = "SELECT * FROM films WHERE film_id = ?";
    private static final String DELETE_FILM_BY_ID_QUERY = "DELETE FROM films WHERE film_id = ?";
    private static final String UPDATE_FILM_BY_ID_QUERY = "UPDATE films SET name = ?, description = ?, " +
            "releaseDate = ?, duration = ?, rating_id = ? WHERE film_id = ?;";
    private static final String INSERT_FILM_QUERY = "INSERT INTO films(name, description, releaseDate, duration, " +
            "rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_ADD_LIKE_FILM_QUERY = "INSERT INTO film_likes(film_id, user_id) " +
            "VALUES (?, ?)";
    private static final String DELETE_LIKE_FILM_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";


    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper, GenreRepository genreRepository) {
        super(jdbc, mapper);
        this.genreRepository = genreRepository;
    }

    public List<Film> findFilmsAll() {
        return findMany(FIND_FILMS_ALL_QUERY);
    }

    public Optional<Film> findFilmById(Long filmId) {
        return findOne(FIND_FILM_BY_ID_QUERY, filmId);
    }

    public Film createFilm(Film film) {
        long id = insert(
                INSERT_FILM_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);

        return film;
    }

    public Film updateFilm(Film film) {
        update(UPDATE_FILM_BY_ID_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        // Очищаем старые данные фильма по жанрам
        genreRepository.deleteFilmMultiGenreAll(film.getId());

        return film;
    }

    public boolean delFilmByID(Long filmId) {
        return delete(DELETE_FILM_BY_ID_QUERY, filmId);
    }

    public boolean addLike(Long filmId, Long userId) {
        long id = insert(
                INSERT_ADD_LIKE_FILM_QUERY,
                filmId,
                userId
        );
        return id > 0;
    }

    public boolean delLike(Long filmId, Long userId) {
        return delete(
                DELETE_LIKE_FILM_QUERY,
                filmId,
                userId
        );
    }

    public Set<Long> getLikeFilmById(Long filmId) {
        String query = "SELECT user_id FROM film_likes WHERE film_id = " + filmId;

        return super.jdbc.queryForList(query, Long.class)
                .stream()
                .collect(Collectors.toSet());
    }
}
