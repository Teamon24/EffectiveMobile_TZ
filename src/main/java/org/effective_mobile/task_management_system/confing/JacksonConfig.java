package org.effective_mobile.task_management_system.confing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Настройка jackson.
 */
@Configuration
public class JacksonConfig {

    /**
     * @return настройки {@link ObjectMapper}, которые будут добавлены к default-ным настройкам,
     * предоставляемым при spring-boot-автоконфигурации.
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer getObjectMapperBuilderCustomizer() {
        return jacksonObjectMapperBuilder -> jacksonObjectMapperBuilder.modulesToInstall(new Jdk8Module());
    }

    /**
     * Предназначен для получения экземпляра {@link ObjectMapper} с согласованными настройками в ситуациях,
     * когда его inject в качетсве бина невозможен.
     * @return json-маппер, настройки которого должны совпадать с настройками из
     * {@link JacksonConfig#getObjectMapperBuilderCustomizer()}.
     */
    public static ObjectMapper getObjectMapperInstance() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new Jdk8Module());
        return mapper;
    }


}
