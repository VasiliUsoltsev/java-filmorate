package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class UserRepository extends BaseRepository<User> {
    private static final String FIND_USERS_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_USER_BY_ID_QUERY = "SELECT * FROM users WHERE user_id = ?";
    private static final String DELETE_USER_BY_ID_QUERY = "DELETE FROM users WHERE user_id = ?";
    private static final String UPDATE_USER_BY_ID_QUERY = "UPDATE users SET email = ?, login = ?," +
            " name = ?, birthday = ? WHERE user_id = ?";
    private static final String INSERT_USER_QUERY = "INSERT INTO users(email, login, name, birthday) " +
            "VALUES (?, ?, ?, ?)";
    private static final String INSERT_ADD_USER_FRIEND_QUERY = "INSERT INTO friends_user(user_id, friend_user_id," +
            " status_id) VALUES (?, ?, '1')";
    private static final String DELETE_USER_FRIEND_QUERY = "DELETE FROM friends_user " +
            "WHERE user_id = ? AND friend_user_id = ?";

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public List<User> findUsersAll() {
        return findMany(FIND_USERS_ALL_QUERY);
    }

    public Optional<User> findUserById(Long userId) {
        return findOne(FIND_USER_BY_ID_QUERY, userId);
    }

    public User createUser(User user) {
        long id = insert(
                INSERT_USER_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    public User updateUser(User user) {
        update(
                UPDATE_USER_BY_ID_QUERY,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    public boolean delUserById(Long userId) {
        return delete(DELETE_USER_BY_ID_QUERY, userId);
    }

    public boolean addFriend(Long userId, Long friendUserId) {
        long id = insert(
                INSERT_ADD_USER_FRIEND_QUERY,
                userId,
                friendUserId
        );
        return id > 0;
    }

    public boolean delFriend(Long userId, Long friendUserId) {
        return delete(
                DELETE_USER_FRIEND_QUERY,
                userId,
                friendUserId
        );
    }
}
