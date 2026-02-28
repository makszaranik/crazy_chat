package com.example.crazy_chat.service;

import com.example.crazy_chat.domains.file.FileMetadataEntity;
import com.example.crazy_chat.exceptions.NoSuchMetadataEntity;
import com.example.crazy_chat.repository.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FileMetadataService {

    private final FileMetadataRepository repository;

    public FileMetadataEntity save(FileMetadataEntity metadataEntity) {
        return repository.save(metadataEntity);
    }

    public FileMetadataEntity findMetadataEntityById(String id) {
        return repository.findById(id)
            .orElseThrow(() -> new NoSuchMetadataEntity("Metadata with id " + id + " not found"));
    }

}
