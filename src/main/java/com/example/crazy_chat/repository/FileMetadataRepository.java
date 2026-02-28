package com.example.crazy_chat.repository;

import com.example.crazy_chat.domains.file.FileMetadataEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface FileMetadataRepository extends MongoRepository<FileMetadataEntity, String> {

    Optional<FileMetadataEntity> findById(String id);

}
