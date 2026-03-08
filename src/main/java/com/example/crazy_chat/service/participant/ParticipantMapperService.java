package com.example.crazy_chat.service.participant;

import com.example.crazy_chat.domains.participant.ParticipantEntity;
import com.example.crazy_chat.dto.participant.output.ParticipantResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipantMapperService {

    public ParticipantResponse toParticipantResponse(ParticipantEntity participant) {
        return ParticipantResponse.builder()
            .id(participant.getId())
            .username(participant.getUsername())
            .build();
    }

    public List<ParticipantResponse> toParticipantResponse(List<ParticipantEntity> participants) {
        return participants.stream().map(this::toParticipantResponse).toList();
    }


}
