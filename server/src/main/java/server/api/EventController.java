package server.api;
import org.springframework.beans.factory.annotation.Autowired;
import commons.Expense;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {
    private final EventService eventService;

    /**
     * constructor
     * @param eventService sets the current eventService
     */
    @Autowired
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }
    
    //Empty event controller class
    private ExpenseService expenseService;

    /**
     * Method responsible for the post command. It is used to update a specific
     * event expenses
     * @param eventId identifies the even by the specific ID
     * @param expense maps the expense to the specific even specified by the ID
     * @return the status of the task
     */
    @PostMapping("/{eventId}/expenses")
    public ResponseEntity<Void> addExpenseToEvent(@PathVariable Long eventId, @RequestBody
    Expense expense) {
        expenseService.addExpenseToEvent(eventId, expense);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * The method is responsible for creating a GET request
     * that returns the desired information
     * @param eventId identifies the even by the specific ID
     * @return the status of the specific page and the information
     * (a list of all expenses for a specific event)
     */
    @GetMapping("/{eventId}/expenses")
    public ResponseEntity<List<Expense>> getAllExpensesForEvent(@PathVariable Long eventId) {
        List<Expense> expenses = expenseService.getAllExpensesForEvent(eventId);
        return ResponseEntity.ok(expenses);
    }

    /**
     * The method is responsible for creating a GET request
     * that returns the desired information
     * @param eventId identifies the even by the specific ID
     * @return the sum of all the expenses for a specific event
     */
    @GetMapping("/{eventId}/total-expenses")
    public ResponseEntity<Double> calculateTotalExpensesForEvent(@PathVariable Long eventId) {
        double totalExpenses = expenseService.calculateTotalExpensesForEvent(eventId);
        return ResponseEntity.ok(totalExpenses);
    }
}