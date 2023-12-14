package org.effective_mobile.task_management_system.repository;

import io.micrometer.common.lang.Nullable;
import net.datafaker.Faker;
import org.apache.commons.lang3.RandomStringUtils;
import org.effective_mobile.task_management_system.TaskManagementSystemApp;
import org.effective_mobile.task_management_system.entity.Task;
import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.enums.Priority;
import org.effective_mobile.task_management_system.enums.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Тест для класса {@link FilteredAndPagedTaskRepositoryImpl}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = TaskManagementSystemApp.class)
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class FilteredAndPagedTaskRepositoryImplTest {

    @Autowired private TaskRepository taskRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private FilteredAndPagedTaskRepositoryImpl repository;

    private final static String creatorName = new Faker().internet().username();
    private final static String executorName = new Faker().internet().username();
    private static final User creator = user(creatorName);;
    private static final User executor = user(executorName);

    private ExpectedCount expectedCountDto;

    private static final List<Task> tasks = Stream.of(
        task(creator, null),
        task(creator, null),
        task(creator, executor),
        task(creator, executor),
        task(creator, executor),
        task(creator, executor),
        task(creator, null),
        task(creator, null)
    ).collect(Collectors.toList());

    record ExpectedCount(
        Long creatorFilterCount,
        Long executorFilterCount,
        Long filterByBothCount,
        Long totalCount
    ) {}

    @BeforeEach
    void setUp() {
        userRepository.saveAllAndFlush(List.of(creator, executor));

        List<Task> savedTasks = tasks.stream()
            .map(taskRepository::saveAndFlush)
            .toList();

        expectedCountDto = new ExpectedCount(
            savedTasks.stream().filter(task -> creatorEqualTo(task, creatorName)).count(),
            savedTasks.stream().filter(task -> executorEqualTo(task, executorName)).count(),
            savedTasks.stream().filter(task -> creatorEqualTo(task, creatorName) && executorEqualTo(task, executorName)).count(),
            savedTasks.stream().count()
        );
    }

    /**
     * Test for {@link FilteredAndPagedTaskRepositoryImpl#findByCreatorAndExecutor}.
     */
    @Transactional
    @ParameterizedTest
    @MethodSource("creatorAndExecutorTestData")
    public void findByCreatorAndExecutorTest(
        @Nullable String creator,
        @Nullable String executor,
        Pageable pageable
    ) {
        Page<Task> tasks = repository.findByCreatorAndExecutor(creator, executor, pageable);
        Long expectedCount = null;

        if (creator != null && executor == null) { expectedCount = expectedCountDto.creatorFilterCount; }
        if (creator == null && executor != null) { expectedCount = expectedCountDto.executorFilterCount; }
        if (creator != null && executor != null) { expectedCount = expectedCountDto.filterByBothCount; }
        if (creator == null && executor == null) { expectedCount = expectedCountDto.totalCount; }

        Assertions.assertEquals(expectedCount, tasks.getTotalElements());
    }

    public static Stream<Arguments> creatorAndExecutorTestData() {
        Pageable pageable = Pageable.ofSize(tasks.size());
        return Stream.of(
            Arguments.of(creatorName, null, pageable),
            Arguments.of(creatorName, executorName, pageable),
            Arguments.of(null, executorName, pageable),
            Arguments.of(null, null, pageable)
        );
    }

    private boolean creatorEqualTo(Task task, String creatorName) {
        return creatorName.equals(task.getCreator().getUsername());
    }

    private boolean executorEqualTo(Task task, String executorName) {
        if (task.getExecutor() != null) {
            return executorName.equals(task.getExecutor().getUsername());
        } else {
            return false;
        }
    }

    private static String randomStr() {
        int count = new Random().nextInt(4, 9);
        return RandomStringUtils.randomAlphabetic(count);
    }

    private static User user(String c) {
        return User.builder()
            .username(c)
            .email(new Faker().internet().emailAddress(c))
            .password(new Faker().internet().password(8, 8, true, true, true))
            .build();
    }

    private static Task task(User creator, User executor) {
        return Task.builder()
            .status(Status.NEW)
            .priority(Priority.LOW)
            .content(randomStr())
            .creator(creator)
            .executor(executor)
            .build();
    }
}