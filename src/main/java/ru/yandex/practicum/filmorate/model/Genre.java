package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class Genre {
    private Long id;
    @EqualsAndHashCode.Exclude
    private String name;
}
