package com.example.crazy_chat.repository;

import com.example.crazy_chat.domains.participant.ParticipantEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ParticipantRepository extends MongoRepository<ParticipantEntity, String> {

    List<ParticipantEntity> findAllByIdIn(List<String> ids);

}
