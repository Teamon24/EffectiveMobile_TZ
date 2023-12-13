package org.effective_mobile.task_management_system.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;

import java.util.LinkedHashMap;
import java.util.Map;

public class PageResponse<E> {

    @JsonProperty
    private Map<String, Object> page;

    public PageResponse(Page<E> page) {
        this.page = new LinkedHashMap<>();
        this.page.put("total items", page.getTotalElements());
        this.page.put("page size", page.getSize());
        this.page.put("page", page.getNumber() + "/" + totalPagesNumber(page));
        this.page.put("page items", page.getContent());
    }

    private int totalPagesNumber(Page<E> page) {
        return page.getTotalPages() == 0 ? 0 : page.getTotalPages() - 1;
    }
}
