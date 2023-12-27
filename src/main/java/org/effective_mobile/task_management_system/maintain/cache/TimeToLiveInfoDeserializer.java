package org.effective_mobile.task_management_system.maintain.cache;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.effective_mobile.task_management_system.maintain.cache.CacheSettings;
import org.effective_mobile.task_management_system.exception.ToEnumConvertException;
import org.effective_mobile.task_management_system.exception.messages.ExceptionMessages;
import org.effective_mobile.task_management_system.utils.enums.converter.EnumNameConverter;

import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Десериализатор для класса {@link CacheSettings.TimeToLiveInfo}. Используется в десериализации настроек кэша.
 */
public class TimeToLiveInfoDeserializer extends JsonDeserializer<CacheSettings.TimeToLiveInfo> {

    @Override
    public CacheSettings.TimeToLiveInfo deserialize(
        JsonParser jp,
        DeserializationContext ctxt
    ) throws IOException {

        ObjectCodec codec = jp.getCodec();
        JsonNode ttl = codec.readTree(jp);
        TimeUnit found = null;
        Integer value = null;
        for (TimeUnit timeUnit : TimeUnit.values()) {
            JsonNode timeUnitNodeLC = ttl.get(timeUnit.name().toLowerCase());
            JsonNode timeUnitNodeUC = ttl.get(timeUnit.name().toUpperCase());
            JsonNode timeUnitNode = timeUnitNodeLC != null ? timeUnitNodeLC : timeUnitNodeUC;
            if (timeUnitNode != null) {
                try {
                    found = TimeUnit.valueOf(timeUnit.name().toUpperCase());
                    value = timeUnitNode.asInt();
                    break;
                } catch (IllegalArgumentException ignored) {}
            }
        }

        if (found == null) {
            Iterator<String> fieldNames = ttl.fieldNames();
            String message = ExceptionMessages.getMessage(
                "exception.enum.conversion",
                fieldNames.hasNext() ? fieldNames.next() : null,
                TimeUnit.class.getSimpleName(),
                StringUtils.join(EnumNameConverter.names(TimeUnit.class), ", ")
            );
            throw new ToEnumConvertException(message);
        }

        return new CacheSettings.TimeToLiveInfo(found, value);
    }
}
