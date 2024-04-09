package client.scenes;

import client.utils.FormattingUtils;
import client.utils.ImageUtils;
import client.utils.Translation;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableColumn;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

import java.util.Date;
import java.util.LinkedList;

import static client.TestObservableUtils.stringToObservable;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;

@ExtendWith({ApplicationExtension.class, MockitoExtension.class})
class StatisticsScreenCtrlTest {
    @InjectMocks
    StatisticsScreenCtrl sut;
    @Mock
    MainCtrl mainCtrl;
    @Mock
    Translation translation;
    @Mock
    ImageUtils imageUtils;

    Event testEvent;
    Participant participant1;
    Participant participant2;
    Expense expense1;
    Expense expense2;

    @BeforeEach
    void setup(){
        testEvent = new Event("Title", new Date(42));
        participant1 = new Participant("Vox");
        participant2 = new Participant("Alastor");
        testEvent.addParticipant(participant1);
        testEvent.addParticipant(participant2);
        expense1 = new Expense("TVs", 500, null, participant1);
        expense2 = new Expense("Radios", 250, null, participant2);
        expense1.addParticipantToExpense(participant1);
        expense1.addParticipantToExpense(participant2);
        expense2.addParticipantToExpense(participant1);
        testEvent.addExpense(expense1);
        testEvent.addExpense(expense2);

        lenient().doReturn(stringToObservable("Binding!")).when(translation).getStringBinding(anyString());
    }

    @BeforeAll
    static void testFXSetup(){
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("monocle.platform", "Headless");
        System.setProperty("prism.order", "sw");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Test
    void shareTableContainsCells() {
        var result = sut.generateShareTable(testEvent);
        assertEquals(2, result.getColumns().size());

        LinkedList<ObservableValue> stringObservablesInTable = new LinkedList<>();
        result.getColumns().forEach( column -> {
            TableColumn.CellDataFeatures cellDataParticipant1 = new TableColumn.CellDataFeatures(result, column, participant1);
            TableColumn.CellDataFeatures cellDataParticipant2 = new TableColumn.CellDataFeatures(result, column, participant2);

            stringObservablesInTable.add(column.getCellValueFactory().call(cellDataParticipant1));
            stringObservablesInTable.add(column.getCellValueFactory().call(cellDataParticipant2));
        });

        LinkedList<String> stringsInTable = new LinkedList<>();
        stringObservablesInTable.forEach(string -> stringsInTable.add((String) string.getValue()));

        assertTrue(stringsInTable.contains(participant1.getName()));
        assertTrue(stringsInTable.contains(participant2.getName()));
        String share1 = FormattingUtils.getFormattedPrice(500);
        String share2 = FormattingUtils.getFormattedPrice(250);
        assertTrue(stringsInTable.contains(share1));
        assertTrue(stringsInTable.contains(share2));
    }

    @Test
    void shareTableEmpty() {
        Event eventWithoutExpensesOrParticipants = new Event("Title", null);
        var result = sut.generateShareTable(eventWithoutExpensesOrParticipants);
        assertEquals(2, result.getColumns().size());
    }
}