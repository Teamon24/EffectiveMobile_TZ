package org.effective_mobile.task_management_system.resource.json

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.databind.ObjectMapper

interface RequestPojo : JsonPojo

interface ResponsePojo : JsonPojo

interface JsonPojoId : JsonPojo { val id: Long? }

/**
 * Интерфейс - общий для всех pojo, которые будут конвертированы в json.
 */
interface JsonPojo {

    /**
     * Переводит данный pojo-объекта в json-строку.
     *
     * @param asPretty true - значит переводить в строку с форматированием (с табуляциями и переносами на новую строку)
     * false - перевод в json-строку без форматирования.
     * @param mapper   экземпляр класса ObjectMapper.
     * @return json-строка pojo-объекта.
     */
    @JsonIgnore
    fun asJson(mapper: ObjectMapper, asPretty: Boolean): String? {
        return try {
            if (asPretty) {
                mapper.writer(DefaultPrettyPrinter()).writeValueAsString(this)
            } else {
                mapper.writeValueAsString(this)
            }
        } catch (e: JsonProcessingException) {
            throw IllegalStateException(e.message, e)
        }
    }

    @JsonIgnore
    fun asPrettyJson(objectMapper: ObjectMapper): String? {
        return asJson(objectMapper, true)
    }
}

