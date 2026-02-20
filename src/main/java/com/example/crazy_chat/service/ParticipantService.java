package com.example.crazy_chat.service;

import com.example.crazy_chat.domains.participant.ParticipantEntity;
import org.springframework.stereotype.Service;

@Service
public class ParticipantService {

    public ParticipantEntity getCurrentParticipant() {
        return ParticipantEntity.builder().username("max").id("12415124213412").build();
    }

}
