package server.api;

import commons.Event;
import commons.Expense;
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
     * !!!NEEDS FURTHER INSPECTION
     * @param id the id of the expense to be deleted
     */
    public void deleteExpense(long id) {
        Expense expense = expenseRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException("Expense not found"));
        Event event = expense.getEvent();
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
                .orElseThrow(() -> new EntityNotFoundException("Expense not found!"));
        expense.setEvent(newExpense.getEvent());
        expense.setDate(newExpense.getDate());
        expense.setName(newExpense.getName());
        expense.setOwedTo(newExpense.getOwedTo());
        expense.setPriceInCents(newExpense.getPriceInCents());
        return expenseRepository.save(expense);
    }
}
