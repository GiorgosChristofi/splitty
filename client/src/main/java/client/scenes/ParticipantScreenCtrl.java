package client.scenes;

import client.utils.ServerUtils;
import client.utils.Translation;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class ParticipantScreenCtrl {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final Translation translation;
    @FXML
    public Button abortButton;
    @FXML
    public Label title;
    @FXML
    public Label name;
    @FXML
    public TextField nameField;
    @FXML
    public Label email;
    @FXML
    public TextField emailField;
    @FXML
    public Label iban;
    @FXML
    public TextField ibanField;
    @FXML
    public Label bic;
    @FXML
    public TextField bicField;
    @FXML
    public Button okButton;

    private Event event;

    private Participant participant;


    @Inject
    public ParticipantScreenCtrl(ServerUtils server, MainCtrl mainCtrl, Translation translation) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.translation = translation;
    }

    public void initialize(URL location, ResourceBundle resources) {
        abortButton.textProperty().bind(translation.getStringBinding("Participants.Button.abort"));
        okButton.textProperty().bind(translation.getStringBinding("Participants.Button.ok"));
        emailField.promptTextProperty().bind(translation.getStringBinding("Participants.Field.email"));
        ibanField.promptTextProperty().bind(translation.getStringBinding("Participants.Field.iban"));
        nameField.promptTextProperty().bind(translation.getStringBinding("Participants.Field.name"));
        bicField.promptTextProperty().bind(translation.getStringBinding("Participants.Field.bic"));
        name.textProperty().bind(translation.getStringBinding("Participants.Label.name"));
        email.textProperty().bind(translation.getStringBinding("Participants.Label.email"));
        iban.textProperty().bind(translation.getStringBinding("Participants.Label.iban"));
        bic.textProperty().bind(translation.getStringBinding("Participants.Label.bic"));
    }

    public void confirmEdit(ActionEvent actionEvent) {
        Participant participant = addParticipant();
        server.addParticipant(event.getId(), participant.getName());
        event.addParticipant(participant);
        mainCtrl.joinEvent(event);
    }

    public void cancel(ActionEvent actionEvent) {
        mainCtrl.joinEvent(event);
    }

    public Participant addParticipant(){
        String name = nameField.getText();
        String email = emailField.getText();
        String iban = ibanField.getText();
        try {
            int bic = Integer.parseInt(String.valueOf(bicField));
        }
        catch (IllegalArgumentException e) {
            System.out.println(":<");
        }
        Participant participant = new Participant(name, event);
        //remember email iban bic when available
        return participant;
    }

    public void editParticipant(Participant participant){
        String name = nameField.getText();
        String email = emailField.getText();
        String iban = ibanField.getText();
        try {
            int bic = Integer.parseInt(String.valueOf(bicField));
        }
        catch (IllegalArgumentException e) {
            System.out.println(":<");
        }
        participant.setName(name);
    }

    public void setEvent (Event event){
        this.event = event;
    }

    public void setParticipant(Participant participant){
        this.participant = participant;
    }

    public Event getEvent() {
        return this.event;
    }

    public Participant getParticipant() {
        return this.participant;
    }
}
