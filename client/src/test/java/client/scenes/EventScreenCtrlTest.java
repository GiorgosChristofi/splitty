package client.scenes;

import commons.Event;
import commons.Expense;
import commons.Participant;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EventScreenCtrlTest {

    @Mock
    private EventScreenCtrl eventScreenCtrl = mock(EventScreenCtrl.class);
    private Participant participant1 = new Participant(1, "John");
    private Participant participant2 = new Participant(2, "Jane");
    private Participant participant3 = new Participant(3, "Mike");
    private Participant participant4 = new Participant(4, "Bob");
    private Event event = new Event();
    private Expense expense1 = new Expense("Drinks", 12, null, participant1);
    private Expense expense2 = new Expense("Food", 20, null, participant2);
    @Test
    void generateTextForExpenseLabelAllTest() {
        when(eventScreenCtrl.generateTextForExpenseLabel(expense1))
            .thenReturn(expense1.stringOnScreen() + "\n(All)");
        Set<Participant> payers = new HashSet<>();
        payers.add(participant1);
        payers.add(participant2);
        payers.add(participant3);
        payers.add(participant4);
        expense1.setParticipantToExpense(payers);
        assertTrue(expense1.getParticipantsInExpense().contains(participant1));
        String result = eventScreenCtrl.generateTextForExpenseLabel(expense1);
        assertEquals(result, "John paid 0.12" + '\u20ac' + " for Drinks\n" +
            "(All)");
    }
    @Test
    void generateTextForExpenseLabelCustomTest() {
        when(eventScreenCtrl.generateTextForExpenseLabel(expense1))
            .thenReturn(expense1.stringOnScreen() + "\n(John Jane )");
        Set<Participant> payers = new HashSet<>();
        payers.add(participant1);
        payers.add(participant2);
        expense1.setParticipantToExpense(payers);
        assertTrue(expense1.getParticipantsInExpense().contains(participant1));
        assertFalse(expense1.getParticipantsInExpense().contains(participant3));
        String result = eventScreenCtrl.generateTextForExpenseLabel(expense1);
        assertEquals(result, "John paid 0.12" + '\u20ac' + " for Drinks\n" +
            "(John Jane )");
    }
}