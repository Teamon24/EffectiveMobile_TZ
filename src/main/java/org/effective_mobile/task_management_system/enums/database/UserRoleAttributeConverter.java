package org.effective_mobile.task_management_system.enums.database;

import jakarta.persistence.Converter;
import org.effective_mobile.task_management_system.enums.UserRole;

@Converter
public class UserRoleAttributeConverter extends EnumNameAttributeConverter<String, UserRole> {

    @Override
    protected Class<UserRoleAttributeConverter> getConverterClassName() {
        return UserRoleAttributeConverter.class;
    }

    @Override
    protected Class<UserRole> getEnumClassName() {
        return UserRole.class;
    }
}
