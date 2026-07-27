package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public UserDto createUser(NewUserRequest newUser);

    public UserDto updateUser(UpdateUserRequest updateUser);

    public void removeUser(Long removeUserId);

    public Collection<UserDto> getAll();

    public UserDto getUser(Long id);

    public User getUserModel(Long id);

    public void addFriend(Long userId, Long friendUserId);

    public void delFriend(Long userId, Long friendUserId);
}
