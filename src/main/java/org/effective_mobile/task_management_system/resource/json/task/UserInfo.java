package org.effective_mobile.task_management_system.resource.json.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.resource.json.ResponsePojo;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements ResponsePojo {
    private Long id;
    private String username;
}
