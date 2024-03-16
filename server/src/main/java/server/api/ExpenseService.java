package server.api;

import commons.Event;
import commons.Expense;
import commons.Participant;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.database.EventRepository;
import server.database.ExpenseRepository;
import server.database.ParticipantRepository;

import java.util.Optional;
import java.util.Set;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final EventRepository eventRepository;
    private final ParticipantRepository participantRepository;

    /**
     *
     * @param expenseRepository the repository containing the expenses
     * @param eventRepository the repository containing the events
     * @param participantRepository the repository containing the participants
     */
    public ExpenseService(ExpenseRepository expenseRepository, EventRepository eventRepository,
                          ParticipantRepository participantRepository) {
        this.expenseRepository = expenseRepository;
        this.eventRepository = eventRepository;
        this.participantRepository = participantRepository;
    }

    /**
     * The method assigns an expense to an event, identified by its id
     * @param eventId the id by which we find the event
     * @param expense the specific expense for that event
     */
    @Transactional
    public void addExpense(String eventId, Expense expense) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        long extractedParticipantId = expense.getOwedTo().getId();
        Participant participant = participantRepository.findById(extractedParticipantId)
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));
        expense.setOwedTo(participant);
        event.addExpense(expense);
        expenseRepository.save(expense);
        eventRepository.save(event);
    }

    /**
     * The method return the list of all the expenses for a specified event
     * @param eventId the id by which we find the event
     * @return a set of all the expenses of the specific event
     */
    public Set<Expense> getAllExpenses(String eventId) {
        Optional<Event> eventOptional = eventRepository.findById(eventId);
        if(eventOptional.isEmpty()){
            return null;
        } else {
            Event event = eventOptional.get();
            return event.getExpenses();
        }

    }

    /**
     * Deletes an Expense
     * @param eventId the id of the corresponding event
     * @param id the id of the expense to be deleted
     */
    public void deleteExpense(String eventId, long id) {
        Expense expense = expenseRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Expense not found"));
        Event event = eventRepository.findById(eventId)
                        .orElseThrow(() -> new EntityNotFoundException("Event not found"));
        event.removeExpense(expense);
        eventRepository.save(event);
    }

    /**
     * Edit the expense
     * @param id ID of the expense
     * @param newExpense The new expense
     * @return The updated expense
     */
    public Expense editExpense(long id, Expense newExpense) {
        Expense expense = expenseRepository.findById(id) //
                .orElseThrow(() -> new EntityNotFoundException("Expense not found"));
        long extractedParticipantId = newExpense.getOwedTo().getId();
        Participant participant = participantRepository.findById(extractedParticipantId)
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));
        expense.setDate(newExpense.getDate());
        expense.setName(newExpense.getName());
        expense.setOwedTo(participant);
        expense.setPriceInCents(newExpense.getPriceInCents());
        return expenseRepository.save(expense);
    }
}
