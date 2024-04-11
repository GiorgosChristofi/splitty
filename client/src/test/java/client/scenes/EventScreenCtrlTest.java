package client.scenes;

import client.utils.ImageUtils;
import client.utils.ServerUtils;
import client.utils.Translation;
import commons.Event;
import commons.Expense;
import commons.Participant;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationExtension;

@ExtendWith({MockitoExtension.class, ApplicationExtension.class})
class EventScreenCtrlTest {
    @Mock
    ServerUtils server;
    @Mock
    MainCtrl mainCtrl;
    @Mock
    Translation translation;
    @Mock
    LanguageIndicatorCtrl languageCtrl;
    @Mock
    ImageUtils imageUtils;

    @InjectMocks
    EventScreenCtrl sut;
    private Participant participant1;
    private Participant participant2;
    private Participant participant3;
    private Participant participant4;
    private Event event;
    private Expense expense1;

    @BeforeEach
    public void setup(){
        participant1 = new Participant(1, "John");
        participant2 = new Participant(2, "Jane");
        participant3 = new Participant(3, "Mike");
        participant4 = new Participant(4, "Bob");
        event = new Event("Title", null);
        event.addParticipant(participant1);
        event.addParticipant(participant2);
        event.addParticipant(participant3);
        event.addParticipant(participant4);
        expense1 = new Expense("Drinks", 12, null, participant1);
        Expense expense2 = new Expense("Food", 20, null, participant2);
        event.addExpense(expense1);
        event.addExpense(expense2);
    }

    @BeforeAll
    static void testFXSetup(){
        System.setProperty("testfx.robot", "glass");
        System.setProperty("testfx.headless", "true");
        System.setProperty("glass.platform", "Monocle");
        System.setProperty("monocle.platform", "Headless");
        System.setProperty("prism.order", "sw");
    }
}