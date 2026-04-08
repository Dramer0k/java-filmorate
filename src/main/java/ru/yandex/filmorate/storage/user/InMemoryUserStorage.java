package ru.yandex.filmorate.storage.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Data
@Component
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
    public void addFriend(String id, String friendId) {
        User user = users.get(Long.valueOf(id));
        User friend = users.get(Long.valueOf(friendId));
        if  (user == null || friend == null) {
            throw new NotFoundException("Пользователь не найден!");
        }
        if (user.getFriends().contains(friend.getId())) {
            throw new ConditionsNotMetException("Такой друг уже есть!");
        }
        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
        log.info("Друг с id {} добавлен", friend.getId());
    }

    @Override
    public void removeFriend(String id, String friendId) {
        User user = users.get(Long.valueOf(id));
        User friend = users.get(Long.valueOf(friendId));
        if  (user == null || friend == null) {
            throw new NotFoundException("Пользователь не найден!");
        }
        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());

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
    public List<User> getAllFriends(String id) {
        User user = users.get(Long.valueOf(id));
        if (user == null) {
            throw new NotFoundException("Пользователя с id " + id + " не существует!");
        }
        return user.getFriends().stream()
                .map(users::get)
                .toList();
    }

    @Override
    public User getById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователя с id " + id + " нет");
        }
        log.info("Пользователь: {}", users.get(id));
        return users.get(id);
    }

    @Override
    public List<User> getMutualFriends(String id, String friendId) {
        User user = users.get(Long.valueOf(id));
        User friend = users.get(Long.valueOf(friendId));
        return user.getFriends().stream()
                .filter(friend.getFriends()::contains)
                .map(users::get)
                .toList();
    }
}
