package org.effective_mobile.task_management_system.component;

import lombok.NonNull;
import net.datafaker.Faker;
import net.datafaker.providers.base.Internet;
import org.apache.commons.lang3.RandomStringUtils;
import org.effective_mobile.task_management_system.AssertionsUtils;
import org.effective_mobile.task_management_system.entity.Task;
import org.effective_mobile.task_management_system.entity.User;
import org.effective_mobile.task_management_system.enums.Priority;
import org.effective_mobile.task_management_system.enums.Status;
import org.effective_mobile.task_management_system.exception.AssignmentException;
import org.effective_mobile.task_management_system.exception.NothingToUpdateInTaskException;
import org.effective_mobile.task_management_system.exception.messages.ExceptionMessages;
import org.effective_mobile.task_management_system.pojo.task.TaskCreationPayload;
import org.effective_mobile.task_management_system.pojo.task.TaskEditionPayload;
import org.effective_mobile.task_management_system.repository.FilteredAndPagedTaskRepository;
import org.effective_mobile.task_management_system.repository.TaskRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.stream.Stream;

import static org.effective_mobile.task_management_system.exception.messages.ExceptionMessages.getMessage;

class TaskComponentTest {

    private TaskRepository taskRepository;
    private FilteredAndPagedTaskRepository filteredAndPagedTaskRepository;
    private UserComponent userComponent;

    private TaskComponent component;
    private static final Faker faker = new Faker();
    private static final Internet internet = faker.internet();


    @BeforeEach
    public void setUp() {
        taskRepository = Mockito.mock(TaskRepository.class);
        userComponent = Mockito.mock(UserComponent.class);
        filteredAndPagedTaskRepository = Mockito.mock(FilteredAndPagedTaskRepository.class);

        component = new TaskComponent(
            taskRepository,
            filteredAndPagedTaskRepository,
            userComponent
        );
    }

    /**
     * Test for {@link TaskComponent#createTask(User, TaskCreationPayload)}.
     */
    @ParameterizedTest
    @MethodSource("creatorAndTaskCreationPayload")
    public void createTaskTest(User creator, TaskCreationPayload taskCreationPayload) {
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
    * Test for {@link TaskComponent#setExecutor(Long, User)}.
    */
    @ParameterizedTest
    @MethodSource("setDistinctExecutorTestData")
    public void setExecutorThatNotSameAsPreviousTest(Long taskId, User newExecutor, Task task) {
        Task taskSpy = Mockito.spy(task);
        Mockito.when(taskRepository.findOrThrow(Task.class, taskId)).thenReturn(taskSpy);
        component.setExecutor(taskId, newExecutor);

        Mockito.verify(taskRepository).findOrThrow(Task.class, taskId);
        Mockito.verify(taskRepository).save(taskSpy);

        Assertions.assertEquals(Status.ASSIGNED, taskSpy.getStatus());
        Assertions.assertEquals(newExecutor, taskSpy.getExecutor());
    }

    /**
     * Test for {@link TaskComponent#setExecutor(Long, User)}.
     */
    @ParameterizedTest
    @MethodSource("setSameExecutorTestData")
    public void setExecutorThatSameAsPreviousTest(Long taskId, User newExecutor, Task task) {
        Task taskSpy = Mockito.spy(task);
        Status status = taskSpy.getStatus();
        User oldExecutor = taskSpy.getExecutor();
        Mockito.when(taskRepository.findOrThrow(Task.class, taskId)).thenReturn(taskSpy);

        AssignmentException assignmentException = Assertions.assertThrows(
            AssignmentException.class,
            () -> component.setExecutor(taskId, newExecutor)
        );

        String message = getMessage("exception.task.executor.same", oldExecutor);
        Assertions.assertEquals(message, assignmentException.getMessage());

        Mockito.verify(taskRepository).findOrThrow(Task.class, taskId);
        Mockito.verify(taskRepository, Mockito.times(0)).save(taskSpy);

        Assertions.assertEquals(status, taskSpy.getStatus());
        Assertions.assertEquals(oldExecutor, taskSpy.getExecutor());
    }

    /**
    * Test for {@link TaskComponent#editTask(Long, TaskEditionPayload)}.
    */
    @ParameterizedTest
    @MethodSource("editTaskWhenChangesData")
    public void editTask_WhenAtLeastOneFieldIsNewTest(Long id, TaskEditionPayload payload, Task task) {
        Mockito.when(taskRepository.findOrThrow(Task.class, id)).thenReturn(task);
        Assertions.assertDoesNotThrow(() -> component.editTask(id, payload));
        Mockito.verify(taskRepository).save(task);
        if (payload.getContent() != null)
            Assertions.assertEquals(task.getContent(), payload.getContent());
        if (payload.getPriority() != null)
            Assertions.assertEquals(task.getPriority().name(), payload.getPriority());
    }

    /**
     * Test for {@link TaskComponent#editTask(Long, TaskEditionPayload)}.
     */
    @ParameterizedTest
    @MethodSource("editTaskWhenNoChangesData")
    public void editTask_WhenNothingToChangeTest(
        Long id,
        TaskEditionPayload payload,
        Task task
    ) {
        Task taskSpy = Mockito.spy(task);
        Mockito.doReturn(id).when(taskSpy).getId();
        Mockito.when(taskRepository.findOrThrow(Task.class, id)).thenReturn(taskSpy);
        NothingToUpdateInTaskException ex = Assertions.assertThrows(
            NothingToUpdateInTaskException.class,
            () -> component.editTask(id, payload)
        );

        Mockito.verify(taskRepository, Mockito.times(0)).save(taskSpy);

        Assertions.assertEquals(
            ExceptionMessages.getMessage("exception.task.update.nothing", Task.class.getSimpleName(), id),
            ex.getMessage()
        );
    }


    public static Stream<Arguments> editTaskWhenChangesData() {
        Priority taskPriority = Priority.HIGH;
        String taskContent = randomContent();

        TaskEditionPayload payload  = new TaskEditionPayload(null,        "LOW");
        TaskEditionPayload payload2  = new TaskEditionPayload(taskContent, "LOW");
        TaskEditionPayload payload3 = new TaskEditionPayload(randomContent(), taskPriority.name());
        TaskEditionPayload payload4 = new TaskEditionPayload(randomContent(), null);

        return Stream.of(
            Arguments.of(1L, payload, getTask(taskPriority, taskContent)),
            Arguments.of(1L, payload2, getTask(taskPriority, taskContent)),
            Arguments.of(1L, payload3, getTask(taskPriority, taskContent)),
            Arguments.of(1L, payload4, getTask(taskPriority, taskContent))
        );
    }

    public static Stream<Arguments> editTaskWhenNoChangesData() {
        Priority taskPriority = Priority.HIGH;
        String taskContent = randomContent();

        TaskEditionPayload payload  = new TaskEditionPayload(null,        null);
        TaskEditionPayload payload3 = new TaskEditionPayload(null,        taskPriority.name());
        TaskEditionPayload payload2 = new TaskEditionPayload(taskContent, taskPriority.name());
        TaskEditionPayload payload4 = new TaskEditionPayload(taskContent, null);

        return Stream.of(
            Arguments.of(1L, payload, getTask(taskPriority, taskContent)),
            Arguments.of(1L, payload2, getTask(taskPriority, taskContent)),
            Arguments.of(1L, payload3, getTask(taskPriority, taskContent)),
            Arguments.of(1L, payload4, getTask(taskPriority, taskContent))
        );
    }


    @NonNull
    private static String randomContent() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    private static Task getTask(Priority taskPriority, String taskContent) {
        return Task.builder()
            .creator(User.builder().username(internet.username()).build())
            .content(taskContent)
            .priority(taskPriority).build();
    }

    public static Stream<Arguments> setDistinctExecutorTestData() {
        User newExecutor = User.builder().username(faker.internet().username()).build();
        User oldExecutor = User.builder().username(faker.internet().username()).build();
        User creator     = User.builder().username(faker.internet().username()).build();

        Task taskToChange = Task.builder()
            .creator(creator)
            .executor(oldExecutor)
            .status(Status.NEW).build();
        return Stream.of(
            Arguments.of(1L, newExecutor, taskToChange)
        );
    }

    public static Stream<Arguments> setSameExecutorTestData() {
        User oldExecutor = User.builder().username(faker.internet().username()).build();
        User creator     = User.builder().username(faker.internet().username()).build();

        Task taskToChange = Task.builder()
            .creator(creator)
            .executor(oldExecutor)
            .status(Status.NEW).build();
        return Stream.of(
            Arguments.of(1L, oldExecutor, taskToChange)
        );
    }

    public static Stream<Arguments> creatorAndTaskCreationPayload() {
        TaskCreationPayload taskCreationPayload = new TaskCreationPayload();
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
}