package client.scenes;

import client.utils.ServerUtils;
import client.utils.Translation;
import commons.Event;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class StartupScreenCtrlTest{

    private TestStartupScreenCtrl sut;
    private TestServerUtils testServerUtils;
    private TestMainController testMainController;
    @BeforeEach
    public void setup() {
        this.testServerUtils = new TestServerUtils();
        this.testMainController =  new TestMainController();
        sut = new TestStartupScreenCtrl(this.testServerUtils, this.testMainController, null);

    }

    @Test
    public void testInitialization(){
        assertTrue(sut.labelBindings.isEmpty());
        assertTrue(sut.textBoxBindings.isEmpty());
        assertTrue(sut.buttonBindings.isEmpty());
        sut.initialize(null, null);
        assertEquals(4, sut.labelBindings.size());
        assertEquals(2, sut.textBoxBindings.size());
        assertEquals(2, sut.buttonBindings.size());
    }

    @Test
    public void testCreateEventEmptyTitle(){
        String title = "";
        sut.textBoxText = title;
        sut.createEvent();
        assertTrue(sut.joinEventCalls.isEmpty());
        assertTrue(testServerUtils.calls.isEmpty());
    }

    @Test
    public void testCreateEventSuccess(){
        String title = "title";
        sut.textBoxText = title;
        sut.createEvent();
        assertEquals(testServerUtils.calls.size(), 1);
        assertEquals(sut.joinEventCalls.size(), 1);
    }
    @Test
    public void testJoinEventInvalidLength(){
        String inviteCode = "invalid";
        sut.textBoxText = inviteCode;
        sut.joinEventClicked();
        assertEquals(0, testServerUtils.calls.size());
        assertEquals(0, testMainController.calls.size());
        assertFalse(sut.labelBindings.isEmpty());
        assertTrue(sut.labelBindings.contains("Startup.Label.InvalidCode"));
    }

    @Test
    public void testJoinEventInvalidCode(){
        //The only valid code is "aaaaaa"
        String inviteCode = "aaaaab";
        sut.textBoxText = inviteCode;
        sut.joinEventClicked();
        assertEquals(1, testServerUtils.calls.size());
        assertFalse(sut.labelBindings.isEmpty());
        assertTrue(sut.labelBindings.contains("Startup.Label.InvalidCode"));
    }

    @Test
    public void testJoinEventValidCode(){
        String inviteCode = "aaaaaa";
        sut.textBoxText = inviteCode;
        sut.joinEventClicked();
        assertEquals(1, testServerUtils.calls.size());
        assertFalse(sut.joinEventCalls.isEmpty());
    }

    @Test
    public void testRemoveFromVboxNull(){
        sut.removeFromVBox(null);
    }

    @Test
    public void testRemoveFromHistoryIfExists(){
        HBox hBox = null;
        Event event = new Event();
        AbstractMap.SimpleEntry<Event, HBox> entry = new AbstractMap.SimpleEntry<>(event, hBox);
        sut.getEventsAndHBoxes().add(entry);
        assertFalse(sut.getEventsAndHBoxes().isEmpty());
        sut.removeFromHistoryIfExists(event);
        assertTrue(sut.getEventsAndHBoxes().isEmpty());
    }

    private class TestServerUtils extends ServerUtils{
        public List<String> calls = new LinkedList<>();
        public String validInvitationCode = "aaaaaa";
        @Override
        public Event getEvent(String inviteCode){
            calls.add("getEvent: " + inviteCode);
            //valid code
            if(inviteCode.equals(validInvitationCode)){
                return new Event();
            }
            //invalid code
            throw new jakarta.ws.rs.BadRequestException();
        }

        @Override
        public Event createEvent(String inviteCode){
            calls.add("createEvent: " + inviteCode);
            return new Event();
        }
    }

    private class TestMainController extends MainCtrl{
        public List<String> calls = new LinkedList<>();
        public Event lastEvent;
        @Override
        public void joinEvent(Event event){
            lastEvent = event;
            calls.add("join");
        }
    }

    private class TestStartupScreenCtrl extends StartupScreenCtrl{
        public String textBoxText;
        public List<String> joinEventCalls = new ArrayList<>();

        public List<String> labelBindings = new ArrayList<>();
        public List<String> textBoxBindings = new ArrayList<>();
        public List<String> buttonBindings = new ArrayList<>();
        public HashMap<HBox, Event> hBoxEventHashMap;
        public HashMap<Event, HBox> eventHBoxHashMap;
        /**
         * Constructor
         *
         * @param server      the ServerUtils instance
         * @param mainCtrl    the MainCtrl instance
         * @param translation the Translation to use
         */
        public TestStartupScreenCtrl(ServerUtils server, MainCtrl mainCtrl, Translation translation) {
            super(server, mainCtrl, translation);
        }

        @Override
        public String getTextBoxText(TextField textBox){
            return textBoxText;
        }

        @Override
        public void bindLabel(Label label,String str){
            labelBindings.add(str);
            return;
        }

        @Override
        public void bindTextBox(TextField textBox, String str){
            textBoxBindings.add(str);
            return;
        }

        @Override
        public void bindButton(Button button, String str){
            buttonBindings.add(str);
            return;
        }

        @Override
        public void joinEvent(Event event){
            joinEventCalls.add(event.toString());
        }

        @Override
        public List<Node> getHistoryNodes(){
            return new ArrayList<>();
        }
    }
}
