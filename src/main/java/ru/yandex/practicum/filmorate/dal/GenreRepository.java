package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
public class GenreRepository extends BaseRepository<Genre> {
    private static final String FIND_GENRES_ALL_QUERY = "SELECT * FROM genre";
    private static final String FIND_GENRE_BY_ID_QUERY = "SELECT * FROM genre WHERE genre_id = ?";
    private static final String FIND_GENRES_BY_FILM_ID_QUERY = "SELECT g.genre_id, g.name " +
            "FROM genre g " +
            "JOIN film_multi_genre fmg ON g.genre_id=fmg.genre_id " +
            "WHERE fmg.film_id = ?";
    private static final String INSERT_FILM_MULTI_GENRE_QUERY = "INSERT INTO film_multi_genre(film_id, genre_id) " +
            "VALUES (?, ?)";
    private static final String DELETE_FILM_MULTI_GENRE_QUERY = "DELETE FROM film_multi_genre " +
            "WHERE film_id = ?";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> findGenresAll() {
        return findMany(FIND_GENRES_ALL_QUERY);
    }

    public Optional<Genre> findGenreById(Long genreId) {
        return findOne(FIND_GENRE_BY_ID_QUERY, genreId);
    }

    public List<Genre> findGenresByFilmId(Long filmId) {
        return findMany(FIND_GENRES_BY_FILM_ID_QUERY, filmId);
    }

    public boolean createFilmMultiGenre(Long filmId, Long genreId) {
        long id = insert(
                INSERT_FILM_MULTI_GENRE_QUERY,
                filmId,
                genreId
        );
        return id > 0;
    }

    public boolean deleteFilmMultiGenreAll(Long filmId) {
        return delete(
                DELETE_FILM_MULTI_GENRE_QUERY,
                filmId
        );
    }
}
