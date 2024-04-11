package client.scenes;
import client.utils.ImageUtils;
import client.utils.ServerUtils;
import client.utils.Translation;
import com.google.inject.Inject;
import commons.Event;
import client.utils.Styling;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.*;

import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

import static javafx.geometry.Pos.CENTER_LEFT;

public class ParticipantListScreenCtrl implements Initializable, SimpleRefreshable {
    private Event event;
    @FXML
    private Button goBack;
    @FXML
    private ListView<HBox> participantList;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final Translation translation;
    private final ImageUtils imageUtils;
    private Map<Long, HBox> map;
    private Styling styling;

    /**
     * Constructor for the participant list screen
     * @param mainCtrl the MainCtrl instance to use
     * @param server the ServerUtils instance to use
     * @param translation the Translation instance to use
     * @param imageUtils the ImageUtils instance to use
     * @param styling the Styling instance to use
     */
    @Inject
    public ParticipantListScreenCtrl(MainCtrl mainCtrl, ServerUtils server,
                                     Translation translation, ImageUtils imageUtils,
                                     Styling styling) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.translation = translation;
        this.imageUtils = imageUtils;
        this.map = new HashMap<>();
        this.styling = styling;
    }

    /**
     * Updates the event instance
     * @param event updated Event information
     */
    public void refresh(Event event) {
        this.event = event;
        showParticipantList();
    }

    /***
     *
     * @param location
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ImageView goBackImage = imageUtils.generateImageView("goBack.png", 15);
        goBack.setGraphic(goBackImage);
        styling.applyStyling(goBack, "positiveButton");
    }

    /**
     * Generates the participants in the list in the final form
     */
    public void showParticipantList () {
        participantList.getItems().clear();
        Image removeImage = imageUtils.loadImageFile("x_remove.png");
        for(Participant participant: event.getParticipants()) {
            HBox participantB1 = generateParticipantBox(participant.getId(), participant.getName());
            Region region = new Region();
            HBox.setHgrow(region, Priority.ALWAYS);
            HBox participantBox = new HBox(generateRemoveButton(participant.getId(), removeImage), participantB1, region);
            participantBox.setAlignment(Pos.CENTER_RIGHT);
            participantBox.setStyle("-fx-border-style: none;");
            participantBox.setSpacing(10);
            map.put(participant.getId(), participantBox);
            participantBox.setAlignment(CENTER_LEFT);
            participantList.getItems().add(participantBox);
        }
    }

    /**
     * generates the remove button for each participant
     * @param participantId the participant for which the remove button is being generated
     * @param removeImage the Image to place on the button
     * @return returns the symbol
     */
    public ImageView generateRemoveButton(long participantId, Image removeImage) {
        ImageView imageView = imageUtils.generateImageView(removeImage, 15);
        imageView.setPickOnBounds(true);
        imageView.setOnMouseEntered(mouseEvent -> mainCtrl.getParticipantScene().
                setCursor(Cursor.HAND));
        imageView.setOnMouseExited(mouseEvent -> mainCtrl.getParticipantScene().
                setCursor(Cursor.DEFAULT));
        imageView.setOnMouseClicked(mouseEvent -> removeFromList(participantId));
        return imageView;
    }

    /**
     * Generates HBox for each participant
     * clicking the participant label leads to the add/edit screen of the participant
     * @param participantId id of the participant
     * @param name name of the participant
     * @return an HBox containing a label for a participant, and a remove button
     */
    public HBox generateParticipantBox(long participantId, String name) {
        HBox hbox = new HBox();
        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);
        Label participant = new Label(name);
        hbox.getChildren().addAll(participant, region);
        hbox.setOnMouseEntered(mouseEvent -> mainCtrl.getParticipantScene()
                .setCursor(Cursor.HAND));
        hbox.setOnMouseExited(mouseEvent -> mainCtrl.getParticipantScene()
                .setCursor(Cursor.DEFAULT));
        hbox.setOnMouseClicked(mouseEvent -> mainCtrl.switchToEditParticipant(participantId));
        return hbox;
    }

    /**
     * go back method when the button is clicked
     */
    public void goBack() {
        mainCtrl.switchScreens(EventScreenCtrl.class);
    }

    /**
     * Removes the participant from the list if deleted
     * @param participantId the ID of the participant
     */
    public void removeFromList(long participantId){
        server.removeParticipant(event.getId(), participantId);
        HBox hBox = map.get(participantId);
        map.remove(participantId);
        participantList.getItems().remove(hBox);
    }

}


