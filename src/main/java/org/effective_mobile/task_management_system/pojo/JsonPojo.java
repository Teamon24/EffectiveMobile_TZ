package org.effective_mobile.task_management_system.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Интерфейс - общий для всех pojo, которые будут конвертированы в json.
 */
public interface JsonPojo {

    /**
     * Переводит данный pojo-объекта в json-строку.
     * @param usePrettyPrinter true - значит переводить в строку, используя
     *                         {@link ObjectMapper#writerWithDefaultPrettyPrinter()} (удобочитаемое форматирование
     *                         с табуляциями и переносами на новую строку), false - перевод в json-строку без
     *                         форматирования.
     * @param mapper
     * @return json-строка pojo-объекта.
     */
    @JsonIgnore
    default String toJson(ObjectMapper mapper, final boolean usePrettyPrinter) {
        try {
            return usePrettyPrinter
                ? mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this)
                : mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @JsonIgnore
    default String toPrettyJson(ObjectMapper objectMapperInstance) {
        return this.toJson(objectMapperInstance, true);
    }
}
