package server.api;
import commons.Expense;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/expenses")
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
    public ResponseEntity<List<Expense>> getAllExpensesForEvent(@PathVariable String eventId) {
        List<Expense> expenses = expenseService.getAllExpenses(eventId);
        return ResponseEntity.ok(expenses);
    }

    /**
     * The method is responsible for creating a GET request
     * that returns the desired information
     * @param eventId identifies the even by the specific ID
     * @return the sum of all the expenses for a specific event
     */
    @GetMapping("/{eventId}/total-expenses")
    public ResponseEntity<Double> calculateTotalExpensesForEvent(@PathVariable String eventId) {
        double totalExpenses = expenseService.calculateTotalExpenses(eventId);
        return ResponseEntity.ok(totalExpenses);
    }

    /**
     *
     * @param id the id of the expense
     * @return a status indicating whether the even was deleted
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteExpenseFromEvent(@PathVariable ("id") Long id) {
        expenseService.deleteExpense(id);
        return ResponseEntity.ok().build();
    }
}