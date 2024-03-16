package server.api;
import commons.Event;
import commons.Participant;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.ParticipantRepository;

import java.util.Date;


@Service
public class EventService {
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;

    /**
     * constructor
     * autowired - automatically inject instances of the parameters when creating an EventService
     * @param eventRepository used for handling events
     * @param participantRepository used for
     */
    @Autowired
    public EventService(EventRepository eventRepository,
                        ParticipantRepository participantRepository) {
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
    }

    /***
     * Creates a new Event based on a given title
     * @param title - the title of the new Event
     * @return a full, persisted Event object
     */
    public Event createEvent(String title){
        Date currentDate = new Date();
        Event event = new Event(title, currentDate);
        return eventRepository.save(event);
    }

    /**
     * edit the title of an event
     * @param eventId the event whose title we want to edit
     * @param newTitle the new title
     * @return the new title of the event is saved in the database
     */
    public Event editTitle (String eventId, String newTitle){
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        event.setTitle(newTitle);
        return eventRepository.save(event);
    }

    /**
     * add a new participant to an event, add a new participant to the repository
     * @param participantName the name of the participant we want to add
     * @param eventId the event to which we want to add a participant
     */
    public void addParticipantToEvent (String eventId, String participantName) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        Participant participant = new Participant(participantName);
        event.addParticipant(participant);
        eventRepository.save(event);
    }

}
