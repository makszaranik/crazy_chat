package com.example.crazy_chat.config;

import com.example.crazy_chat.dto.message.input.FileMessageRequest;
import com.example.crazy_chat.dto.message.input.TextMessageRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.ClassNameTypeResolver;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        ClassNameTypeResolver resolver = new ClassNameTypeResolver();
        resolver.addMapping(TextMessageRequest.class, "TextMessage");
        resolver.addMapping(FileMessageRequest.class, "FileMessage");
        return builder -> builder.type("Message", typeWiring -> typeWiring.typeResolver(resolver));
    }

}
