package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(Long id, Long friendId) {
        if (id.equals(friendId)) {
            throw new ValidationException("Идентификаторы пользователей не должны совпадать");
        }

        User user = userStorage.getUserModel(id);
        User userFriend = userStorage.getUserModel(friendId);

        userStorage.addFriend(id, friendId);
    }

    public void deleteFriend(Long id, Long friendId) {
        User user = userStorage.getUserModel(id);
        User userFriend = userStorage.getUserModel(friendId);

        userStorage.delFriend(id, friendId);
    }

    public Collection<UserDto> getFriendsAll(Long id) {
        User user = userStorage.getUserModel(id);

        return user.getFriends()
                .stream()
                .map(friendId -> userStorage.getUser(friendId.getFriendUser()))
                .toList();
    }

    public Collection<UserDto> getCommonFriends(Long id, Long otherId) {
        User user = userStorage.getUserModel(id);
        User userOther = userStorage.getUserModel(otherId);

        return user.getFriends().stream()
                .filter(userOther.getFriends()::contains)
                .map(friendId -> userStorage.getUser(friendId.getFriendUser()))
                .toList();
    }
}