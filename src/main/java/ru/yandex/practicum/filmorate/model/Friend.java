package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
public class Friend {
    Long friendUser;
    @EqualsAndHashCode.Exclude
    Integer status;
}
