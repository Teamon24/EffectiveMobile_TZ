package org.effective_mobile.task_management_system.resource;

import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("@authenticationComponent.isAuthenticated()")
public interface AuthenticatedResource {}