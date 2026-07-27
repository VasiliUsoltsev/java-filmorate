package ru.yandex.practicum.filmorate;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.dto.*;
import ru.yandex.practicum.filmorate.exception.ExceptionObjectNotFound;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@AutoConfigureTestDatabase
@Sql(scripts = {"/schema.sql", "/data.sql"})
class DbStorageTest {

    @Autowired
    private UserDbStorage userStorage;
    @Autowired
    private FilmDbStorage filmStorage;

    private NewFilmRequest testRequestFilm;

    private NewUserRequest testRequest;

    @BeforeEach
    void setUp() {
        testRequest = new NewUserRequest();
        testRequest.setLogin("testuser");
        testRequest.setEmail("test@mail.ru");
        testRequest.setName("Test User");
        testRequest.setBirthday(LocalDate.of(1990, 1, 1));

        testRequestFilm = new NewFilmRequest();
        testRequestFilm.setName("Test Film");
        testRequestFilm.setDescription("Test Description");
        testRequestFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        testRequestFilm.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1L);
        testRequestFilm.setMpa(mpa);

        Genre genre = new Genre();
        genre.setId(1L);
        testRequestFilm.setGenres(List.of(genre));
    }

    @Test
    void createUserAndReturnUser() {
        UserDto result = userStorage.createUser(testRequest);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("testuser", result.getLogin());
        assertEquals("test@mail.ru", result.getEmail());
        assertEquals("Test User", result.getName());
        assertEquals(LocalDate.of(1990, 1, 1), result.getBirthday());
    }

    @Test
    void createUserWithEmptyNameUseLogin() {
        testRequest.setName("");
        UserDto result = userStorage.createUser(testRequest);
        assertEquals("testuser", result.getName());
    }

    @Test
    void createUserWithNullNameShouldUseLogin() {
        testRequest.setName(null);
        UserDto result = userStorage.createUser(testRequest);
        assertEquals("testuser", result.getName());
    }

    @Test
    void createUserWithLoginContainingSpacesShouldThrow() {
        testRequest.setLogin("test user");
        Exception exception = assertThrows(Exception.class, () -> userStorage.createUser(testRequest));
        assertTrue(exception.getMessage().contains("Логин пользователя содержит пробелы"));
    }

    @Test
    void getUserShouldReturnUser() {
        UserDto created = userStorage.createUser(testRequest);
        UserDto result = userStorage.getUser(created.getId());

        assertNotNull(result);
        assertEquals(created.getId(), result.getId());
        assertEquals(created.getLogin(), result.getLogin());
        assertEquals(created.getEmail(), result.getEmail());
    }

    @Test
    void getUserWithInvalidIdShouldThrow() {
        Exception exception = assertThrows(ExceptionObjectNotFound.class, () -> userStorage.getUser(999L));
        assertTrue(exception.getMessage().contains("Пользователь не найден"));
    }

    @Test
    void getUserModelShouldReturnUserEntity() {
        UserDto created = userStorage.createUser(testRequest);
        User result = userStorage.getUserModel(created.getId());

        assertNotNull(result);
        assertEquals(created.getId(), result.getId());
        assertEquals(created.getLogin(), result.getLogin());
        assertEquals(created.getEmail(), result.getEmail());
        assertEquals(created.getName(), result.getName());
    }

    @Test
    void updateUserhouldUpdateAndReturnUser() {
        UserDto created = userStorage.createUser(testRequest);

        UpdateUserRequest update = new UpdateUserRequest();
        update.setId(created.getId());
        update.setLogin("updateduser");
        update.setEmail("updated@mail.ru");
        update.setName("Updated User");
        update.setBirthday(LocalDate.of(2000, 1, 1));

        UserDto result = userStorage.updateUser(update);

        assertEquals("updateduser", result.getLogin());
        assertEquals("updated@mail.ru", result.getEmail());
        assertEquals("Updated User", result.getName());
        assertEquals(LocalDate.of(2000, 1, 1), result.getBirthday());
    }

    @Test
    void updateUserWithInvalidIdShouldThrow() {
        UpdateUserRequest update = new UpdateUserRequest();
        update.setId(999L);
        update.setLogin("updateduser");
        update.setEmail("updated@mail.ru");

        Exception exception = assertThrows(ExceptionObjectNotFound.class, () -> userStorage.updateUser(update));
        assertTrue(exception.getMessage().contains("Пользователь не найден"));
    }

    @Test
    void getAllShouldReturnAllUsers() {
        userStorage.createUser(testRequest);
        userStorage.createUser(testRequest);

        Collection<UserDto> result = userStorage.getAll();

        assertNotNull(result);
        assertTrue(result.size() >= 2);
    }

    @Test
    void addFriendShouldAddFriend() {
        UserDto user1 = userStorage.createUser(testRequest);

        NewUserRequest friendReq = new NewUserRequest();
        friendReq.setLogin("friend");
        friendReq.setEmail("friend@mail.ru");
        friendReq.setName("Friend");
        friendReq.setBirthday(LocalDate.of(1995, 5, 5));

        UserDto user2 = userStorage.createUser(friendReq);

        userStorage.addFriend(user1.getId(), user2.getId());

        UserDto result = userStorage.getUser(user1.getId());
        assertNotNull(result);
    }

    @Test
    void delFriendShouldRemoveFriend() {
        UserDto user1 = userStorage.createUser(testRequest);

        NewUserRequest friendReq = new NewUserRequest();
        friendReq.setLogin("friend");
        friendReq.setEmail("friend@mail.ru");
        friendReq.setName("Friend");
        friendReq.setBirthday(LocalDate.of(1995, 5, 5));

        UserDto user2 = userStorage.createUser(friendReq);

        userStorage.addFriend(user1.getId(), user2.getId());
        userStorage.delFriend(user1.getId(), user2.getId());

        UserDto result = userStorage.getUser(user1.getId());
        assertNotNull(result);
    }

    @Test
    void removeUserShouldRemoveUser() {
        UserDto user = userStorage.createUser(testRequest);
        userStorage.removeUser(user.getId());

        Exception exception = assertThrows(ExceptionObjectNotFound.class, () -> userStorage.getUser(user.getId()));
        assertTrue(exception.getMessage().contains("Пользователь не найден"));
    }

    @Test
    void removeUserWithInvalidIdShouldThrow() {
        Exception exception = assertThrows(ExceptionObjectNotFound.class, () -> userStorage.removeUser(999L));
        assertTrue(exception.getMessage().contains("Пользователь не найден"));
    }

    // Фильмы
    @Test
    void createFilmShouldCreateAndReturnFilm() {
        FilmDto result = filmStorage.createFilm(testRequestFilm);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("Test Film", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertEquals(LocalDate.of(2000, 1, 1), result.getReleaseDate());
        assertEquals(120, result.getDuration());
        assertNotNull(result.getMpa());
        assertEquals(1L, result.getMpa().getId());
        assertNotNull(result.getGenres());
        assertEquals(1, result.getGenres().size());
        assertEquals(1L, result.getGenres().get(0).getId());
    }

    @Test
    void createFilmWithInvalidReleaseDateShouldThrow() {
        testRequestFilm.setReleaseDate(LocalDate.of(1890, 1, 1));

        Exception exception = assertThrows(Exception.class, () -> filmStorage.createFilm(testRequestFilm));
        assertTrue(exception.getMessage().contains("Дата релиза не может быть раньше 28 декабря 1895 года"));
    }

    @Test
    void createFilmWithDuplicateGenresShouldRemoveDuplicates() {
        Genre genre1 = new Genre();
        genre1.setId(1L);
        Genre genre2 = new Genre();
        genre2.setId(2L);
        testRequestFilm.setGenres(List.of(genre1, genre2, genre1));

        FilmDto result = filmStorage.createFilm(testRequestFilm);

        assertEquals(2, result.getGenres().size());
        assertEquals(1L, result.getGenres().get(0).getId());
        assertEquals(2L, result.getGenres().get(1).getId());
    }

    @Test
    void getFilmShouldReturnFilm() {
        FilmDto created = filmStorage.createFilm(testRequestFilm);
        FilmDto result = filmStorage.getFilm(created.getId());

        assertNotNull(result);
        assertEquals(created.getId(), result.getId());
        assertEquals(created.getName(), result.getName());
        assertEquals(created.getDescription(), result.getDescription());
        assertEquals(created.getReleaseDate(), result.getReleaseDate());
        assertEquals(created.getDuration(), result.getDuration());
    }

    @Test
    void getFilmWithInvalidIdShouldThrow() {
        Exception exception = assertThrows(ExceptionObjectNotFound.class, () -> filmStorage.getFilm(999L));
        assertTrue(exception.getMessage().contains("Фильм не найден"));
    }

    @Test
    void getFilmModelShouldReturnFilmEntity() {
        FilmDto created = filmStorage.createFilm(testRequestFilm);
        Film result = filmStorage.getFilmModel(created.getId());

        assertNotNull(result);
        assertEquals(created.getId(), result.getId());
        assertEquals(created.getName(), result.getName());
        assertEquals(created.getDescription(), result.getDescription());
        assertEquals(created.getReleaseDate(), result.getReleaseDate());
        assertEquals(created.getDuration(), result.getDuration());
        assertNotNull(result.getGenres());
        assertNotNull(result.getMpa());
        assertNotNull(result.getLikes());
    }

    @Test
    void updateFilmShouldUpdateAndReturnFilm() {
        FilmDto created = filmStorage.createFilm(testRequestFilm);

        UpdateFilmRequest update = new UpdateFilmRequest();
        update.setId(created.getId());
        update.setName("Updated Film");
        update.setDescription("Updated Description");
        update.setReleaseDate(LocalDate.of(2010, 5, 5));
        update.setDuration(150);

        Mpa mpa = new Mpa();
        mpa.setId(2L);
        update.setMpa(mpa);

        FilmDto result = filmStorage.updateFilm(update);

        assertEquals("Updated Film", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals(LocalDate.of(2010, 5, 5), result.getReleaseDate());
        assertEquals(150, result.getDuration());
        assertEquals(2L, result.getMpa().getId());
    }

    @Test
    void updateFilmWithInvalidIdShouldThrow() {
        UpdateFilmRequest update = new UpdateFilmRequest();
        update.setId(999L);
        update.setName("Updated Film");
        update.setDescription("Updated Description");
        update.setReleaseDate(LocalDate.of(2010, 5, 5));
        update.setDuration(150);

        Exception exception = assertThrows(ExceptionObjectNotFound.class, () -> filmStorage.updateFilm(update));
        assertTrue(exception.getMessage().contains("Фильм не найден"));
    }

    @Test
    void getAllShouldReturnAllFilms() {
        filmStorage.createFilm(testRequestFilm);
        filmStorage.createFilm(testRequestFilm);

        Collection<FilmDto> result = filmStorage.getAll();

        assertNotNull(result);
        assertTrue(result.size() >= 2);
    }

    @Test
    void getAllModelShouldReturnAllFilmEntities() {
        filmStorage.createFilm(testRequestFilm);
        filmStorage.createFilm(testRequestFilm);

        Collection<Film> result = filmStorage.getAllModel();

        assertNotNull(result);
        assertTrue(result.size() >= 2);
        result.forEach(film -> {
            assertNotNull(film.getGenres());
            assertNotNull(film.getMpa());
            assertNotNull(film.getLikes());
        });
    }

    @Test
    void addLikeShouldAddLike() {
        FilmDto film = filmStorage.createFilm(testRequestFilm);
        Long userId = 1L;
        filmStorage.addLike(film.getId(), userId);

        FilmDto result = filmStorage.getFilm(film.getId());

        assertTrue(result.getLikes().contains(userId));
    }

    @Test
    void deleteLikeShouldRemoveLike() {
        FilmDto film = filmStorage.createFilm(testRequestFilm);
        Long userId = 1L;

        filmStorage.addLike(film.getId(), userId);
        filmStorage.delLike(film.getId(), userId);

        FilmDto result = filmStorage.getFilm(film.getId());
        assertFalse(result.getLikes().contains(userId));
    }
}