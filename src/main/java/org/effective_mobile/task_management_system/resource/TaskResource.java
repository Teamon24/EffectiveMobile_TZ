package org.effective_mobile.task_management_system.resource;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.effective_mobile.task_management_system.enums.Status;
import org.effective_mobile.task_management_system.enums.converter.StatusConverter;
import org.effective_mobile.task_management_system.pojo.PageResponse;
import org.effective_mobile.task_management_system.pojo.assignment.AssignmentResponse;
import org.effective_mobile.task_management_system.pojo.task.ChangedStatusResponse;
import org.effective_mobile.task_management_system.pojo.task.TaskCreationPayload;
import org.effective_mobile.task_management_system.pojo.task.TaskEditionPayload;
import org.effective_mobile.task_management_system.pojo.task.TaskJsonPojo;
import org.effective_mobile.task_management_system.security.JwtPrincipal;
import org.effective_mobile.task_management_system.service.TaskService;
import org.effective_mobile.task_management_system.validator.ValidEnum;
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

@RestController
@RequestMapping(Api.TASK)
@AllArgsConstructor
public class TaskResource {

    private final TaskService taskService;

    @PostMapping
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody Long createTask(
        @RequestBody @Valid TaskCreationPayload taskCreationPayload,
        @AuthenticationPrincipal JwtPrincipal jwtPrincipal)
    {
        return taskService.createTask(jwtPrincipal.getUserId(), taskCreationPayload);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody TaskJsonPojo getTask(@NotNull @PathVariable Long id) {
        return taskService.getTask(id);
    }

    @GetMapping
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody PageResponse<TaskJsonPojo> getTasks(
        @RequestParam(required = false, name = "creatorUsername") String creatorUsername,
        @RequestParam(required = false, name = "executorUsername") String executorUsername,
        @RequestParam(defaultValue = "0", name = "page") @NotNull int pageNumber,
        @RequestParam(defaultValue = "3", name = "size") @NotNull int size
    ) {
        Pageable page = PageRequest.of(pageNumber, size);
        Page<TaskJsonPojo> tasksPage = taskService.getByCreatorOrExecutor(creatorUsername, executorUsername, page);
        return new PageResponse<>(tasksPage);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody TaskJsonPojo editTask(
        @NotNull @PathVariable Long id,
        @Valid @RequestBody @NonNull TaskEditionPayload taskEditionPayload
    ) {
        return taskService.editTask(id, taskEditionPayload);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@NotNull @PathVariable Long id) {
        taskService.deleteTask(id);
    }

    @PutMapping("/{id}" + Api.EXECUTOR)
    public @ResponseBody AssignmentResponse setExecutor(
        @NotNull @PathVariable Long id,
        @RequestParam(Api.EXECUTOR_USERNAME) String executorUsername
    ) {
        return taskService.setExecutor(id, executorUsername);
    }

    @PutMapping("/{id}" + Api.UNASSIGN)
    public Long removeExecutor(@NotNull @PathVariable Long id) {
        return taskService.unassign(id);
    }

    @PutMapping("/{id}" + Api.STATUS)
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody ChangedStatusResponse setStatus(
        @NotNull @PathVariable Long id,
        @RequestParam(name = Api.NEW_STATUS_PARAM) @ValidEnum(clazz = Status.class) String newStatusStr
    ) {
        Status newStatus = new StatusConverter().convert(newStatusStr);
        return taskService.setStatus(id, newStatus);
    }

}
