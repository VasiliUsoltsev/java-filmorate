package ru.yandex.practicum.filmorate.dal;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Friend;

import java.util.List;
import java.util.Set;

@Repository
public class FriendsUserRepository extends BaseRepository<Friend> {
    private static final String FIND_ALL_FRIENDS_USER_BY_USER_ID_QUERY = "SELECT friend_user_id, status_id FROM friends_user " +
            "WHERE user_id = ?";
    private static final String INSERT_FRIENDS_USER_BY_USER_ID_QUERY = "INSERT INTO friends_user(user_id, " +
            "friend_user_id,status_id) VALUES(?, ?, ?)";
    private static final String DELETE_FRIENDS_USER_BY_USER_ID_QUERY = "DELETE FROM friends_user WHERE user_id = ?";


    public FriendsUserRepository(JdbcTemplate jdbc, RowMapper<Friend> mapper) {
        super(jdbc, mapper);
    }

    public List<Friend> findAllFriendsUserByUserId(Long userId) {
        return findMany(
                FIND_ALL_FRIENDS_USER_BY_USER_ID_QUERY,
                userId
        );
    }

    public void addFriendsUser(Long userId, Set<Friend> friendsUser) {
        for (Friend curFriendsUser : friendsUser.stream().toList()) {
            insert(
                    INSERT_FRIENDS_USER_BY_USER_ID_QUERY,
                    userId,
                    curFriendsUser.getFriendUser(),
                    curFriendsUser.getStatus()
            );
        }
    }

    public void delFriendsUser(Long userId) {
        delete(
                DELETE_FRIENDS_USER_BY_USER_ID_QUERY,
                userId
        );
    }
}
