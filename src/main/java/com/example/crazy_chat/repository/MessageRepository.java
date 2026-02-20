package com.example.crazy_chat.repository;

import com.example.crazy_chat.domains.message.MessageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<MessageEntity, String> {

}
