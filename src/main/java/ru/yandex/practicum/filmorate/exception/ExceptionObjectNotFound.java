package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class ExceptionObjectNotFound extends RuntimeException {
    private final String message;
    private final Long id;

    public ExceptionObjectNotFound(String message, Long id) {
        super(message);
        this.message = message;
        this.id = id;
    }
}
