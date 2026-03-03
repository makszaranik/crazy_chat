package com.example.crazy_chat.repository;

import com.example.crazy_chat.domains.message.outboxEvent.OutBoxEventEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OutBoxEventRepository extends MongoRepository<OutBoxEventEntity, String> {

    List<OutBoxEventEntity> findOutBoxEventEntitiesByStatus(OutBoxEventEntity.AggregateType status);

}
