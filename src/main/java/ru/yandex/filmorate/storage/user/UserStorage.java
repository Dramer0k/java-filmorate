package ru.yandex.filmorate.storage.user;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    Collection<User> getUsersMap();

    User createUser(User user) throws ValidationException;

    User updateUserInfo(User user) throws ValidationException;

    User removeUser(Long userId);

    void addFriend(String id, String friendId);

    void removeFriend(String id, String friendId);

    List<User> getAllFriends(String id);

    User getById(Long id);

    List<User> getMutualFriends(String id, String friendId);
}
