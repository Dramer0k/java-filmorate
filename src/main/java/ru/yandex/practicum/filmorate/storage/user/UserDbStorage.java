package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.BaseRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;


@Slf4j
@Primary
@Repository
public class UserDbStorage extends BaseRepository<User> implements UserStorage{
    private static final String FIND_ALL_QUERY = "SELECT * FROM users";
    private static final String FIND_USER_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO users(login, name, email, birthday)" +
            "VALUES (?, ?, ?, ?)";
    private static final String INSERT_FRIEND_QUERY = "INSERT INTO friends(user_id, friend_id, friendship)" +
            "VALUES (?, ?, ?)";
    private static final String INSERT_LIKE_QUERY = "MERGE INTO favorites_film AS f\n" +
            "USING (VALUES (?, ?)) AS v (user_id, film_id)\n" +
            "ON f.user_id = v.user_id AND f.film_id = v.film_id\n" +
            "WHEN NOT MATCHED THEN\n" +
            "    INSERT (user_id, film_id) VALUES (v.user_id, v.film_id)";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE users SET name = ?, login = ?, email = ?, birthday = ?";
    private static final String UPDATE_FRIENDSHIP_QUERY = "UPDATE friends SET friendship = ? WHERE user_id = ? and friend_id = ?";
    private static final String DELETE_FRIEND_QUERY = "DELETE FROM friends WHERE user_id = ? and friend_id = ?";
    private static final String FIND_FRIENDSHIP_QUERY = "SELECT COUNT(*) FROM friends WHERE user_id = ? and friend_id = ?";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM favorites_film WHERE film_id = ? and user_id = ?";
    private static final String FIND_ALL_FRIENDS_QUERY = "SELECT u.*\n" +
            "FROM users AS u\n" +
            "JOIN (SELECT friend_id FROM friends WHERE user_id = ?) AS f ON u.id = f.friend_id\n";
    private static final String FIND_MUTUAL_FRIENDS_QUERY = "SELECT * FROM users as u\n" +
            "JOIN (SELECT friend_id FROM friends WHERE user_id = ?) as f1 ON u.id = f1.friend_id\n" +
            "JOIN (SELECT friend_id FROM friends WHERE user_id = ?) as f2 ON u.id = f2.friend_id\n";

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<User> getUsersMap() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public User createUser(User user) throws InternalServerException {
        long id = insert(
                INSERT_QUERY,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday()
        );
        user.setId(id);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        User user;
        if(findOne(FIND_USER_BY_ID_QUERY, id).isPresent()) {
            user = findOne(FIND_USER_BY_ID_QUERY, id).get();
            return user;
        } else {
            throw new NotFoundException("Пользователь не найден!");
        }
    }

    @Override
    public User removeUser(Long userId) {
        User user = getUserById(userId);
        boolean result = delete(DELETE_BY_ID_QUERY, userId);
        if (result) {
            return user;
        } else {
            throw new ConditionsNotMetException("Не удалось удалить пользователя");
        }
    }

    @Override
    public User updateUserInfo(User user) throws InternalServerException {
        update(
                UPDATE_QUERY,
                user.getName(),
                user.getLogin(),
                user.getEmail(),
                user.getBirthday()
        );
        return user;
    }

    @Override
    public void addFriend(Long id, Long friendId) throws InternalServerException {
        User user = getUserById(id);
        User friend = getUserById(friendId);

        if (checkAvailability(FIND_FRIENDSHIP_QUERY, id, friendId)) {
            throw new ConditionsNotMetException("Дружба уже есть!");
        }

        if(friend.getFriends().containsKey(id)) {
            update(
                    UPDATE_FRIENDSHIP_QUERY,
                    true,
                    friendId,
                    id
            );
            insertWithoutId(
                    INSERT_FRIEND_QUERY,
                    id,
                    friendId,
                    true
            );
            return;
        }
        insertWithoutId(
                INSERT_FRIEND_QUERY,
                id,
                friendId,
                false
        );
    }

    @Override
    public void removeFriend(Long id, Long friendId) {
        getUserById(id);
        getUserById(friendId);

        boolean result = delete(DELETE_FRIEND_QUERY, id, friendId);
        if (result) {
            return;
        }
        log.info("Пользователь {} не имеет дружбы с пользователем {}", id, friendId);

    }

    @Override
    public void setLike(Long id, Long userId) throws InternalServerException {
        insertWithoutId(INSERT_LIKE_QUERY, userId, id);
    }

    @Override
    public void removeLike(Long id, Long userId) {
        getUserById(userId);
        log.info("Юзер: {}", userId);
        log.info("Фильм: {}", id);
        boolean result = delete(DELETE_LIKE_QUERY, id, userId);
        if (!result) {
            log.info("Лайк с фильма убран!");
        } else {
            throw new ConditionsNotMetException("На этом фильме лайка не было!");
        }
    }

    @Override
    public List<User> getAllFriends(Long id) {
        User user = getUserById(id);
        return findMany(FIND_ALL_FRIENDS_QUERY, id);
    }

    @Override
    public List<User> getMutualFriends(Long id, Long friendId) {
        getUserById(id);
        getUserById(friendId);

        return findMany(FIND_MUTUAL_FRIENDS_QUERY, id, friendId);
    }
}
