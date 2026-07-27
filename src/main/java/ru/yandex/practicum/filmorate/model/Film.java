package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film implements Comparable<Film> {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Set<Long> likes;
    private List<Genre> genres;
    private Mpa mpa;

    public Film() {
        likes = new HashSet<>();
        genres = new ArrayList<>();
    }

    @Override
    public int compareTo(Film o) {
        return Integer.compare(this.getLikes().size(), o.getLikes().size());
    }
}