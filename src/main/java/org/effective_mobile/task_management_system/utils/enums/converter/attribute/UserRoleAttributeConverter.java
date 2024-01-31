package org.effective_mobile.task_management_system.utils.enums.converter.attribute;

import org.effective_mobile.task_management_system.utils.enums.UserRole;

public class UserRoleAttributeConverter extends ValuableEnumAttributeConverter<UserRole, String> {
    @Override
    Class<UserRole> enumClass() {
        return UserRole.class;
    }
}