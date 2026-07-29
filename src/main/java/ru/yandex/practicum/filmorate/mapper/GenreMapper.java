package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GenreMapper {
    public static List<Genre> mapToListGenre(List<Long> list) {
        List<Genre> listGenre = new ArrayList<>();
        for (Long cur : list) {
            Genre genre = new Genre();
            genre.setId(cur);
            listGenre.add(genre);
        }

        return listGenre;
    }

    public static List<Long> mapToListLong(List<Genre> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream()
                .map(genre -> genre.getId())
                .toList();
    }
}
