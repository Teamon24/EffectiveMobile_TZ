package org.effective_mobile.task_management_system.security.authorization.privilege;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class StatusChangePrivilegesImpl implements StatusChangePrivileges {

    @Override
    public Set<String> canStartNewTask() {
        return Sets.newHashSet("STATUS_EXECUTING");
    }

    @Override
    public Set<String> canSuspendTask() {
        return Sets.newHashSet("STATUS_SUSPENDING");
    }

    @Override
    public Set<String> canFinishTask() {
        return Sets.newHashSet("STATUS_FINISHING");
    }

    @Override
    public Set<String> canFinishSuspendedTask() {
        return Sets.newHashSet("STATUS_SUSPENDED_FINISHING");
    }

    @Override
    public Set<String> canResumeTask() {
        return Sets.newHashSet("STATUS_RESUMING");
    }

    @Override
    public Set<String> canResuspendTask() {
        return Sets.newHashSet("STATUS_FINISHED_SUSPENDING");
    }
}
