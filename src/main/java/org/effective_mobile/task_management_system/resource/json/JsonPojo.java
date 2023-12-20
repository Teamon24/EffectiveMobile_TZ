package org.effective_mobile.task_management_system.resource.json;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.MinimalPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Интерфейс - общий для всех pojo, которые будут конвертированы в json.
 */
public interface JsonPojo {

    /**
     * Переводит данный pojo-объекта в json-строку.
     *
     * @param asPretty true - значит переводить в строку с форматированием (с табуляциями и переносами на новую строку)
     *                 false - перевод в json-строку без форматирования.
     * @param mapper   экземпляр класса ObjectMapper.
     * @return json-строка pojo-объекта.
     */
    @JsonIgnore
    default String asJson(ObjectMapper mapper, final boolean asPretty) {
        try {
            return asPretty
                ? mapper.writer(new DefaultPrettyPrinter()).writeValueAsString(this)
                : mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    @JsonIgnore
    default String asPrettyJson(ObjectMapper objectMapper) {
        return this.asJson(objectMapper, true);
    }
}
