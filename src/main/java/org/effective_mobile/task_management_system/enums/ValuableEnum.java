package org.effective_mobile.task_management_system.enums;

import java.io.Serializable;

public interface ValuableEnum<Value> extends Serializable {
    Value getValue();
}