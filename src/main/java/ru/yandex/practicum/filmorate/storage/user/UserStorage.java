package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Qualifier;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    Collection<User> getUsersMap();

    User createUser(User user) throws ValidationException, InternalServerException;

    User updateUserInfo(User user) throws ValidationException, InternalServerException;

    User removeUser(Long userId);

    void addFriend(Long id, Long friendId) throws InternalServerException;

    void removeFriend(Long id, Long friendId);

    void setLike(Long id, Long userId) throws InternalServerException;

    void removeLike(Long id, Long userId);

    List<User> getAllFriends(Long id);

    User getUserById(Long id);

    List<User> getMutualFriends(Long id, Long friendId);
}
