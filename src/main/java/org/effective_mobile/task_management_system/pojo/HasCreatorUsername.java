package org.effective_mobile.task_management_system.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface HasCreatorUsername {
    @JsonIgnore
    String getCreatorUsername();
}
