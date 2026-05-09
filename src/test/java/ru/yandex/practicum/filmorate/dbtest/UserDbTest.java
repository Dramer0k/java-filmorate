package ru.yandex.practicum.filmorate.dbtest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserDbTest {
    private final UserDbStorage userStorage;
    private final JdbcTemplate jdbcTemplate;
    User testUser = new User();

    @Test
    void testDatabaseConnection() {
        String sql = "SELECT COUNT(*) FROM users";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        assertNotNull(count);
        assertTrue(count >= 0);
    }

    @Test
    public void testFindUserById() {

        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(1L));

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    public void testCreateUser() throws InternalServerException {
        testUser.setName("Lola");
        testUser.setLogin("Lily");
        testUser.setEmail("Lily@ya.ru");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));

        User createdUser = userStorage.createUser(testUser);

        assertThat(createdUser)
                .isNotNull()
                .hasFieldOrPropertyWithValue("name", "Lola")
                .hasFieldOrPropertyWithValue("login", "Lily")
                .hasFieldOrPropertyWithValue("email", "Lily@ya.ru")
                .extracting("birthday") // получаем значение поля birthday
                .isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    public void testGetUsersMap() {

        Collection<User> userMap = userStorage.getUsersMap();

        assertThat(userMap).isNotNull();
    }

    @Test
    public void testGetUserById() {

        User user = userStorage.getUserById(1L);

        assertThat(user).hasFieldOrPropertyWithValue("name", "Hota");
    }

    @Test
    public void testRemoveUser() {

        userStorage.removeUser(1L);
        Collection<User> userMap = userStorage.getUsersMap();
        assertThat(userMap).noneMatch(user -> user.getName().equals("Hota"));
    }

    @Test
    public void testUpdateUserInfo() throws InternalServerException {
        testUser.setId(1L);
        testUser.setName("Lola");
        testUser.setLogin("Lily");
        testUser.setEmail("Lily@ya.ru");
        testUser.setBirthday(LocalDate.of(1990, 1, 1));

        userStorage.updateUserInfo(testUser);
        User updateUser = userStorage.getUserById(1L);

        assertThat(updateUser)
                .hasFieldOrPropertyWithValue("name", "Lola")
                .hasFieldOrPropertyWithValue("login", "Lily")
                .hasFieldOrPropertyWithValue("email", "Lily@ya.ru")
                .extracting("birthday") // получаем значение поля birthday
                .isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    public void testAddFriend() throws InternalServerException {

        userStorage.addFriend(1L, 2L);
        List<User> friendList = userStorage.getAllFriends(1L);

        assertThat(friendList).isNotNull()
                .anyMatch(user -> user.getId().equals(2L));
    }

    @Test
    public void testRemoveFriend() throws InternalServerException {

        userStorage.addFriend(1L, 2L);
        userStorage.removeFriend(1L, 2L);
        List<User> friendList = userStorage.getAllFriends(1L);

        assertThat(friendList).isNotNull()
                .noneMatch(user -> user.getId().equals(2L));
    }

    @Test
    public void testGetAllFriends() throws InternalServerException {

        userStorage.addFriend(1L, 2L);
        userStorage.addFriend(1L, 3L);
        List<User> friendList = userStorage.getAllFriends(1L);

        assertThat(friendList).isNotNull()
                .anyMatch(user -> user.getId().equals(3L))
                .anyMatch(user -> user.getId().equals(2L));
    }

    @Test
    public void testGetMutualFriends() throws InternalServerException {

        userStorage.addFriend(1L, 3L);
        userStorage.addFriend(2L, 3L);
        List<User> friendList = userStorage.getMutualFriends(1L, 2L);

        assertThat(friendList).isNotNull()
                .anyMatch(user -> user.getId().equals(3L));
    }


}
