package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public void addFriend(Long id, Long friendId) {
        if (id.equals(friendId)) {
            throw new ValidationException("Идентификаторы пользователей не должны совпадать");
        }

        User user = userStorage.getUser(id);
        User userFriend = userStorage.getUser(friendId);

        user.getFriends().add(friendId);
        userFriend.getFriends().add(id);
    }

    public void deleteFriend(Long id, Long friendId) {
        User user = userStorage.getUser(id);
        User userFriend = userStorage.getUser(friendId);

        user.getFriends().remove(friendId);
        userFriend.getFriends().remove(id);
    }

    public Collection<User> getFriendsAll(Long id) {
        User user = userStorage.getUser(id);

        return user.getFriends().stream()
                .map(friendId -> userStorage.getUser(friendId))
                .collect(Collectors.toList());
    }

    public Collection<User> getCommonFriends(Long id, Long otherId) {
        User user = userStorage.getUser(id);
        User userOther = userStorage.getUser(otherId);

        return user.getFriends().stream()
                .filter(userOther.getFriends()::contains)
                .map(friendId -> userStorage.getUser(friendId))
                .collect(Collectors.toList());
    }
}