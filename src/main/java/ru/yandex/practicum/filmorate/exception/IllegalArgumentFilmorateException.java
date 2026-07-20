package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class IllegalArgumentFilmorateException extends IllegalArgumentException {
    private final String message;
    private final Long count;

    public IllegalArgumentFilmorateException(String message, Long count) {
        super(message);
        this.message = message;
        this.count = count;
    }
}
