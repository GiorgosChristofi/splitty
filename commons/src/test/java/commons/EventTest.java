package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EventTest {
    Event event;
    Participant participant1;
    @BeforeEach
    public void setUp(){
        event = new Event("title", null);
        participant1 = new Participant("Alastor");
    }
    /**
     * Constructor test
     */
    @Test
    public void checkConstructor() {
        var e = new Event("title", null);
        assertEquals("title", e.getTitle());
    }
    /**
     * Test for getting and setting title
     */
    @Test
    public void getterSetterTitleCheck(){
        assertEquals("title", event.getTitle());
        event.setTitle("new title");
        assertEquals("new title", event.getTitle());
    }
    /**
     * Test for adding and removing participants
     */
    @Test
    public void participantsCheck(){
        assertTrue(event.getParticipants().isEmpty());
        event.addParticipant(participant1);
        assertSame(1, event.getParticipants().size());
        event.removeParticipant(participant1);
        assertTrue(event.getParticipants().isEmpty());
    }
    /**
     * Test for adding and removing expenses
     */
    @Test
    public void expensesCheck(){
        assertTrue(event.getExpenses().isEmpty());
        Expense ex = new Expense();
        event.addExpense(ex);
        assertSame(1, event.getExpenses().size());
        event.removeExpense(ex);
        assertTrue(event.getExpenses().isEmpty());
    }
    /**
     * Equality checker for equal events
     */
    @Test
    public void equalsHashCode() {
        var b = event;
        assertEquals(event, b);
        assertEquals(event.hashCode(), b.hashCode());
    }
    /**
     * Equality checker for unequal events
     */
    @Test
    public void notEqualsHashCode() {
        var b = new Event("title2", null);
        assertNotEquals(event, b);
        assertNotEquals(event.hashCode(), b.hashCode());
    }
    /**
     * Test for toString
     */
    @Test
    public void hasToString() {
        var actual = event.toString();
        assertTrue(actual.contains(Event.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("expenses"));
        assertTrue(actual.contains("id"));
        assertTrue(actual.contains("participants"));
        assertTrue(actual.contains("title"));
    }

    /***
     * Test to make sure IDs are not null, and generated to the right length
     */
    @Test
    public void idFormatTest(){
        String id = event.getId();
        assertEquals(Event.codeLength, id.length());
    }

    /***
     * Test to make sure IDs are unique
     */
    @Test
    public void idDiffersTest(){
        Event event1 = new Event("title", null);
        Event event2 = new Event("new title", null);
        assertNotEquals(event1.getId(), event2.getId());
    }

    /***
     * Test of 2 uneven expenses splitting evenly correctly
     */
    @Test
    public void splitBasicTest(){
        int cents = 100;
        Participant participant2 = new Participant("Husk");
        event.addParticipant(participant1);
        event.addParticipant(participant2);

        Expense expense1 = new Expense();
        expense1.setPriceInCents(20*cents);
        expense1.setOwedTo(participant1);

        Expense expense2 = new Expense();
        expense2.setPriceInCents(10*cents);
        expense2.setOwedTo(participant2);

        event.addExpense(expense1);
        event.addExpense(expense2);

        var result = event.getOwedShares();
        assertEquals(5*cents, result.get(participant1));
        assertEquals(-5*cents, result.get(participant2));
    }

    /***
     * Test of 3 equal expenses that don't round nicely cancelling out in the end
     */
    @Test
    public void splitRoundingCancelsOutTest(){
        int cents = 100;
        Participant participant2 = new Participant("Val");
        Participant participant3 = new Participant("Vox");
        event.addParticipant(participant1);
        event.addParticipant(participant2);
        event.addParticipant(participant3);

        for (Participant participant:
             event.getParticipants()) {
            Expense expense = new Expense();
            expense.setPriceInCents(10*cents);
            expense.setOwedTo(participant);
            event.addExpense(expense);
        }

        var result = event.getOwedShares();
        assertEquals(0, result.get(participant1));
        assertEquals(0, result.get(participant2));
        assertEquals(0, result.get(participant3));
    }

    /***
     * Test based on assumption that in situations with fractional cents,
     * the debt should just be ignored
     */
    @Test
    public void splitCentRounding(){
        Participant participant2 = new Participant("Lilith");
        event.addParticipant(participant1);
        event.addParticipant(participant2);

        Expense uglyExpense = new Expense();
        uglyExpense.setPriceInCents(1);
        uglyExpense.setOwedTo(participant1);
        event.addExpense(uglyExpense);

        // the assumption is that if there's a fractional cent to transfer, no one transfers
        var result = event.getOwedShares();
        assertEquals(0, result.get(participant1));
        assertEquals(0, result.get(participant2));
    }

    /***
     * All expenses should be added up per participant
     * p1: 0
     * p2: 10
     * p3: 20 + 30
     */
    @Test
    public void spendingBasicTest(){
        int cents = 100;
        Participant participant2 = new Participant("Zestial");
        Participant participant3 = new Participant("Carmine");
        event.addParticipant(participant1);
        event.addParticipant(participant2);
        event.addParticipant(participant3);

        Expense expense1 = new Expense();
        expense1.setPriceInCents(10*cents);
        expense1.setOwedTo(participant2);
        event.addExpense(expense1);

        Expense expense2 = new Expense();
        expense2.setPriceInCents(20*cents);
        expense2.setOwedTo(participant3);
        event.addExpense(expense2);

        Expense expense3 = new Expense();
        expense3.setPriceInCents(30*cents);
        expense3.setOwedTo(participant3);
        event.addExpense(expense3);

        var result = event.getSpendingPerPerson();
        assertEquals(0, result.get(participant1));
        assertEquals(10*cents, result.get(participant2));
        assertEquals(50*cents, result.get(participant3));
    }

    @Test
    public void zeroSpendingTest(){
        Participant participant2 = new Participant("Rosie");
        Participant participant3 = new Participant("Velvette");
        event.addParticipant(participant1);
        event.addParticipant(participant2);
        event.addParticipant(participant3);

        var result = event.getSpendingPerPerson();
        assertEquals(0, result.get(participant1));
        assertEquals(0, result.get(participant2));
        assertEquals(0, result.get(participant3));
    }

    @Test
    public void sumBasicTest(){
        int cents = 100;

        Expense expense1 = new Expense();
        expense1.setPriceInCents(10*cents);
        event.addExpense(expense1);

        Expense expense2 = new Expense();
        expense2.setPriceInCents(20*cents);
        event.addExpense(expense2);

        Expense expense3 = new Expense();
        expense3.setPriceInCents(30*cents);
        event.addExpense(expense3);

        var result = event.getTotalSpending();
        assertEquals((10+20+30)*cents, result);
    }

    @Test
    public void noExpenseTest(){
        var result = event.getTotalSpending();
        assertEquals(0, result);
    }

    @Test
    public void GetAndSetLastActivity() {
        LocalDateTime time = LocalDateTime.of(0, 1, 1, 0, 0);
        event.setLastActivity(time);
        assertEquals(event.getLastActivity(), time);
    }
}
