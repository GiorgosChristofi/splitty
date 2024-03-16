package server.api;

import commons.Event;
import commons.Participant;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.ParticipantRepository;

@Service
public class ParticipantService {
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;

    /**
     * constructor
     * autowired - automatically inject instances of the parameters when creaitng an EventService
     * @param eventRepository used for handling events
     * @param participantRepository used for handling participants
     */
    @Autowired
    public ParticipantService(EventRepository eventRepository, ParticipantRepository participantRepository) {
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
    }

    /**
     * remove a participant, make sure both the event and participants exist or else
     * throw an exception
     * @param eventId the id of the event of the participant
     * @param participantId the participant we want to remove
     */
    public void removeParticipant(String eventId, Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        event.removeParticipant(participant);
        eventRepository.save(event);
    }

    /**
     * edit the details of a participant that is in an event
     * @param participantId the participant whose details we want to change
     * @param participantDetails the details of the participant
     * @return the participants modified details are now saved in the database
     */

    public Participant editParticipant(Long participantId, Participant participantDetails) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));
        participant.setName(participantDetails.getName());
        return participantRepository.save(participant);
    }


}
