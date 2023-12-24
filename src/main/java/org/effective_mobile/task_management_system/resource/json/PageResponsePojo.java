package org.effective_mobile.task_management_system.resource.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;

import java.util.LinkedHashMap;
import java.util.Map;

public class PageResponsePojo<E> implements ResponsePojo {

    @JsonProperty
    private Map<String, Object> page;

    public PageResponsePojo(Page<E> page) {
        this.page = new LinkedHashMap<>();
        this.page.put("pages", page.getTotalPages());
        this.page.put("total items", page.getTotalElements());
        this.page.put("page size", page.getSize());
        this.page.put("page", pageNumber(page));
        this.page.put("items on current page", page.get().count());
        this.page.put("items", page.getContent());
    }

    private String pageNumber(Page<E> page) {
        return page.getNumber() + "/" + lastPageNumber(page);
    }

    private int lastPageNumber(Page<E> page) {
        return page.getTotalPages() == 0 ? 0 : page.getTotalPages() - 1;
    }
}
