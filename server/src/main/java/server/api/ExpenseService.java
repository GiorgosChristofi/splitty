package server.api;

import commons.Event;
import commons.Expense;
import commons.Participant;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import server.database.EventRepository;
import server.database.ExpenseRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final EventRepository eventRepository;

    /**
     *
     * @param expenseRepository the repository containing the expenses
     * @param eventRepository the repository containing the events
     */
    public ExpenseService(ExpenseRepository expenseRepository, EventRepository eventRepository) {
        this.expenseRepository = expenseRepository;
        this.eventRepository = eventRepository;
    }

    /**
     * The method assigns an expense to an event, identified by its id
     * @param eventId the id by which we find the event
     * @param expense the specific expense for that event
     */
    public void addExpense(String eventId, Expense expense) {
        Optional<Event> opEvent = eventRepository.findById(eventId);
        if (opEvent.isEmpty())
            throw new IllegalArgumentException("Event not found");
        Event event = opEvent.get();
        expense.setEvent(event);
        expenseRepository.save(expense);
        event.addExpense(expense);
        eventRepository.save(event);
    }

    /**
     * The method return the list of all the expenses for a specified event
     * @param eventId the id by which we find the event
     * @return a list of all the expenses of the specific event
     */
    public List<Expense> getAllExpenses(String eventId) {
        return expenseRepository.findByEventId(eventId);
    }

    /**
     * The method calculates the total amount of money for a specific
     * event
     * @param eventId the id by which we find the event
     * @return the total of all the expenses of the specific event
     * in cents
     */
    public int calculateTotalExpenses(String eventId) {
        List<Expense> expenses = getAllExpenses(eventId);
        return expenses.stream().mapToInt(Expense::getPriceInCents).sum();
    }

    /**
     * !!!NEEDS FURTHER INSPECTION
     * @param id the id of the expense to be deleted
     * @return true if the expense was deleted, false otherwise
     */
    public void deleteExpense(long id) {
        Expense expense = expenseRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Expense not found"));
        Event event = expense.getEvent();
        event.removeExpense(expense);
        eventRepository.save(event);
    }
}
