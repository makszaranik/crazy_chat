package com.example.crazy_chat.config;

import com.example.crazy_chat.domains.message.FileMessageEntity;
import com.example.crazy_chat.domains.message.TextMessageEntity;
import com.example.crazy_chat.dto.message.FileMessageDto;
import com.example.crazy_chat.dto.message.TextMessageDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.ClassNameTypeResolver;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        ClassNameTypeResolver resolver = new ClassNameTypeResolver();
        resolver.addMapping(TextMessageEntity.class, "TextMessage");
        resolver.addMapping(FileMessageEntity.class, "FileMessage");
        return builder -> builder.type("Message", typeWiring -> typeWiring.typeResolver(resolver));
    }

}
