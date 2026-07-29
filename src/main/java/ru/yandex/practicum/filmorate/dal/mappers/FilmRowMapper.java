package ru.yandex.practicum.filmorate.dal.mappers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {
    private final GenreRepository genreRepository;
    private final MpaRepository mpaRepository;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getTimestamp("releaseDate").toLocalDateTime().toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpa(getMpaByFilmId(rs.getLong("rating_id")));

        return film;
    }

    private Mpa getMpaByFilmId(Long mpaId) {
        Mpa mpa = new Mpa();
        mpa.setId(mpaId);
        return mpa;
    }
}
