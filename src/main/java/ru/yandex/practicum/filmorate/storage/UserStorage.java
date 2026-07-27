package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    public User createUser(User newUser);

    public User updateUser(User updateUser);

    public User removeUser(Long removeUserId);

    public Collection<User> getAll();

    public User getUser(Long id);
}
