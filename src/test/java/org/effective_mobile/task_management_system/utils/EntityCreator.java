package org.effective_mobile.task_management_system.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.datafaker.Faker;
import net.datafaker.providers.base.Internet;
import net.datafaker.providers.base.Text;
import org.effective_mobile.task_management_system.entity.Comment;
import org.effective_mobile.task_management_system.entity.Role;
import org.effective_mobile.task_management_system.entity.Task;
import org.effective_mobile.task_management_system.entity.User;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EntityCreator {
    public static final Faker faker = new Faker();
    public static final Internet internet = faker.internet();
    public static final  Text text = faker.text();

    public static Task createTask(User creator1, User executor) {
        Task task = Task.builder()
            .content(text.text())
            .creator(creator1)
            .executor(executor)
            .build();

        return task;
    }

    public static Task createTask(User creator1) {
        return createTask(creator1, null);
    }

    public static List<Comment> createComments(
        Task task,
        Integer commentsQuantity,
        List<User> commentators
    ) {
        List<Comment> collect = IntStream.range(0, commentsQuantity)
            .boxed()
            .map(i -> createComment(task, text.text(), getRandomFrom(commentators)))
            .collect(Collectors.toList());

        return collect;
    }

    private static User getRandomFrom(List<User> commentators) {
        int commentatorNumber = new Random().nextInt(commentators.size());
        return commentators.get(commentatorNumber);
    }

    public static Comment createComment(
        Task task,
        String content,
        User user
    ) {
        return Comment.builder()
            .content(content)
            .user(user)
            .task(task)
            .createdAt(new Date(System.currentTimeMillis()))
            .build();
    }

    public static User createUser(Role... roles) {
        List<Role> rolesList = Arrays.stream(roles).collect(Collectors.toList());
        String username = internet.username();
        return User.builder()
            .username(username)
            .email(username + "@gmail.com")
            .password(internet.password())
            .roles(rolesList).build();
    }
}
