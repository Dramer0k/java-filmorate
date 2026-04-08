package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.mapper.UserMapper;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.request.UserRequest;
import ru.yandex.practicum.filmorate.model.response.UserResponse;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    public final UserService userService;
    public final UserMapper mapper;


    @GetMapping
    public Collection<User> getUsers() {
        return userService.getUsersMap();
    }

    @PostMapping
    public UserResponse create(@RequestBody UserRequest request) throws ValidationException {
        User user = mapper.toUser(request);
        User result = userService.createUser(user);
        return mapper.toResponse(result);
    }

    @PutMapping
    public UserResponse update(@RequestBody UserRequest request) throws ValidationException {
        User user = mapper.toUser(request);
        User result = userService.updateUserInfo(user);
        return mapper.toResponse(result);
    }

    @DeleteMapping
    public User delete(@RequestParam Long userId) {
        return userService.removeUser(userId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable String id, @PathVariable final  String friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable String id, @PathVariable String friendId) {
        userService.removeFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getAllFriends(@PathVariable String id) {
        return userService.getAllFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(@PathVariable String id, @PathVariable String otherId) {
        return userService.getMutualFriends(id, otherId);
    }
}