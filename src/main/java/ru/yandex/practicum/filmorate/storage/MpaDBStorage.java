package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.exception.ExceptionObjectNotFound;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class MpaDBStorage {
    private final MpaRepository mpaRepository;

    private static final String EXCEPTION_TEXT_ID_MPA_NOT_FOUND = "Возрастной рейтинг не найден по идентификатору: ";

    public Mpa getMpa(Long mpaId) {
        return mpaRepository.findMpaById(mpaId)
                .orElseThrow(() -> new ExceptionObjectNotFound(EXCEPTION_TEXT_ID_MPA_NOT_FOUND, mpaId));
    }

    public Collection<Mpa> getAllMpa() {
        return mpaRepository.findMpaAll();
    }
}
