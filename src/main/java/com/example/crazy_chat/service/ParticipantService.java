package com.example.crazy_chat.service;

import com.example.crazy_chat.domains.participant.ParticipantEntity;
import com.example.crazy_chat.dto.participant.output.ParticipantChatEventResponse;
import com.example.crazy_chat.dto.participant.output.ParticipantResponse;
import com.example.crazy_chat.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
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
    private final Sinks.Many<ParticipantChatEventResponse> events = Sinks.many().multicast().directBestEffort();

    public ParticipantEntity getCurrentParticipant() {
        return ParticipantEntity.builder().username("max").id("12415124213412").build();
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

    public Optional<ParticipantEntity> fetchParticipantByUsername(String username){
        return participantRepository.findParticipantEntityByUsername(username);
    }

    public ParticipantEntity save(ParticipantEntity participant){
        return participantRepository.save(participant);
    }

    public List<ParticipantEntity> fetchParticipantsByIds(List<String> participantIds) {
        return participantRepository.findAllByIdIn(participantIds);
    }

    public ParticipantResponse toParticipantResponse(ParticipantEntity participant) {
        return ParticipantResponse.builder()
            .id(participant.getId())
            .username(participant.getUsername())
            .build();
    }

    public List<ParticipantResponse> toParticipantResponse(List<ParticipantEntity> participants) {
        return participants.stream().map(this::toParticipantResponse).toList();
    }

    public Map<String, List<ParticipantResponse>> fetchParticipantsByChatIds(List<String> participantIds) {
        List<ParticipantEntity> participants = participantRepository.findAllByIdIn(participantIds);
        return participants.stream()
            .collect(Collectors.groupingBy(
                ParticipantEntity::getId,
                Collectors.mapping(this::toParticipantResponse, Collectors.toList())
            ));
    }

}
