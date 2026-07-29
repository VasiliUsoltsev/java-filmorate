package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    public UserController(@Qualifier("userDbStorage") UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        return userStorage.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable Long id) {
        return userStorage.getUser(id);
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDto> getFriendAll(@PathVariable Long id) {
        return userService.getFriendsAll(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> getCommonFriends(@PathVariable Long id,
                                                @PathVariable Long otherId
    ) {
        return userService.getCommonFriends(id, otherId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserRequest newUser) {
        return userStorage.createUser(newUser);
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody UpdateUserRequest updateUser) {
        return userStorage.updateUser(updateUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateFriend(@PathVariable Long id,
                             @PathVariable Long friendId
    ) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{removeUserId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable() Long removeUserId) {
        userStorage.removeUser(removeUserId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriend(@PathVariable Long id,
                             @PathVariable Long friendId
    ) {
        userService.deleteFriend(id, friendId);
    }
}