package com.example.crazy_chat.repository;

import com.example.crazy_chat.domains.chat.ChatEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface ChatRepository extends MongoRepository<ChatEntity, String> {

    Optional<ChatEntity> findChatEntityById(String id);

}
