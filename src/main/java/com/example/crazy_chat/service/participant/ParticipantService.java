package com.example.crazy_chat.service.participant;

import com.example.crazy_chat.domains.participant.ParticipantEntity;
import com.example.crazy_chat.dto.participant.output.ParticipantChatEventResponse;
import com.example.crazy_chat.dto.participant.output.ParticipantResponse;
import com.example.crazy_chat.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final ParticipantMapperService participantMapperService;
    private final Sinks.Many<ParticipantChatEventResponse> events = Sinks.many().multicast().directBestEffort();

    public ParticipantEntity getCurrentParticipant() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return participantRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Participant not found"));
    }

    public void publishEvent(ParticipantChatEventResponse eventDto) {
        events.tryEmitNext(eventDto);
    }

    public Flux<ParticipantChatEventResponse> fetchEvents() {
        return events.asFlux();
    }

    public ParticipantEntity fetchParticipantById(String participantId) {
        return participantRepository.findById(participantId)
            .orElseThrow(() -> new IllegalStateException("Participant with id " + participantId + " not found"));
    }

    public ParticipantEntity save(ParticipantEntity participant){
        return participantRepository.save(participant);
    }


    public Map<String, List<ParticipantResponse>> fetchParticipantsByChatIds(List<String> participantIds) {
        List<ParticipantEntity> participants = participantRepository.findAllByIdIn(participantIds);
        return participants.stream()
            .collect(Collectors.groupingBy(
                ParticipantEntity::getId,
                Collectors.mapping(participantMapperService::toParticipantResponse, Collectors.toList()))
            );
    }

    public Optional<ParticipantEntity> fetchParticipantByUsername(String username) {
        return participantRepository.findParticipantEntityByUsername(username);
    }
}
