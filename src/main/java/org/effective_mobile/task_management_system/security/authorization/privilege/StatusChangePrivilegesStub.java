package org.effective_mobile.task_management_system.security.authorization.privilege;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;

import java.util.Set;

import static org.effective_mobile.task_management_system.database.repository.PrivilegeRepositoryStub.STUB_FOR_PRIVILEGES;

@Component
public class StatusChangePrivilegesStub implements StatusChangePrivileges {

    @Override
    public Set<String> canStartNewTask() {
        return Sets.newHashSet(STUB_FOR_PRIVILEGES);
    }

    @Override
    public Set<String> canSuspendTask() {
        return Sets.newHashSet(STUB_FOR_PRIVILEGES);
    }

    @Override
    public Set<String> canChangeStatusOfActiveTask() {
        return Sets.newHashSet(STUB_FOR_PRIVILEGES);
    }
}
