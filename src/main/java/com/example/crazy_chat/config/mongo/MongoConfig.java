package com.example.crazy_chat.config.mongo;

import com.example.crazy_chat.domains.participant.ParticipantEntity;
import com.example.crazy_chat.service.participant.ParticipantService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

import java.util.Optional;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

    @Bean
    AuditorAware<String> auditorAware(ParticipantService participantService) {
        return () -> Optional.ofNullable(participantService.getCurrentParticipant()).map(ParticipantEntity::getId);
    }

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

}
