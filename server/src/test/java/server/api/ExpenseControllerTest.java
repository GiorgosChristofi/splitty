package server.api;

import commons.Expense;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseControllerTest {
    @InjectMocks
    private ExpenseController expenseController;

    @Mock
    private ExpenseService expenseService;
    @Test
    public void addExpenseToEventTest() {
        String eventId = "sampleEventId";
        Expense expense = new Expense("Sample Expense",
            100, null, null, null);
        doNothing().when(expenseService).addExpense(anyString(), any(Expense.class));
        ResponseEntity<Void> responseEntity
            = expenseController.addExpenseToEvent(eventId, expense);
        verify(expenseService).addExpense(eq(eventId), eq(expense));
        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    }

    @Test
    public void getAllExpensesForEventTest() {
        String eventId = "sampleEventId";
        List<Expense> mockExpenses = Arrays.asList(
            new Expense("Expense 1", 100,
                null, null, null),
            new Expense("Expense 2", 200,
                null, null, null)
        );
        when(expenseService.getAllExpenses(eventId)).thenReturn(mockExpenses);
        ResponseEntity<List<Expense>> responseEntity
            = expenseController.getAllExpensesForEvent(eventId);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        List<Expense> returnedExpenses = responseEntity.getBody();
        assertNotNull(returnedExpenses);
        assertEquals(mockExpenses.size(), returnedExpenses.size());
        for (int i = 0; i < mockExpenses.size(); i++)
            assertEquals(mockExpenses.get(i), returnedExpenses.get(i));
    }
    //Need to add tests for the delete method
}