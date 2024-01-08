package org.effective_mobile.task_management_system.repository;

import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;
import org.effective_mobile.task_management_system.entity.AbstractEntity;
import org.effective_mobile.task_management_system.entity.Comment;
import org.effective_mobile.task_management_system.entity.Role;
import org.effective_mobile.task_management_system.entity.Task;
import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.enums.UserRole;
import org.effective_mobile.task_management_system.utils.EntityAssertionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static org.effective_mobile.task_management_system.utils.EntityAssertionUtils.assertFieldsAreEqual;
import static org.effective_mobile.task_management_system.utils.EntityCreator.createComments;
import static org.effective_mobile.task_management_system.utils.EntityCreator.createTask;
import static org.effective_mobile.task_management_system.utils.EntityCreator.createUser;
import static org.effective_mobile.task_management_system.utils.EntityManagerUtils.persistFlushRefresh;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class RepositoriesTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @Transactional
    public void testUsersWhenTheyHaveTasks() {
        Role executorRole = new Role(UserRole.EXECUTOR);
        Role creatorRole = new Role(UserRole.CREATOR);

        User creator1 = createUser(creatorRole, executorRole);
        User creator2 = createUser(creatorRole, executorRole);
        User executor = createUser(executorRole);

        Task task1 = createTask(creator1);
        Task task2 = createTask(creator2, executor);
        Task task3 = createTask(creator1, creator2);

        persistFlushRefresh(testEntityManager, executorRole, creatorRole);
        persistFlushRefresh(testEntityManager, creator1, creator2, executor);

        persistFlushRefresh(testEntityManager, task1, task2, task3);

        List<Comment> comments1 = createComments(task1, 3, List.of(creator1, executor, creator2));
        List<Comment> comments2 = createComments(task2, 1, List.of(creator1));
        List<Comment> comments3 = createComments(task3, 2, List.of(executor, creator2));

        persistFlushRefresh(testEntityManager, comments1);
        persistFlushRefresh(testEntityManager, comments2);
        persistFlushRefresh(testEntityManager, comments3);

        List<User> createdUsers = List.of(creator1, creator2, executor);
        createdUsers.forEach(u -> u.getTasks().forEach(t -> t.getComments().forEach(c -> c.getUser())));
        createdUsers.forEach(u -> u.getRoles().size());

        testEntityManager.clear();
        List<User> foundUsers = getUsers(createdUsers.stream().map(AbstractEntity::getId).toList());

        assertUsersAreEqual(
            findEqualTo(createdUsers, executor),
            findEqualTo(foundUsers, executor)
        );

        List<User> foundWithoutExecutor = foundUsers.stream().filter(userIsNot(executor)).toList();
        List<User> createdWithoutExecutor = createdUsers.stream().filter(userIsNot(executor)).toList();

        assertEquals(foundWithoutExecutor.size(), createdWithoutExecutor.size());
        EntityAssertionUtils
            .pairsById(foundWithoutExecutor, createdWithoutExecutor)
            .peek(EntityAssertionUtils::assertIdsAreEqual)
            .peek(pair -> assertFieldsAreEqual(pair, User::getUsername))
            .peek(pair -> assertFieldsAreEqual(pair, User::getPassword))
            .peek(pair -> assertFieldsAreEqual(pair, User::getEmail))
            .peek(pair -> {
                List<Role> expectedRoles = pair.getLeft().getRoles();
                List<Role> others = pair.getRight().getRoles();
                assertRolesAreEqual(expectedRoles, others);
            })
            .forEach(this::assertTasksAreFullyEqual);
    }

    @NonNull
    private User findEqualTo(List<User> createdUsers, User executor) {
        return createdUsers.stream().filter(userIs(executor)).findFirst().get();
    }

    @NonNull
    private Predicate<User> userIs(User executor) {
        return user -> Objects.equals(user.getId(), executor.getId());
    }

    @NonNull
    private Predicate<User> userIsNot(User executor) {
        return user -> !Objects.equals(user.getId(), executor.getId());
    }

    private List<User> getUsers(List<Long> ids) {
        return ids.stream().map(id -> testEntityManager.find(User.class, id)).toList();
    }

    private void assertRolesAreEqual(List<Role> roles, List<Role> others) {
        assertEquals(roles.size(), others.size());
        EntityAssertionUtils
            .pairsById(roles, others)
            .peek(EntityAssertionUtils::assertIdsAreEqual)
            .forEach(RepositoriesTest::assertNamesAreEqual);
    }

    private void assertTasksAreFullyEqual(Pair<User, User> usersPair) {
        User expectedCreator = usersPair.getLeft();
        List<Task> tasks = expectedCreator.getTasks();
        List<Task> others = usersPair.getRight().getTasks();

        assertFalse(tasks.isEmpty());
        assertEquals(tasks.size(), others.size());
        EntityAssertionUtils
            .pairsById(tasks, others)
            .peek(EntityAssertionUtils::assertIdsAreEqual)
            .peek(p -> assertFieldsAreEqual(p, Task::getStatus))
            .peek(p -> assertFieldsAreEqual(p, Task::getPriority))
            .peek(p -> assertFieldsAreEqual(p, Task::getContent))
            .peek(p -> {
                User actualCreator = p.getLeft().getCreator();
                assertUsersAreEqual(expectedCreator, actualCreator);
                assertUsersAreEqual(actualCreator, p.getRight().getCreator());
            })
            .peek(p -> {
                assertUsersAreEqual(p.getLeft().getExecutor(), p.getRight().getExecutor());
            })
            .forEach(this::assertCommentsAreEqual);
    }

    private void assertCommentsAreEqual(Pair<Task, Task> tasksPair) {
        Task expectedTask = tasksPair.getLeft();
        List<Comment> comments = expectedTask.getComments();
        List<Comment> others = tasksPair.getRight().getComments();

        EntityAssertionUtils
            .pairsById(comments, others)
            .peek(EntityAssertionUtils::assertIdsAreEqual)
            .peek(p -> assertFieldsAreEqual(p, Comment::getContent))
            .peek(p -> assertFieldsAreEqual(p, Comment::getCreatedAt))
            .peek(p -> assertFieldsAreEqual(p, Comment::getUpdatedAt))
            .peek(p -> {
                Task actualTask = p.getLeft().getTask();
                assertTasksAreEqual(actualTask, p.getRight().getTask());
                assertEquals(expectedTask.getId(), actualTask.getId());
            })
            .forEach(p -> {
                User user = p.getLeft().getUser();
                User other = p.getRight().getUser();
                System.out.println(user);
                System.out.println(other);
                assertUsersAreEqual(user, other);
            });
    }

    private void assertTasksAreEqual(Task task, Task other) {
        assertFieldsAreEqual(task, other, Task::getId);
        assertFieldsAreEqual(task, other, Task::getContent);
        assertFieldsAreEqual(task, other, Task::getStatus);
        assertFieldsAreEqual(task, other, Task::getPriority);
        assertFieldsAreEqual(task, other, Task::getCreatedAt);
        assertFieldsAreEqual(task, other, Task::getUpdatedAt);
    }

    private void assertUsersAreEqual(User user, User other) {
        if (user == null && other == null) return;
        assertNotNull(user);
        assertFieldsAreEqual(user, other, User::getId);
        assertFieldsAreEqual(user, other, User::getUsername);
        assertFieldsAreEqual(user, other, User::getEmail);
        assertFieldsAreEqual(user, other, User::getPassword);
        assertFieldsAreEqual(user, other, User::getCreatedAt);
        assertFieldsAreEqual(user, other, User::getUpdatedAt);
        assertRolesAreEqual(user.getRoles(), other.getRoles());
    }

    public static void assertNamesAreEqual(Pair<Role, Role> pair) {
        assertFieldsAreEqual(pair, Role::getName);
        assertFieldsAreEqual(pair, Role::getCreatedAt);
        assertFieldsAreEqual(pair, Role::getUpdatedAt);
    }
}
