package org.effective_mobile.task_management_system.validator.smart;

import org.effective_mobile.task_management_system.utils.JsonPojos;
import org.effective_mobile.task_management_system.utils.enums.Priority;
import org.effective_mobile.task_management_system.utils.enums.Status;
import org.effective_mobile.task_management_system.utils.enums.ValuableEnum;

import java.util.HashMap;
import java.util.Map;

public interface ValuableEnumByJsonPropertyNames {
    Map<String, Class<? extends ValuableEnum>> map = new HashMap<>() {{
       put(JsonPojos.Task.Field.PRIORITY, Priority.class);
       put(JsonPojos.Task.Field.STATUS, Status.class);
    }};

    static Class<? extends ValuableEnum> get(String fieldName) {
        Class<? extends ValuableEnum> enumClass = map.get(fieldName);
        if (enumClass == null) {
            String template = "There is no %s class for field '%s'";
            throw new RuntimeException(template.formatted(ValuableEnum.class.getSimpleName(), fieldName));
        }
        return enumClass;
    }
}
