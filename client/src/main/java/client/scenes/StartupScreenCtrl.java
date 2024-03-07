package client.scenes;

import client.utils.ServerUtils;
import client.utils.Translation;
import com.google.inject.Inject;
import commons.Event;
import jakarta.ws.rs.BadRequestException;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

import static javafx.geometry.Pos.CENTER_LEFT;
public class StartupScreenCtrl implements Initializable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    @FXML
    private TextField eventTitleTextBox;
    @FXML
    private TextField inviteCodeTextBox;
    @FXML
    private Label createEventFeedback;
    @FXML
    private Label joinEventFeedback;
    @FXML
    private VBox recentlyViewedEventsVBox;
    @FXML
    private Button joinEventButton;
    @FXML
    private Button createEventButton;
    @FXML
    private Label createEventLabel;
    @FXML
    private Label joinEventLabel;

    private List<AbstractMap.SimpleEntry<Event, HBox>> eventsAndHBoxes;
    private Translation translation;

    /**
     * Constructor
     * @param server the ServerUtils instance
     * @param mainCtrl the MainCtrl instance
     * @param translation the Translation to use
     */
    @Inject
    public StartupScreenCtrl(ServerUtils server, MainCtrl mainCtrl, Translation translation) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.translation = translation;
        eventsAndHBoxes = new ArrayList<>();
    }

    /**
     * Binds the fields to their matching binding
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bindTextBox(eventTitleTextBox, "Startup.TextBox.EventTitle");
        bindTextBox(inviteCodeTextBox, "Startup.TextBox.EventCode");
        bindLabel(joinEventLabel, "Startup.Label.JoinEvent");
        bindLabel(createEventLabel, "Startup.Label.CreateEvent");
        bindButton(joinEventButton, "Startup.Button.JoinEvent");
        bindButton(createEventButton, "Startup.Button.CreateEvent");
        bindLabel(joinEventFeedback, "empty");
        bindLabel(createEventFeedback, "empty");
    }

    /**
     * Creates and joins the event specified by the user in the text box
     */
    public void createEvent(){
        bindLabel(createEventFeedback, "empty");
        String title = getTextBoxText(eventTitleTextBox);
        if (title.isEmpty()){
            bindLabel(createEventFeedback, "Startup.Label.UnspecifiedTitle");
            return;
        }
        Event event = server.createEvent(title);
        System.out.println(event);
        joinEvent(event);
    }

    /**
     * Joins the event specified by the user in the text box
     */
    public void joinEventClicked(){
        bindLabel(joinEventFeedback, "empty");
        String inviteCode = getTextBoxText(inviteCodeTextBox);
        if (inviteCode.length() != 6){
            bindLabel(joinEventFeedback, "Startup.Label.InvalidCode");
            return;
        }
        try{
            Event event = server.getEvent(inviteCode);
            joinEvent(event);
        }catch (BadRequestException exception){
            bindLabel(joinEventFeedback, "Startup.Label.InvalidCode");
        }
    }
    /**
     * Joins the given event
     * @param event the event to join
     */
    public void joinEvent(Event event){
        mainCtrl.joinEvent(event);
        addToHistory(event);
    }

    /**
     * Adds the event to the history
     * @param event the event to add to the history
     */
    private void addToHistory(Event event) {
        Label eventLabel = generateLabelForEvent(event);
        try{
            ImageView imageView = generateRemoveButton(eventLabel);
            HBox hbox = generateHBox(eventLabel, imageView);
            removeFromHistoryIfExists(event);
            List<Node> recentlyViewedEvents = recentlyViewedEventsVBox.getChildren();
            recentlyViewedEvents.addFirst(hbox);
            eventsAndHBoxes.add(new AbstractMap.SimpleEntry<>(event, hbox));
            if (recentlyViewedEventsVBox.getChildren().size() > 5){
                HBox lastHBox = (HBox) recentlyViewedEvents.getLast();
                Event removedEvent = getEntryFromEventHbox(lastHBox).getKey();
                removeFromHistoryIfExists(removedEvent);
            }
        }catch (FileNotFoundException e){
            System.out.println("File was not found!");
        }
    }
    /**
     * Gets the entry from the eventHBoxHashMap
     * @param event the event to get the entry for
     * @return the entry
     */
    public AbstractMap.SimpleEntry<Event, HBox> getEntryFromEventHbox(Event event){
        for (AbstractMap.SimpleEntry<Event, HBox> entry : eventsAndHBoxes){
            if (entry.getKey().equals(event)){
                return entry;
            }
        }
        return null;
    }
    /**
     * Gets the entry from the eventHBoxHashMap
     * @param hBox the HBox to get the entry for
     * @return the entry
     */
    public AbstractMap.SimpleEntry<Event, HBox> getEntryFromEventHbox(HBox hBox){
        for (AbstractMap.SimpleEntry<Event, HBox> entry : eventsAndHBoxes){
            if (entry.getValue().equals(hBox)){
                return entry;
            }
        }
        return null;
    }

    /**
     * Getter for eventsAndHBoxes
     * @return eventsAndHBoxes
     */
    public List<AbstractMap.SimpleEntry<Event, HBox>> getEventsAndHBoxes() {
        return eventsAndHBoxes;
    }

    /**
     * Removes the HBox containing the event given if it exists already in the history
     * @param event the event to remove the history of
     */
    public void removeFromHistoryIfExists(Event event){
        AbstractMap.SimpleEntry<Event, HBox> entry = getEntryFromEventHbox(event);
        if (getEntryFromEventHbox(event) != null){
            HBox hBox = entry.getValue();
            eventsAndHBoxes.remove(entry);
            if (hBox != null){
                removeFromVBox(hBox.idProperty());
            }
        }
    }

    /**
     * Generates the label for the history
     * @param event the event to generate the label for
     * @return the label generated
     */
    public Label generateLabelForEvent(Event event){
        String eventTitle = event.getTitle();
        Label label = new Label();
        StringBuilder sb = new StringBuilder(eventTitle);
        sb.append(" (");
        sb.append(event.getId());
        sb.append(")");
        label.setText(sb.toString());

        label.setOnMouseClicked(
                mouseEvent -> {
                    joinEvent(event);
                    removeFromVBox(label.getParent().idProperty());
                    joinEventFeedback.textProperty().bind(translation.getStringBinding("empty"));
                });
        label.setOnMouseEntered(
                mouseEvent -> {
                    label.setUnderline(true);
                    mainCtrl.getMainMenuScene().setCursor(Cursor.HAND);
                }
        );
        label.setOnMouseExited(
                mouseEvent -> {
                    label.setUnderline(false);
                    mainCtrl.getMainMenuScene().setCursor(Cursor.DEFAULT);
                }
        );
        return label;
    }
    /**
     * Generates the HBox for the history (which will contain the label and image)
     * @param label the label to display
     * @param imageView the image to display
     * @return HBox generated
     */

    public HBox generateHBox(Label label, ImageView imageView){
        HBox hbox = new HBox(imageView, label);
        hbox.setSpacing(10);
        hbox.setAlignment(CENTER_LEFT);
        return hbox;
    }
    /**
     * Generates an image which when clicked will remove the HBox which contains it.
     * It will also change the cursor of the user while hovering to indicate clickability.
     * @param label the label which will be contained with the image in the HBox
     * @return ImageView with the image
     */
    public ImageView generateRemoveButton(Label label) throws FileNotFoundException {
        FileInputStream input = null;
        input = new FileInputStream("client/src/main/resources/images/x_remove.png");
        Image image = new Image(input);
        ImageView imageView = new ImageView(image);
        int imgSize = 15;
        imageView.setFitHeight(imgSize);
        imageView.setFitWidth(imgSize);
        imageView.setPickOnBounds(true);
        imageView.setOnMouseEntered(
                mouseEvent -> {
                    mainCtrl.getMainMenuScene().setCursor(Cursor.HAND);
                }
        );
        imageView.setOnMouseExited(
                mouseEvent -> {
                    mainCtrl.getMainMenuScene().setCursor(Cursor.DEFAULT);
                }
        );
        imageView.setOnMouseClicked(
                mouseEvent -> {
                    removeFromVBox(label.getParent().idProperty());
                }
        );
        return imageView;
    }
    /**
     * Removes from the VBox the child with the given id
     * @param id the given id
     */
    public void removeFromVBox(StringProperty id){
        List<Node> allNodes = getHistoryNodes();
        Node removeNode = null;
        for (Node node : allNodes){
            if (node != null && node.idProperty() != null && node.idProperty().equals(id)){
                removeNode = node;
            }
        }
        if (removeNode != null){
            allNodes.remove(removeNode);
        }
    }

    /**
     * Returns the history nodes that are in recentlyViewedEventsVBox
     * @return a list with the nodes
     */
    public List<Node> getHistoryNodes(){
        return recentlyViewedEventsVBox.getChildren();
    }

    /**
     * Gets the text from a given textfield
     * @param textBox the textfield to get the text from
     * @return String the text from the textfield
     */
    public String getTextBoxText(TextField textBox){
        return textBox.getText();
    }

    /**
     * Binds a label to a given string binding
     * @param label the label to bind
     * @param key the key for the translator
     */

    public void bindLabel(Label label, String key){
        label.textProperty().bind(translation.getStringBinding(key));
    }

    /**
     * Binds a textfield to a given string binding
     * @param textBox the textfield to bind
     * @param key the translator key
     */
    public void bindTextBox(TextField textBox, String key) {
        textBox.promptTextProperty().bind(translation.getStringBinding(key));
    }

    /**
     * Binds a button to a given string binding
     * @param button the button to bind
     * @param key the translator key
     */
    public void bindButton(Button button, String key) {
        button.textProperty().bind(translation.getStringBinding(key));
    }

    /**
     * Refreshes the events in the history
     */
    public void refreshEvents(){
        List<String> eventIds = new ArrayList<>();
        List<Map.Entry<Event, HBox>> entries = new ArrayList<>();
        for (Map.Entry<Event, HBox> entry : eventsAndHBoxes){
            eventIds.add(entry.getKey().getId());
            entries.add(entry);
        }
        for (Map.Entry<Event, HBox> entry : entries) {
            removeFromHistoryIfExists(entry.getKey());
        }
        eventsAndHBoxes.clear();
        recentlyViewedEventsVBox.getChildren().clear();
        for (String id : eventIds){
            Event event = server.getEvent(id);
            addToHistory(event);
        }
    }
}
