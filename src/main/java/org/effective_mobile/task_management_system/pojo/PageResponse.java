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
        this.page.put("page", pageNumber(page));
        this.page.put("items on current page", page.get().count());
        this.page.put("items", page.getContent());
    }

    private String pageNumber(Page<E> page) {
        return page.getNumber() + "/" + totalPagesNumber(page);
    }

    private int totalPagesNumber(Page<E> page) {
        return page.getTotalPages() == 0 ? 0 : page.getTotalPages() - 1;
    }
}
