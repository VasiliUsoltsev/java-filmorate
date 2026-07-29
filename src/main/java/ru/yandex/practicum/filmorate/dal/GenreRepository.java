package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

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

    public Map<Long, Set<Genre>> findGenresByArrayFilmId(List<Long> listFilmsId) {
        if (listFilmsId == null || listFilmsId.isEmpty()) {
            return Collections.EMPTY_MAP;
        }

        String parametr = listFilmsId.stream()
                .map(id -> "?")
                .collect(Collectors.joining(", "));

        String query = "SELECT fmg.film_id, g.genre_id, g.name " +
                "FROM genre g " +
                "JOIN film_multi_genre fmg ON g.genre_id=fmg.genre_id " +
                "WHERE fmg.film_id IN (" + parametr + ")";
        Map<Long, Set<Genre>> result = new HashMap<>();
        super.jdbc.query(query, (ResultSet rs) -> {
            while (rs.next()) {
                Long filmId = rs.getLong("film_id");
                Genre genre = new Genre();
                genre.setId(rs.getLong("genre_id"));
                genre.setName(rs.getString("name"));

                result.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
            }
            return result;
        }, listFilmsId.toArray());

        return result;
    }
}

