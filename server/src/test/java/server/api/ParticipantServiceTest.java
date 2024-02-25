package server.api;

import commons.Event;
import commons.Participant;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.database.EventRepository;
import server.database.ParticipantRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ParticipantServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @InjectMocks
    private ParticipantService participantService;

    /**
     * create a mock event and participant to see if it can be removed, not needing to interact
     * with a real database
     */
    @Test
    public void testRemoveParticipantFromEvent() {
        Event mockEvent = new Event("Sample Event", null);
        Participant mockParticipant = new Participant("John Doe", mockEvent);
        mockEvent.addParticipant(mockParticipant);
        when(participantRepository.findById(anyLong())).thenReturn(Optional.of(mockParticipant));
        participantService.removeParticipant(mockParticipant.getId());
        assertFalse(mockEvent.getParticipants().contains(mockParticipant));
        verify(eventRepository).save(mockEvent);
    }

    /**
     * now we are removing two participants, check if it is works without
     * having to use the actual database
     */
    @Test
    public void testRemoveMultipleParticipantsFromEvent() {
        Event mockEvent = new Event("Sample Event", null);
        Participant mockParticipant1 = new Participant("Participant One", mockEvent);
        Participant mockParticipant2 = new Participant("Participant Two", mockEvent);
        mockEvent.addParticipant(mockParticipant1);
        mockEvent.addParticipant(mockParticipant2);
        when(participantRepository.findById(mockParticipant1.getId())).thenReturn(Optional.of(mockParticipant1));
        when(participantRepository.findById(mockParticipant2.getId())).thenReturn(Optional.of(mockParticipant2));
        participantService.removeParticipant(mockParticipant1.getId());
        participantService.removeParticipant(mockParticipant2.getId());
        assertFalse(mockEvent.getParticipants().contains(mockParticipant1));
        assertFalse(mockEvent.getParticipants().contains(mockParticipant2));
        verify(eventRepository, times(2)).save(mockEvent);
    }

    /**
     * check if we try to remove a participant from an event that does not exist
     * and throw an exception in that case
     */
    @Test
    public void removeNonexistentParticipantThrowsException() {
        long nonexistentEventId = 999L;
        try{
            participantService.removeParticipant(nonexistentEventId);
            fail("Expected EntityNotFoundException not thrown");
        } catch (EntityNotFoundException ex) {
            assertEquals("Participant not found", ex.getMessage());
        }
        verify(eventRepository, never()).save(any(Event.class));
    }

    /**
     * check to see if the editing of the participant works
     */
    @Test
    public void checkEditParticipantToEvent() {
        Event mockEvent = new Event("Sample Event", null);
        Participant mockParticipant = new Participant("John Doe", mockEvent);
        Participant updatedDetails = new Participant("Jane Doe", mockEvent);
        when(participantRepository.findById(mockParticipant.getId())).thenReturn(Optional.of(mockParticipant));
        when(participantRepository.save(any(Participant.class))).thenReturn(updatedDetails);
        Participant result = participantService.editParticipant(mockParticipant.getId(), updatedDetails);
        assertEquals("Jane Doe", result.getName());
    }

    /**
     * test to see what happens if we try to edit a participant's detail
     * if the event he was assigned to did not exist
     */
    @Test
    public void editParticipantToEventNonExistentEventCheck() {
        Participant updatedDetails = new Participant("Jane Doe", null);
        assertThrows(EntityNotFoundException.class, () -> {
            participantService.editParticipant(updatedDetails.getId(), updatedDetails);
        }, "Should throw EntityNotFoundException for a nonexistent event.");
    }

    /**
     * test to see what would happen if we tried to edit a participant that was not
     * existing in the first place
     */
    @Test
    public void editParticipantToEventNonExistentParticipantCheck() {
        String participantName = "Jane Doe";
        Event mockEvent = new Event("Sample Event", null);
        Participant mockParticipant = new Participant(participantName, mockEvent);
        assertThrows(EntityNotFoundException.class, () -> participantService.editParticipant(mockParticipant.getId(), mockParticipant));
    }
}
