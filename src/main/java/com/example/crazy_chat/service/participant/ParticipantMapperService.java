package com.example.crazy_chat.service.participant;

import com.example.crazy_chat.domains.participant.ParticipantEntity;
import com.example.crazy_chat.dto.participant.output.ParticipantResponse;
import org.springframework.stereotype.Service;

@Service
public class ParticipantMapperService {

    public ParticipantResponse toParticipantResponse(ParticipantEntity participant) {
        return ParticipantResponse.builder()
            .id(participant.getId())
            .username(participant.getUsername())
            .build();
    }


}
