package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ParticipantTest {
    /**
     * Test for constructor
     */
    @Test
    public void checkConstructor() {
        Event e = new Event();
        var p = new Participant("name");
        assertEquals("name", p.getName());
        assertTrue(p.getExpensesOwedTo().isEmpty());
    }

    /**
     * Test for name getter and setter
     */
    @Test
    public void setterGetterNameTest(){
        Event e = new Event();
        var p = new Participant("name");
        assertEquals("name", p.getName());
        p.setName("new name");
        assertEquals("new name", p.getName());
    }

    /**
     * Test for adding and removing expenses
     */
    @Test
    public void expensesTest(){
        Event e = new Event();
        var p = new Participant("name");
        assertTrue(p.getExpensesOwedTo().isEmpty());
        Expense ex = new Expense();
        p.addExpense(ex);
        assertTrue(p.getExpensesOwedTo().contains(ex));
        p.removeExpense(ex);
        assertTrue(p.getExpensesOwedTo().isEmpty());
    }

    @Test
    public void otherFieldsTest(){
        Participant p = new Participant("participant");
        p.setBic(1234);
        p.setIban("NL12");
        p.setEmail("oo@pp.com");
        assertEquals(1234, p.getBic());
        assertEquals("NL12", p.getiban());
        assertEquals("oo@pp.com", p.getEmail());
    }
    /**
     * Equality checker for equal expenses
     */
    @Test
    public void equalsHashCode() {
        Event e = new Event();
        var a = new Participant("name");
        var b = new Participant("name");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }
    /**
     * Equality checker for unequal expenses
     */
    @Test
    public void notEqualsHashCode() {
        Event e = new Event();
        var a = new Participant("name");
        var b = new Participant("new name");
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }
    /**
     * Tests for toString
     */
    @Test
    public void hasToString() {
        var actual = new Participant("name").toString();
        assertTrue(actual.contains("expensesOwedTo"));
        assertTrue(actual.contains("id"));
    }
}
