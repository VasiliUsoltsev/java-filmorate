package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaRepository extends BaseRepository<Mpa> {
    private static final String FIND_MPA_ALL_QUERY = "SELECT * FROM rating_film";
    private static final String FIND_MPA_BY_ID_QUERY = "SELECT * FROM rating_film WHERE rating_film_id = ?";

    public MpaRepository(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    public List<Mpa> findMpaAll() {
        return findMany(FIND_MPA_ALL_QUERY);
    }

    public Optional<Mpa> findMpaById(Long mpaId) {
        return findOne(FIND_MPA_BY_ID_QUERY, mpaId);
    }
}
