package server.api;

import commons.Event;
import commons.Expense;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import server.database.EventRepository;
import server.database.ExpenseRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {
    @Mock
    private ExpenseRepository mockExpenseRepository;

    @Mock
    private EventRepository mockEventRepository;

    @InjectMocks
    private ExpenseService mockExpenseService;

    /**
     * Tests the getExpensesForEvents
     */
    @Test
    public void getAllExpensesForEventTest() {
        String eventId = "mockEventId";
        List<Expense> expectedExpenses = new ArrayList<>();
        expectedExpenses.add(new Expense("Expense 1", 100,
            null, null, null));
        expectedExpenses.add(new Expense("Expense 2", 200,
            null, null, null));
        when(mockExpenseRepository.findByEventId(anyString()))
            .thenReturn(expectedExpenses);
        List<Expense> actualExpenses =
            mockExpenseService.getAllExpenses(eventId);
        assertEquals(expectedExpenses, actualExpenses);
    }

    /**
     * Tests the getExpensesForEvents
     */
    @Test
    public void calculateTotalExpensesForEventTest() {
        String eventId = "mockEventId";
        List<Expense> expectedExpenses = new ArrayList<>();
        expectedExpenses.add(new Expense("Expense 1", 100,
            null, null, null));
        expectedExpenses.add(new Expense("Expense 2", 200,
            null, null, null));
        when(mockExpenseRepository.findByEventId(anyString()))
            .thenReturn(expectedExpenses);
        List<Expense> actualExpenses = mockExpenseService
            .getAllExpenses(eventId);
        int sum = 0;
        for (Expense actualExpense : actualExpenses)
            sum += actualExpense.getPriceInCents();
        assertEquals(sum, 300);
    }

    /**
     * Tests the deleteExpense for one deletion
     */
    @Test
    public void deleteExpenseTest() {
        Event mockEvent = new Event("Sample Event");
        Expense mockExpense = new Expense("mockExpense", 100,
            null, mockEvent, null);
        mockEvent.addExpense(mockExpense);
        when(mockExpenseRepository.findById(anyString()))
            .thenReturn(Optional.of(mockExpense));
        mockExpenseService.deleteExpense(mockExpense.getId());
        assertFalse(mockEvent.getExpenses().contains(mockExpense));
    }

    /**
     * Tests the deleteExpense for more than one deletion
     */
    @Test
    public void deleteMultipleExpensesTest() {
        Event mockEvent = new Event("Sample Event");
        Expense expense1 = new Expense("mockExpense1", 100,
            null, mockEvent, null);
        Expense expense2 = new Expense("mockExpense2", 200,
            null, mockEvent, null);
        mockEvent.addExpense(expense1);
        mockEvent.addExpense(expense2);
        when(mockExpenseRepository.findById(expense1.getId()))
            .thenReturn(Optional.of(expense1));
        when(mockExpenseRepository.findById(expense2.getId()))
            .thenReturn(Optional.of(expense2));
        mockExpenseService.deleteExpense(expense1.getId());
        mockExpenseService.deleteExpense(expense2.getId());
        assertFalse(mockEvent.getExpenses().contains(expense1));
        assertFalse(mockEvent.getExpenses().contains(expense2));
    }
}