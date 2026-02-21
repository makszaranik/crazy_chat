package com.example.crazy_chat.service;

import com.example.crazy_chat.domains.participant.ParticipantEntity;
import com.example.crazy_chat.dto.participant.output.ParticipantEventResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Service
public class ParticipantService {

    private final Sinks.Many<ParticipantEventResponse> events = Sinks.many().multicast().directBestEffort();

    public ParticipantEntity getCurrentParticipant() {
        return ParticipantEntity.builder().username("max").id("12415124213412").build();
    }

    public void publishEvent(ParticipantEventResponse eventDto) {
        events.tryEmitNext(eventDto);
    }

    public Flux<ParticipantEventResponse> fetchEvents() {
        return events.asFlux();
    }


}
