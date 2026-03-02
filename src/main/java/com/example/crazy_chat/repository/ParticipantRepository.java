package com.example.crazy_chat.repository;

import com.example.crazy_chat.domains.participant.ParticipantEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends MongoRepository<ParticipantEntity, String> {

    List<ParticipantEntity> findAllByIdIn(List<String> ids);

    Optional<ParticipantEntity> findParticipantEntityByUsername(String username);
}
