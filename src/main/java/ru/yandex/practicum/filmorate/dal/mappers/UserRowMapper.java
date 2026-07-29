package ru.yandex.practicum.filmorate.dal.mappers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.FriendsUserRepository;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserRowMapper implements RowMapper<User> {
    private final FriendsUserRepository friendsUserRepository;

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setBirthday(rs.getTimestamp("birthday").toLocalDateTime().toLocalDate());
        user.setFriends(getFriendsUser(user.getId()));

        return user;
    }

    private Set<Friend> getFriendsUser(Long userId) {
        Set<Friend> list = friendsUserRepository.findAllFriendsUserByUserId(userId)
                .stream()
                .collect(Collectors.toSet());
        return list;
    }
}
