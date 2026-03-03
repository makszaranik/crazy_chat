package com.example.crazy_chat.repository;

import com.example.crazy_chat.domains.eventOutbox.EventOutBoxEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventOutboxRepository extends MongoRepository<EventOutBoxEntity, String> {

    List<EventOutBoxEntity> findOutBoxEventEntitiesByStatus(EventOutBoxEntity.AggregateType status);

}
