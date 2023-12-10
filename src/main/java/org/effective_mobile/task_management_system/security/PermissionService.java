package org.effective_mobile.task_management_system.security;

import org.effective_mobile.task_management_system.entity.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PermissionService {

    public List<SimpleGrantedAuthority> getPermissions(User user) {
        return user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority(role.getName().name()))
            .toList();
    }
}
