package org.effective_mobile.task_management_system.repository;

import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;
import org.effective_mobile.task_management_system.entity.Comment;
import org.effective_mobile.task_management_system.entity.DatedEntity;
import org.effective_mobile.task_management_system.entity.HasLongId;
import org.effective_mobile.task_management_system.entity.Role;
import org.effective_mobile.task_management_system.entity.Task;
import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.enums.UserRole;
import org.effective_mobile.task_management_system.utils.EntityAssertionUtils;
import org.effective_mobile.task_management_system.utils.EntityCreator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.effective_mobile.task_management_system.utils.EntityAssertionUtils.assertDatesAreEqual;
import static org.effective_mobile.task_management_system.utils.EntityAssertionUtils.assertFieldsAreEqual;
import static org.effective_mobile.task_management_system.utils.EntityAssertionUtils.pairsStreamById;
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
    Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSSS");

    Role executorRole       ; Role creatorRole        ;
    User executor           ; User creator1           ; User creator2;
    Task task1              ; Task task2              ; Task task3;
    List<Comment> comments1 ; List<Comment> comments2 ; List<Comment> comments3;

    List<User> createdUsers;

    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    public void createEntities() {
        executorRole = new Role(UserRole.EXECUTOR);
        creatorRole = new Role(UserRole.CREATOR);

        creator1 = createUser(creatorRole, executorRole);
        creator2 = createUser(creatorRole, executorRole);
        executor = createUser(executorRole);

        task1 = createTask(creator1);
        task2 = createTask(creator2, executor);
        task3 = createTask(creator1, creator2);

        persistFlushRefresh(testEntityManager, executorRole, creatorRole);
        persistFlushRefresh(testEntityManager, creator1, creator2, executor);
        persistFlushRefresh(testEntityManager, task1, task2, task3);

        comments1 = createComments(task1, 3, List.of(creator1, executor, creator2));
        comments2 = createComments(task2, 1, List.of(creator1));
        comments3 = createComments(task3, 2, List.of(executor, creator2));

        persistFlushRefresh(testEntityManager, comments1);
        persistFlushRefresh(testEntityManager, comments2);
        persistFlushRefresh(testEntityManager, comments3);

        createdUsers = List.of(creator1, creator2, executor);

        fetchAllLazyDependencies(createdUsers);
        testEntityManager.clear();
    }

    @Test
    public void datedEntityTest() {
        createdUsers = getUsers(createdUsers.stream().map(HasLongId::getId).toList());
        for (User createdUser : createdUsers) {
            createdUser.setUsername(EntityCreator.username());
            testEntityManager.persistAndFlush(createdUser);
            for (Task task : createdUser.getTasks()) {
                task.setContent(EntityCreator.content());
                testEntityManager.persistAndFlush(task);
                for (Comment comment : task.getComments()) {
                    comment.setContent(EntityCreator.content());
                    testEntityManager.persistAndFlush(comment);
                }
            }
        }

        fetchAllLazyDependencies(createdUsers);
        testEntityManager.clear();

        List<User> updatedUsers = getUsers(createdUsers.stream().map(HasLongId::getId).toList());

        pairsStreamById(updatedUsers, createdUsers).forEach(usersPair -> {
                User updatedUser = usersPair.getLeft();
                User createdUser = usersPair.getRight();
                assertUpdate(usersPair, User::getUsername);
                pairsStreamById(updatedUser.getTasks(), createdUser.getTasks()).forEach(tasksPair -> {
                    Task updatedTask = tasksPair.getLeft();
                    Task createdTask = tasksPair.getRight();
                    assertUpdate(tasksPair, Task::getContent);
                    pairsStreamById(updatedTask.getComments(), createdTask.getComments()).forEach(commentsPair -> {
                        assertUpdate(commentsPair, Comment::getContent);
                    });
                });
            });
    }

    private <E extends DatedEntity> void assertUpdate(
        Pair<E, E> pair,
        Function<E, String> updatedFieldGetter
    ) {
        assertFieldsAreEqual(pair.getLeft(), pair.getRight(), updatedFieldGetter);
        assertDatedFields(pair.getLeft(), pair.getRight());
    }

    private <E extends DatedEntity> void assertDatedFields(E updated, E created) {
        assertDatesAreEqual(updated, created, formatter, DatedEntity::getCreatedAt);
        assertDatesAreEqual(updated, created, formatter, DatedEntity::getUpdatedAt);
        assertDatesAreEqual(updated, created, formatter, DatedEntity::getDeletedAt);
    }

    @Test
    public void testUsersWhenTheyHaveTasks() {
        List<User> foundUsers = getUsers(createdUsers.stream().map(HasLongId::getId).toList());

        assertUsersAreEqual(
            findEqualTo(executor, createdUsers),
            findEqualTo(executor, foundUsers)
        );

        List<User> foundWithoutExecutor = foundUsers.stream().filter(userIsNot(executor)).toList();
        List<User> createdWithoutExecutor = createdUsers.stream().filter(userIsNot(executor)).toList();

        assertEquals(foundWithoutExecutor.size(), createdWithoutExecutor.size());
        pairsStreamById(foundWithoutExecutor, createdWithoutExecutor)
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
    private User findEqualTo(User executor, List<User> createdUsers) {
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
        pairsStreamById(roles, others)
            .peek(EntityAssertionUtils::assertIdsAreEqual)
            .forEach(RepositoriesTest::assertNamesAreEqual);
    }

    private void assertTasksAreFullyEqual(Pair<User, User> usersPair) {
        User expectedCreator = usersPair.getLeft();
        List<Task> tasks = expectedCreator.getTasks();
        List<Task> others = usersPair.getRight().getTasks();

        assertFalse(tasks.isEmpty());
        assertEquals(tasks.size(), others.size());
        pairsStreamById(tasks, others)
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
        assertFalse(comments.isEmpty());
        assertEquals(comments.size(), others.size());
        pairsStreamById(comments, others)
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
    }

    private void fetchAllLazyDependencies(List<User> users) {
        users.stream()
            .map(User::getTasks)
            .flatMap(Collection::stream)
            .map(Task::getComments)
            .peek(Collection::size)
            .flatMap(Collection::stream)
            .peek(Comment::getUser)
            .forEach(Comment::getContent);

        users.stream()
            .map(User::getTasks)
            .peek(Collection::size)
            .flatMap(Collection::stream)
            .forEach(Task::getExecutor);

        users.stream()
            .map(User::getRoles)
            .forEach(Collection::size);
    }
}
