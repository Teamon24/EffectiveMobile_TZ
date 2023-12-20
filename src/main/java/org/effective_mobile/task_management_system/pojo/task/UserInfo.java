package org.effective_mobile.task_management_system.pojo.task;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.effective_mobile.task_management_system.pojo.ResponsePojo;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo implements ResponsePojo {
    private Long id;
    private String username;
}
