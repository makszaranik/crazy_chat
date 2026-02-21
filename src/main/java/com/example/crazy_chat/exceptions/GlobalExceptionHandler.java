package com.example.crazy_chat.exceptions;

import graphql.ErrorClassification;
import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ChatNotFoundException.class)
    GraphQLError chatNotFoundException(ChatNotFoundException exception, DataFetchingEnvironment environment) {
        return GraphQLError.newError()
            .message(exception.getMessage())
            .errorType(ErrorType.DataFetchingException)
            .path(environment.getExecutionStepInfo().getPath())
            .build();
    }

    @ExceptionHandler(ParticipantNotInChatException.class)
    GraphQLError chatNotFoundException(ParticipantNotInChatException exception, DataFetchingEnvironment environment) {
        return GraphQLError.newError()
            .message(exception.getMessage())
            .errorType(ErrorType.DataFetchingException)
            .path(environment.getExecutionStepInfo().getPath())
            .build();
    }

}
