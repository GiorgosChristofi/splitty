package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventCodeGeneratorTest {

    /***
     * Test to make sure IDs are not null, and generated to the right length
     */
    @Test
    public void idFormatTest(){
        String id = EventCodeGenerator.generateID();
        assertNotNull(id);
        assertEquals(EventCodeGenerator.codeLength, id.length());
    }

    /***
     * Test to make sure IDs are unique
     */
    @Test
    public void idDiffersTest(){
        String idFirst = EventCodeGenerator.generateID();
        String idSecond = EventCodeGenerator.generateID();
        assertNotEquals(idFirst, idSecond);
    }

}