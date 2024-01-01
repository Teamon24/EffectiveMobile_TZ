package org.effective_mobile.task_management_system.resource;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import org.effective_mobile.task_management_system.resource.json.PageResponsePojo;
import org.effective_mobile.task_management_system.resource.json.assignment.AssignmentResponsePojo;
import org.effective_mobile.task_management_system.resource.json.task.ChangedStatusResponsePojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskCreationRequestPojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskEditionRequestPojo;
import org.effective_mobile.task_management_system.resource.json.task.TaskResponsePojo;
import org.effective_mobile.task_management_system.resource.json.task.TasksFiltersRequestPojo;
import org.effective_mobile.task_management_system.security.CustomUserDetails;
import org.effective_mobile.task_management_system.service.TaskService;
import org.effective_mobile.task_management_system.utils.enums.Status;
import org.effective_mobile.task_management_system.utils.enums.converter.StatusConverter;
import org.effective_mobile.task_management_system.component.validator.ValidEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import static org.effective_mobile.task_management_system.resource.Api.PathParam.ID;

@RestController
@RequestMapping(Api.TASK)
@AllArgsConstructor
public class TaskResource {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody
    TaskResponsePojo createTask(
        @RequestBody @Valid TaskCreationRequestPojo taskCreationPayload,
        @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        return taskService.createTask(customUserDetails.getUserId(), taskCreationPayload);
    }

    @GetMapping("/{"+ ID + "}")
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody
    TaskResponsePojo getTask(@NotNull @PathVariable Long id) {
        return taskService.getTask(id);
    }

    @GetMapping
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody
    PageResponsePojo<TaskResponsePojo> getTasks(
        @RequestBody @Valid TasksFiltersRequestPojo tasksFiltersRequestPojo,
        @RequestParam(defaultValue = Api.QueryParam.Page.DEFAULT_INDEX, name = Api.QueryParam.Page.NAME) int pageNumber,
        @RequestParam(defaultValue = Api.QueryParam.Page.DEFAULT_SIZE, name = Api.QueryParam.Page.SIZE) int size
    ) {
        Pageable page = PageRequest.of(pageNumber, size);
        Page<TaskResponsePojo> tasksPage = taskService.getByCreatorOrExecutor(tasksFiltersRequestPojo, page);
        return new PageResponsePojo<>(tasksPage);
    }

    @PutMapping("/{"+ ID + "}")
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody
    TaskResponsePojo editTask(
        @PathVariable(name = ID) Long id,
        @RequestBody TaskEditionRequestPojo taskEditionRequestPojo
    ) {
        return taskService.editTask(id, taskEditionRequestPojo);
    }

    @DeleteMapping("/{"+ ID + "}")
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public void deleteTask(@PathVariable(name = ID) Long id) {
        taskService.deleteTask(id);
    }

    @PutMapping("/{"+ ID + "}" + Api.EXECUTOR)
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody
    AssignmentResponsePojo setExecutor(
        @PathVariable(name = ID) Long id,
        @RequestParam(Api.QueryParam.EXECUTOR) String executorUsername
    ) {
        return taskService.setExecutor(id, executorUsername);
    }

    @PutMapping("/{"+ ID + "}" + Api.UNASSIGN)
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public Long removeExecutor(@PathVariable(name = ID) Long id) {
        return taskService.unassign(id);
    }

    @PutMapping("/{"+ ID + "}" + Api.STATUS)
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody
    ChangedStatusResponsePojo setStatus(
        @PathVariable(name = ID) Long id,
        @RequestParam(name = Api.QueryParam.NEW_STATUS) @ValidEnum(clazz = Status.class) String newStatusStr
    ) {
        Status newStatus = new StatusConverter().convert(newStatusStr);
        return taskService.setStatus(id, newStatus);
    }
}
