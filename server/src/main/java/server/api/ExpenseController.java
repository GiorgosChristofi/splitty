package server.api;
import commons.Expense;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/events")
public class ExpenseController {
    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    /**
     * Method responsible for the post command. It is used to update a specific
     * event expenses
     * @param eventId identifies the even by the specific ID
     * @param expense maps the expense to the specific even specified by the ID
     * @return the status of the task
     */
    @PostMapping("/{eventId}/expenses")
    public ResponseEntity<Void> addExpenseToEvent(@PathVariable String eventId, @RequestBody
        Expense expense) {
        expenseService.addExpense(eventId, expense);
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
    public ResponseEntity<Set<Expense>> getAllExpensesForEvent(@PathVariable String eventId) {
        Set<Expense> expenses = expenseService.getAllExpenses(eventId);
        return ResponseEntity.ok(expenses);
    }

    /**
<<<<<<< HEAD
<<<<<<< HEAD
     *
=======
>>>>>>> b693a523caaeb29967cfe3f89abe2cd132efb36c
=======
>>>>>>> b693a523caaeb29967cfe3f89abe2cd132efb36c
     * Deletes an expense from an event
     * @param id the id of the expense
     * @param eventId the id of the event to delete the expense from
     * @return a status indicating whether the even was deleted
     */
    @DeleteMapping("/{eventId}/expenses/{id}")
    public ResponseEntity<String> deleteExpenseFromEvent(@PathVariable String eventId,
                                                         @PathVariable ("id") Long id) {
        expenseService.deleteExpense(eventId, id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{eventId}/expenses/{id}")
    public ResponseEntity<Expense> editExpense(@PathVariable long id,
                                               @RequestBody Expense expense) {
        // This does not need the eventId
        // but to keep it in line with the mapping (/api/events) it has one
        // feel free to change if you come up with a better solution
        // (these changes were made because Expense lost its Event field to fix a bug
        // which prevents it from knowing its Event on its own
        // so deleting an Expense now requires the Event ID)
        // this is the only method here impacted this way
        Expense updatedExpense = expenseService.editExpense(id, expense);
        return ResponseEntity.ok(updatedExpense);
<<<<<<< HEAD
=======

>>>>>>> b693a523caaeb29967cfe3f89abe2cd132efb36c
    }
}