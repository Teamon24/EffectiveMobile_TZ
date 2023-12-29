package org.effective_mobile.task_management_system.docs;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.effective_mobile.task_management_system.resource.Api;
import org.effective_mobile.task_management_system.resource.json.PageResponsePojo;
import org.effective_mobile.task_management_system.resource.json.assignment.AssignmentResponsePojo;
import org.effective_mobile.task_management_system.resource.json.task.ChangedStatusResponsePojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskCreationRequestPojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskEditionRequestPojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskResponsePojo;
import org.effective_mobile.task_management_system.resource.json.task.TasksFiltersRequestPojo;
import org.effective_mobile.task_management_system.security.CustomUserDetails;
import org.effective_mobile.task_management_system.utils.enums.Status;
import org.effective_mobile.task_management_system.utils.validator.ValidEnum;

import static io.swagger.v3.oas.annotations.enums.ParameterIn.PATH;
import static io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY;
import static org.effective_mobile.task_management_system.utils.docs.Docs.TASK_ID_DESCRIPTION;

public interface TaskResourceDocs {

    @Tag(name = "Создание задачи")
    TaskResponsePojo createTask(
        @RequestBody @Valid TaskCreationRequestPojo taskCreationPayload,
        CustomUserDetails customUserDetails
    );

    @Tag(name = "Получение задачи")
    TaskResponsePojo getTask(
        @Parameter(
            in = PATH,
            name = Api.PathParam.ID,
            description = TASK_ID_DESCRIPTION) Long id);

    @Tag(name = "Пагинация и фильтрация задач")
    PageResponsePojo<TaskResponsePojo> getTasks(
        @RequestBody @Valid TasksFiltersRequestPojo tasksFiltersRequestPojo,
        @Parameter(
            in = QUERY,
            name = Api.QueryParam.Page.NAME,
            description = "Номер страницы") int pageNumber,
        @Parameter(
            in = QUERY,
            name = Api.QueryParam.Page.SIZE,
            description = "Размер страницы") int size
    );

    @Tag(name = "Редактирование задачи")
    TaskResponsePojo editTask(
        @Parameter(
            in = PATH,
            name = Api.PathParam.ID,
            description = TASK_ID_DESCRIPTION) Long id,
        @RequestBody TaskEditionRequestPojo taskEditionRequestPojo
    );

    @Tag(name = "Удаление задачи")
    void deleteTask(
        @Parameter(
            in = PATH,
            name = Api.PathParam.ID,
            description = TASK_ID_DESCRIPTION) Long id);

    @Tag(name = "Назначение исполнителя")
    AssignmentResponsePojo setExecutor(
        @Parameter(
            in = PATH,
            name = Api.PathParam.ID,
            description = TASK_ID_DESCRIPTION) Long id,
        @Parameter(
            in = QUERY,
            name = Api.QueryParam.EXECUTOR,
            description = "Назначаемый исполнитель") String executorUsername
    );

    @Tag(name = "Удаление исполнителя")
    Long removeExecutor(
        @Parameter(
            in = PATH,
            name = Api.PathParam.ID,
            description = TASK_ID_DESCRIPTION) Long id);

    @Tag(name = "Изменение статуса")
    ChangedStatusResponsePojo setStatus(
        @Parameter(
            in = PATH,
            name = Api.PathParam.ID,
            description = TASK_ID_DESCRIPTION) Long id,
        @Parameter(
            in = QUERY,
            name = Api.QueryParam.NEW_STATUS,
            description = "Новый статус") @ValidEnum(clazz = Status.class) String newStatusStr
    );
}
