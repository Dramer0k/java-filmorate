package ru.yandex.practicum.filmorate.storage.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
@Repository
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public Collection<User> getUsersMap() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
        user.setId(id);
        id++;
        users.put(user.getId(), user);
        log.info("Новый пользователь {} добавлен", user);
        return user;
    }

    @Override
    public User updateUserInfo(User user) {
        users.put(user.getId(), user);
        log.info("Данные пользователя изменены!");
        return user;
    }

    @Override
    public User removeUser(Long userId) {
        User oldUser = users.get(userId);
        users.remove(userId);

        log.info("Пользователь {} с id {} удален", oldUser.getName(), userId);

        return oldUser;
    }

    @Override
    public void addFriend(Long id, Long friendId) {
        User user = users.get(id);
        User friend = users.get(friendId);
        if  (user == null || friend == null) {
            throw new NotFoundException("Пользователь не найден!");
        }
        if (user.getFriends().containsKey(friend.getId())) {
            if (user.getFriends().get(friend.getId()) == true) {
                throw new ConditionsNotMetException("Такой друг уже есть!");
            }
            user.getFriends().put(friend.getId(), true);
        }
        user.getFriends().put(friend.getId(), true);
        friend.getFriends().put(user.getId(), false);
        log.info("Друг с id {} добавлен", friend.getId());
    }

    @Override
    public void removeFriend(Long id, Long friendId) {
        User user = users.get(id);
        User friend = users.get(friendId);
        if  (user == null || friend == null) {
            throw new NotFoundException("Пользователь не найден!");
        }
        user.getFriends().put(friend.getId(), false);
        log.info("Друг с id {} удален", friend.getId());
    }

    @Override
    public void setLike(Long id, Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден!");
        }
        if (user.getFavoriteFilms().contains(id)) {
            throw new ConditionsNotMetException("Лайк на этот фильм уже поставлен!");
        }
        user.getFavoriteFilms().add(id);
    }

    @Override
    public void removeLike(Long id, Long userId) {
        User user = users.get(userId);
        if (user == null) {
            throw new NotFoundException("Пользователь не найден!");
        }
        if (!user.getFavoriteFilms().contains(id)) {
            throw new ConditionsNotMetException("Лайка на этом фильме нет!");
        }
        user.getFavoriteFilms().remove(id);
    }

    @Override
    public List<User> getAllFriends(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователя с id " + id + " не существует!");
        }
        List<User> friendList = user.getFriends().keySet()
                .stream()
                .map(users::get)
                .toList();
        log.info("Список друзей: {}", friendList);
        return friendList;
    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователя с id " + id + " нет");
        }
        log.info("Пользователь: {}", users.get(id));
        return users.get(id);
    }

    @Override
    public List<User> getMutualFriends(Long id, Long friendId) {
        User user = users.get(id);
        User friend = users.get(friendId);
        return user.getFriends().keySet().stream()
                .filter(friend.getFriends().keySet()::contains)
                .map(users::get)
                .toList();
    }
}