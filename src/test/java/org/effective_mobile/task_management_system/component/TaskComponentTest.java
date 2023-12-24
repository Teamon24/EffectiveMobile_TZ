package org.effective_mobile.task_management_system.component;

import net.datafaker.Faker;
import net.datafaker.providers.base.Internet;
import org.apache.commons.lang3.RandomStringUtils;
import org.effective_mobile.task_management_system.AssertionsUtils;
import org.effective_mobile.task_management_system.database.entity.Task;
import org.effective_mobile.task_management_system.database.entity.User;
import org.effective_mobile.task_management_system.database.repository.FilteredAndPagedTaskRepositoryImpl;
import org.effective_mobile.task_management_system.database.repository.TaskRepository;
import org.effective_mobile.task_management_system.exception.AssignmentException;
import org.effective_mobile.task_management_system.exception.NothingToUpdateInTaskException;
import org.effective_mobile.task_management_system.exception.messages.ExceptionMessages;
import org.effective_mobile.task_management_system.resource.json.task.TaskCreationRequestPojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskEditionRequestPojo;
import org.effective_mobile.task_management_system.utils.enums.Priority;
import org.effective_mobile.task_management_system.utils.enums.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.effective_mobile.task_management_system.exception.messages.ExceptionMessages.getMessage;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
class TaskComponentTest {

    @Mock
    private TaskRepository taskRepository;
    private TaskComponent component;
    private static final Faker faker = new Faker();
    private static final Internet internet = faker.internet();

    @BeforeEach
    public void setUp() {
        component = new TaskComponent(
            taskRepository,
            Mockito.mock(FilteredAndPagedTaskRepositoryImpl.class)
        );
    }

    /**
     * Test for {@link TaskComponent#createTask(User, TaskCreationRequestPojo)}.
     */
    @ParameterizedTest
    @MethodSource("creatorAndTaskCreationRequestPojo")
    public void createTaskTest(User creator, TaskCreationRequestPojo taskCreationPayload) {
        ArgumentCaptor<Task> taskCaptor = ArgumentCaptor.forClass(Task.class);
        component.createTask(creator, taskCreationPayload);
        Mockito.verify(taskRepository).save(taskCaptor.capture());
        Task task = taskCaptor.getValue();

        Assertions.assertNull(task.getExecutor());
        AssertionsUtils.assertEnumEquals(taskCreationPayload.getPriority(), task.getPriority());

        Assertions.assertEquals(taskCreationPayload.getContent(), task.getContent());
        Assertions.assertEquals(Status.NEW, task.getStatus());
        User actualCreator = task.getCreator();

        AssertionsUtils.assertEquals(creator, actualCreator, User::getId);
        AssertionsUtils.assertEquals(creator, actualCreator, User::getUsername);
        AssertionsUtils.assertEquals(creator, actualCreator, User::getPassword);
        AssertionsUtils.assertEquals(creator, actualCreator, User::getEmail);
    }

    /**
    * Test for {@link TaskComponent#setExecutor(Task, User)}.
    */
    @ParameterizedTest
    @MethodSource("setDistinctExecutorTestData")
    public void setExecutorThatNotSameAsPreviousTest(User newExecutor, Task task) {
        component.setExecutor(task, newExecutor);

        Mockito.verify(taskRepository).save(task);
        Mockito.verifyNoMoreInteractions(taskRepository);

        Assertions.assertEquals(Status.ASSIGNED, task.getStatus());
        Assertions.assertEquals(newExecutor, task.getExecutor());
    }

    /**
     * Test for {@link TaskComponent#setExecutor(Task, User)}.
     */
    @ParameterizedTest
    @MethodSource("setSameExecutorTestData")
    public void setExecutorThatSameAsPreviousTest(User newExecutor, Task task) {
        User oldExecutor = task.getExecutor();

        AssignmentException assignmentException = Assertions.assertThrows(
            AssignmentException.class,
            () -> component.setExecutor(task, newExecutor)
        );

        String message = getMessage("exception.task.executor.same", oldExecutor);
        Assertions.assertEquals(message, assignmentException.getMessage());

        Mockito.verifyNoInteractions(taskRepository);
    }

    /**
    * Test for {@link TaskComponent#editTask(Task, TaskEditionRequestPojo)}.
    */
    @ParameterizedTest
    @MethodSource("editTaskWhenChangesData")
    public void editTask_WhenAtLeastOneFieldIsNewTest(TaskEditionRequestPojo payload, Task task) {
        Assertions.assertDoesNotThrow(() -> component.editTask(task, payload));
        if (payload.getContent() != null)
            Assertions.assertEquals(task.getContent(), payload.getContent());
        if (payload.getPriority() != null)
            Assertions.assertEquals(task.getPriority().name(), payload.getPriority());

        Mockito.verify(taskRepository).save(task);
        Mockito.verifyNoMoreInteractions(taskRepository);
    }

    /**
     * Test for {@link TaskComponent#editTask(Task, TaskEditionRequestPojo)}.
     */
    @ParameterizedTest
    @MethodSource("editTaskWhenNoChangesData")
    public void editTask_WhenNothingToChangeTest(
        TaskEditionRequestPojo payload,
        Task task
    ) {
        NothingToUpdateInTaskException ex = Assertions.assertThrows(
            NothingToUpdateInTaskException.class,
            () -> component.editTask(task, payload)
        );

        Mockito.verifyNoInteractions(taskRepository);

        Assertions.assertEquals(
            ExceptionMessages.getMessage("exception.task.update.nothing", Task.class.getSimpleName(), task.getId()),
            ex.getMessage()
        );
    }


    public static Stream<Arguments> editTaskWhenChangesData() {
        Priority taskPriority = Priority.HIGH;
        String taskContent = randomContent();

        TaskEditionRequestPojo payload  = new TaskEditionRequestPojo(null,        "LOW");
        TaskEditionRequestPojo payload2  = new TaskEditionRequestPojo(taskContent, "LOW");
        TaskEditionRequestPojo payload3 = new TaskEditionRequestPojo(randomContent(), taskPriority.name());
        TaskEditionRequestPojo payload4 = new TaskEditionRequestPojo(randomContent(), null);

        return Stream.of(
            Arguments.of(payload, getTaskSpy(randomId(), taskContent, taskPriority)),
            Arguments.of(payload2, getTaskSpy(randomId(), taskContent, taskPriority)),
            Arguments.of(payload3, getTaskSpy(randomId(), taskContent, taskPriority)),
            Arguments.of(payload4, getTaskSpy(randomId(), taskContent, taskPriority))
        );
    }

    public static Stream<Arguments> editTaskWhenNoChangesData() {
        Priority taskPriority = Priority.HIGH;
        String taskContent = randomContent();

        TaskEditionRequestPojo payload  = new TaskEditionRequestPojo(null,        null);
        TaskEditionRequestPojo payload3 = new TaskEditionRequestPojo(null,        taskPriority.name());
        TaskEditionRequestPojo payload2 = new TaskEditionRequestPojo(taskContent, taskPriority.name());
        TaskEditionRequestPojo payload4 = new TaskEditionRequestPojo(taskContent, null);

        return Stream.of(
            Arguments.of(payload, getTaskSpy(randomId(), taskContent, taskPriority)),
            Arguments.of(payload2, getTaskSpy(randomId(), taskContent, taskPriority)),
            Arguments.of(payload3, getTaskSpy(randomId(), taskContent, taskPriority)),
            Arguments.of(payload4, getTaskSpy(randomId(), taskContent, taskPriority))
        );
    }

    public static Stream<Arguments> setDistinctExecutorTestData() {
        User newExecutor = User.builder().username(faker.internet().username()).build();
        User oldExecutor = User.builder().username(faker.internet().username()).build();
        User creator     = User.builder().username(faker.internet().username()).build();
        return Stream.of(
            Arguments.of(newExecutor, getTaskSpy(randomId(), creator, oldExecutor))
        );
    }

    public static Stream<Arguments> setSameExecutorTestData() {
        User oldExecutor = User.builder().username(faker.internet().username()).build();
        User creator     = User.builder().username(faker.internet().username()).build();
        return Stream.of(
            Arguments.of(oldExecutor, getTaskSpy(randomId(), creator, oldExecutor))
        );
    }

    public static Stream<Arguments> creatorAndTaskCreationRequestPojo() {
        TaskCreationRequestPojo taskCreationPayload = new TaskCreationRequestPojo();
        taskCreationPayload.setContent(faker.text().text());
        taskCreationPayload.setPriority(Priority.HIGH.name());
        User user = User.builder()
            .username(faker.internet().username())
            .email(faker.internet().emailAddress())
            .password(faker.internet().password())
            .build();
        return Stream.of(
            Arguments.of(user, taskCreationPayload)
        );
    }

    private static Task getTaskSpy(long id, String taskContent, Priority taskPriority) {
        Task task = Mockito.spy(getTask(taskPriority, taskContent));
        Mockito.doReturn(id).when(task).getId();
        return task;
    }

    private static Task getTaskSpy(long id, User creator, User oldExecutor) {
        Task task = Mockito.spy(Task.builder()
            .creator(User.builder().username(internet.username()).build())
            .creator(creator)
            .executor(oldExecutor).build());
        Mockito.doReturn(id).when(task).getId();
        return task;
    }


    private static String randomContent() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    private static Long randomId() {
        return Long.valueOf(RandomStringUtils.randomNumeric(3));
    }

    private static Task getTask(Priority taskPriority, String taskContent) {
        return Task.builder()
            .creator(User.builder().username(internet.username()).build())
            .content(taskContent)
            .priority(taskPriority).build();
    }
}