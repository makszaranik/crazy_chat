package com.example.crazy_chat.config.graphql;

import com.example.crazy_chat.dto.message.output.MessageResponse;
import com.example.crazy_chat.dto.message.output.FileMessageResponse;
import com.example.crazy_chat.dto.message.output.TextMessageResponse;
import graphql.scalars.ExtendedScalars;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class GraphQLConfig {


    /*    @Bean
        public RuntimeWiringConfigurer runtimeWiringConfigurer() {
            ClassNameTypeResolver resolver = new ClassNameTypeResolver();
            resolver.addMapping(TextMessageResponse.class, "TextMessage");
            resolver.addMapping(FileMessageResponse.class, "FileMessage");
            return builder -> builder
                .scalar(ExtendedScalars.GraphQLLong)
                .type("Message", typeWiring -> typeWiring.typeResolver(resolver));
        }*/

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return builder -> builder
            .type("Message", typeWiring -> typeWiring.typeResolver(env -> {
                MessageResponse message = env.getObject();
                return switch (message) {
                    case TextMessageResponse textMessage -> env.getSchema().getObjectType("TextMessage");
                    case FileMessageResponse fileMessage -> env.getSchema().getObjectType("FileMessage");
                };
            }))
            .scalar(ExtendedScalars.GraphQLLong);
    }
}
