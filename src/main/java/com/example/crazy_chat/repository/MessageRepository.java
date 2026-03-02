package com.example.crazy_chat.repository;

import com.example.crazy_chat.domains.message.MessageEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Map;

public interface MessageRepository extends MongoRepository<MessageEntity, String> {

    List<MessageEntity> findAllByChatId(String chatId);

    List<MessageEntity> findByChatIdIn(List<String> chatIds);
}
