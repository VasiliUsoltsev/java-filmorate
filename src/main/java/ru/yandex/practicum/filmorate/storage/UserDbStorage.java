package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ExceptionObjectNotFound;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Slf4j
@Component("userDbStorage")
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final UserRepository userRepository;

    public static final String EXCEPTION_TEXT_ID_USER_NOT_FOUND = "Пользователь не найден по идентификатору: ";

    @Override
    public UserDto createUser(NewUserRequest request) {
        User newUser = UserMapper.mapFromNewUserRequestToUser(request);
        log.debug("Создание пользователя: {}", newUser.getLogin() + " (email: " + newUser.getEmail() + ")");

        // Проверка логина на пробелы
        isValidLogin(newUser.getLogin());

        // имя для отображения может быть пустым — в таком случае будет использован логин
        if (isEmptyNameUser(newUser.getName())) {
            newUser.setName(newUser.getLogin());
        }

        // Добавляем нового пользователя
        newUser = userRepository.createUser(newUser);

        log.debug("Данные созданного пользователя: {}", newUser);

        return UserMapper.mapToUserDto(newUser);
    }

    @Override
    public UserDto updateUser(UpdateUserRequest request) {
        User updateUser = UserMapper.mapFromUpdateUserRequestToUser(request);

        log.debug("Изменение пользователя: {}", updateUser.getLogin() + " (email: " + updateUser.getEmail() + ")");

        if (updateUser.getId() == null) {
            throw new ValidationException("Не указан идентификатор пользователя");
        }

        Long userId = updateUser.getId();
        User oldUser = userRepository.findUserById(userId)
                .orElseThrow(() -> new ExceptionObjectNotFound(EXCEPTION_TEXT_ID_USER_NOT_FOUND, userId));

        // Проверка логина на пробелы
        isValidLogin(updateUser.getLogin());

        oldUser.setLogin(updateUser.getLogin());

        // имя для отображения может быть пустым — в таком случае будет использован логин
        if (isEmptyNameUser(updateUser.getName())) {
            oldUser.setName(updateUser.getLogin());
        } else {
            oldUser.setName(updateUser.getName());
        }

        oldUser.setEmail(updateUser.getEmail());

        oldUser.setBirthday(updateUser.getBirthday());

        updateUser = userRepository.updateUser(oldUser);

        log.debug("Измененные данные пользователя: {}", updateUser);

        return UserMapper.mapToUserDto(updateUser);
    }

    @Override
    public void removeUser(Long removeUserId) {
        boolean isRemove = userRepository.delUserById(removeUserId);
        if (!isRemove) {
            throw new ExceptionObjectNotFound(EXCEPTION_TEXT_ID_USER_NOT_FOUND, removeUserId);
        }
    }

    @Override
    public Collection<UserDto> getAll() {
        return userRepository.findUsersAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto getUser(Long id) {
        User user = getUserModel(id);

        return UserMapper.mapToUserDto(user);
    }

    @Override
    public User getUserModel(Long id) {
        User user = userRepository.findUserById(id).orElseThrow(
                () -> new ExceptionObjectNotFound(EXCEPTION_TEXT_ID_USER_NOT_FOUND, id));

        return user;
    }

    @Override
    public void addFriend(Long userId, Long friendUserId) {
        userRepository.addFriend(userId, friendUserId);
    }

    @Override
    public void delFriend(Long userId, Long friendUserId) {
        userRepository.delFriend(userId, friendUserId);
    }

    // Проверка корректности логина пользователя
    private boolean isValidLogin(String login) {
        if (login.contains(" ")) {
            throw new ValidationException("Логин пользователя содержит пробелы");
        }
        return true;
    }

    // Обработка имени пользователя
    public boolean isEmptyNameUser(String name) {
        return name == null || name.isBlank();
    }
}
