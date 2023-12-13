package org.effective_mobile.task_management_system.resource;

import org.effective_mobile.task_management_system.pojo.task.PrioritiesResponse;
import org.effective_mobile.task_management_system.pojo.task.StatusesResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(Api.TASK_INFO)
public class TaskInfoResource {

    @GetMapping(Api.PRIORITIES)
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody PrioritiesResponse getPriorities() {
        return new PrioritiesResponse();
    }

    @GetMapping(Api.STATUSES)
    @PreAuthorize("@authenticationComponent.isAuthenticated()")
    public @ResponseBody StatusesResponse getStatuses() {
        return new StatusesResponse();
    }
}
