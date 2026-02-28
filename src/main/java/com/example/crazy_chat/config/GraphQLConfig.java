package com.example.crazy_chat.config;

import com.example.crazy_chat.dto.message.input.FileMessageRequest;
import com.example.crazy_chat.dto.message.input.TextMessageRequest;
import com.example.crazy_chat.dto.message.output.FileMessageResponse;
import com.example.crazy_chat.dto.message.output.TextMessageResponse;
import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.ClassNameTypeResolver;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLConfig {

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        ClassNameTypeResolver resolver = new ClassNameTypeResolver();
        resolver.addMapping(TextMessageResponse.class, "TextMessage");
        resolver.addMapping(FileMessageResponse.class, "FileMessage");
        return builder -> builder
            .scalar(ExtendedScalars.GraphQLLong)
            .type("Message", typeWiring -> typeWiring.typeResolver(resolver));
    }

}
