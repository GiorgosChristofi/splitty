package client.scenes;

import client.utils.ImageUtils;
import client.utils.ServerUtils;
import client.utils.Styling;
import client.utils.Translation;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParticipantScreenCtrl implements Initializable, SimpleRefreshable {
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final Styling styling;
    private final Translation translation;
    private final ImageUtils imageUtils;
    @FXML
    private Button cancelButton;
    @FXML
    private Label title;
    @FXML
    private Label name;
    @FXML
    private Label noName;
    @FXML
    private Label wrongIban;
    @FXML
    private Label wrongBic;
    @FXML
    private Label noEmail;
    @FXML
    private Label optional;
    @FXML
    private TextField nameField;
    @FXML
    private Label email;
    @FXML
    private TextField emailField;
    @FXML
    private Label iban;
    @FXML
    private TextField ibanField;
    @FXML
    private Label bic;
    @FXML
    private TextField bicField;
    @FXML
    private Button okButton;
    @FXML
    private Label bankDetails;
    @FXML
    private Label holder;
    @FXML
    private TextField holderField;
    private Event event;
    private long participantId;

    /**
     * constructor
     * @param server -> the server
     * @param mainCtrl main controller
     * @param translation for translating buttons and fields
     */
    @Inject
    public ParticipantScreenCtrl(ServerUtils server, MainCtrl mainCtrl, Translation translation, ImageUtils imageUtils, Styling styling) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.translation = translation;
        this.imageUtils = imageUtils;
        this.styling = styling;
    }

    /***
     * binder for the buttons, fields, labels
     * @param location
     * The location used to resolve relative paths for the root object, or
     * {@code null} if the location is not known.
     * Adds response to pressing the Enter key, as well as a few usability features
     * @param resources
     * The resources used to localize the root object, or {@code null} if
     * the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        title.textProperty().bind(translation.getStringBinding("Participants.Label.title"));
        cancelButton.textProperty().bind(translation.getStringBinding("Participants.Button.cancel"));
        okButton.textProperty().bind(translation.getStringBinding("Participants.Button.ok"));
        bankDetails.textProperty().bind(translation.getStringBinding("Participants.Label.bankDetails"));
        emailField.promptTextProperty().bind(translation.getStringBinding("Participants.Field.email"));
        holderField.promptTextProperty().bind(translation.getStringBinding("Participants.Field.holder"));
        ibanField.promptTextProperty().bind(translation.getStringBinding("Participants.Field.iban"));
        nameField.promptTextProperty().bind(translation.getStringBinding("Participants.Field.name"));
        bicField.promptTextProperty().bind(translation.getStringBinding("Participants.Field.bic"));
        name.textProperty().bind(translation.getStringBinding("Participants.Label.name"));
        email.textProperty().bind(translation.getStringBinding("Participants.Label.email"));
        holder.textProperty().bind(translation.getStringBinding("Participants.Label.holder"));
        iban.textProperty().bind(translation.getStringBinding("Participants.Label.iban"));
        bic.textProperty().bind(translation.getStringBinding("Participants.Label.bic"));
        optional.textProperty().bind(translation.getStringBinding("Participants.Label.optional"));
        resetErrorFields(translation, noName, noEmail, wrongBic, wrongIban);
        ibanField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (checkIban(newValue)) {
                ibanField.setStyle("-fx-border-color: #26c506;");
                wrongIban.textProperty().bind(translation.getStringBinding("empty"));
            } else {
                ibanField.setStyle("-fx-border-color: red;");
                wrongIban.textProperty().bind(translation.getStringBinding("empty"));
            }
            if(newValue == null || newValue.isEmpty()) {
                ibanField.setStyle("");
                wrongIban.textProperty().bind(translation.getStringBinding("empty"));
            }
        });
        bicField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (checkBic(newValue)) {
                wrongBic.textProperty().bind(translation.getStringBinding("empty"));
                bicField.setStyle("-fx-border-color: #26c506;");
            } else {
                bicField.setStyle("-fx-border-color: red;");
                wrongBic.textProperty().bind(translation.getStringBinding("empty"));
            }
            if(newValue == null || newValue.isEmpty()) {
                bicField.setStyle("");
                wrongBic.textProperty().bind(translation.getStringBinding("empty"));
            }
        });
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                noEmail.textProperty().bind(translation.getStringBinding("empty"));
            }
        });
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                noName.textProperty().bind(translation.getStringBinding("empty"));
            }
        });
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if(participantId!=0) {
                Participant participant = findById(participantId, event);
                if (participant != null && participant.getName().equals(newValue))
                    noName.textProperty().bind(translation.getStringBinding("empty"));
                else {
                    if (checkParticipantName(newValue, event)) {
                        noName.textProperty().bind(translation.getStringBinding("Participants.Label.sameName"));
                        styling.changeStyling(noName, "errorText", "warningLabel");
                    }
                }
            }
            else if (checkParticipantName(newValue, event)) {
                noName.textProperty().bind(translation.getStringBinding("Participants.Label.sameName"));
                styling.changeStyling(noName, "errorText", "warningLabel");
            }
        });
        nameField.setOnKeyPressed(event -> bindNameField(event, emailField));
        holderField.setOnKeyPressed(event -> bindOtherFields(event, holderField));
        emailField.setOnKeyPressed(event -> bindOtherFields(event, emailField));
        ibanField.setOnKeyPressed(event -> bindOtherFields(event, ibanField));
        bicField.setOnKeyPressed(event -> bindOtherFields(event, bicField));
    }

    public void bindNameField(KeyEvent event, TextField textField){
        if (event.getCode() == KeyCode.ENTER) {
            if(textField.getText()==null || textField.getText().isEmpty())
                confirmCreateEdit();
            else textField.requestFocus();
        }
    }

    public void bindOtherFields(KeyEvent event, TextField textField){
        if (event.getCode() == KeyCode.ENTER)
            if(textField.getText()==null || textField.getText().isEmpty())
                confirmCreateEdit();
    }

    public void confirmCreateEdit(){
        resetErrorFields(translation, noName, noEmail, wrongBic, wrongIban);
        Boolean ok = true;
        Participant participant = addParticipant(nameField, emailField, holderField, bicField, ibanField);
        confirmEdit(styling, ok, participant, noName, noEmail, wrongBic, wrongIban, translation, server, mainCtrl, event, participantId, nameField, holderField, emailField, ibanField, bicField);
    }
    /**
     * method for clicking 'ok' in add/edit participant screen
     * if the participant is new it is added to the event
     * otherwise edits the participant
     * A few checks are made regarding the values inserted by the user
     */
    public void confirmEdit(Styling styling, Boolean ok, Participant participant, Label noName, Label noEmail, Label wrongBic, Label wrongIban, Translation translation, ServerUtils server, MainCtrl mainCtrl, Event event, long participantId, TextField nameField, TextField holderField, TextField emailField, TextField ibanField, TextField bicField) {
        ok = true;
        if(participant.getName() == null || participant.getName().isEmpty()) {
            noName.textProperty()
                    .bind(translation.getStringBinding("Participants.Label.noName"));
            styling.changeStyling(noName, "warningLabel", "errorText");
            ok = false;
        }
        else{
            if(participant.getEmail() != null && participant.getEmail().equals("wrongEmail")){
                noEmail.textProperty()
                      .bind(translation.getStringBinding("Participants.Label.wrongEmail"));
                ok = false;
                System.out.println("Email format is not correct");
            } else if (participant.getEmail().equals("empty")) {
                participant.setEmail(null);
            }
        }
        if(participant.getIban() != null && participant.getIban().equals("wrongIban")) {
            wrongIban.textProperty().bind(translation.getStringBinding("Participants.Label.wrongIban"));
            ok = false;
        }
        if(participant.getBic() != null &&  participant.getBic().equals("wrongBic")){
            wrongBic.textProperty().bind(translation.getStringBinding("Participants.Label.wrongBic"));
            ok = false;
        }
        if(ok){
            if(participantId == 0){
                server.addParticipant(event.getId(), participant);
                mainCtrl.switchScreens(EventScreenCtrl.class);
            }
            else {
                server.editParticipant(event.getId(), participantId, participant);
                mainCtrl.switchScreens(ParticipantListScreenCtrl.class);
            }
            clearFields(nameField, holderField, ibanField, bicField, emailField);
        }
    }

    /**
     * takes you back to the event screen if 'abort' is clicked
     */
    public void cancel() {
        clearFields(nameField, holderField, ibanField, bicField, emailField);
        saveId(0L);
        mainCtrl.switchScreens(EventScreenCtrl.class);
    }

    /**
     * resets the errors for the wrong values
     */
    public void resetErrorFields(Translation translation, Label noName, Label noEmail, Label wrongBic, Label wrongIban){
        noName.textProperty().bind(translation.getStringBinding("empty"));
        noEmail.textProperty().bind(translation.getStringBinding("empty"));
        wrongBic.textProperty().bind(translation.getStringBinding("empty"));
        wrongIban.textProperty().bind(translation.getStringBinding("empty"));
    }

    /***
     * Removes text from all details fields
     */
    public void clearFields(TextField nameField, TextField holderField, TextField ibanField, TextField bicField, TextField emailField){
        nameField.clear();
        holderField.clear();
        ibanField.clear();
        bicField.clear();
        emailField.clear();
    }

    /**
     * creates an instance of a participant which is being assigned/updated
     * in the confirm method
     * Checks for the correctness of certain values
     * @return returns the new participant
     */
    public Participant addParticipant(TextField nameField, TextField emailField, TextField holderField, TextField bicField, TextField ibanField){
        String name = nameField.getText();
        Participant participant = new Participant(name);
        String email = emailField.getText();
        if (checkEmail(email)){
            participant.setEmail(email);
        }
        else{
            if(emailField.getText() == null || emailField.getText().isEmpty())
                participant.setEmail("empty");
            else
                participant.setEmail("wrongEmail");
        }
        String accountHolder = holderField.getText();
        String iban = ibanField.getText();
        if(iban == null || iban.isEmpty()){
            participant.setIban(iban);
        }
        else{
            if(checkIban(iban))
                participant.setIban(iban);
            else{
                participant.setIban("wrongIban");
                System.out.println("IBAN format is not correct");
            }
        }
        String bic = bicField.getText();
        if(bic == null || bic.isEmpty()){
            participant.setBic(bic);
        }
        else{
            if(checkBic(bic))
                participant.setBic(bic);
            else{
                participant.setBic("wrongBic");
                System.out.println("BIC format is not correct");
            }
        }
        participant.setLegalName(accountHolder);
        return participant;
    }

    /**
     * sets up the screen when editing a participant
     * -> selects details of the participant in the current state
     * @param id -> id of the participant
     */
    public void setParticipant(long id, Event event, TextField nameField, TextField holderField, TextField ibanField, TextField bicField, TextField emailField) {
        Set<Participant> participantList = event.getParticipants();
        Participant participantFin = null;
        for(Participant participant: participantList)
            if(participant.getId() == id) {
                participantFin = participant;
                break;
            }
        if(participantFin == null) return;
        nameField.setText(participantFin.getName());
        holderField.setText(participantFin.getLegalName());
        ibanField.setText(participantFin.getIban());
        bicField.setText(participantFin.getBic());
        emailField.setText(participantFin.getEmail());
    }

    public void callSetParticipant(long id){
        setParticipant(id, event, nameField, holderField, ibanField, bicField, emailField);
    }

    /**
     * updates the event instance
     * @param event -> new details
     */
    public void refresh(Event event){
        this.event = event;
        resetErrorFields(translation, noName, noEmail, wrongBic, wrongIban);
    }

    /**
     * checks if the inserted email has an appropriate pattern
     * @param email the value inserted by the user
     * @return true if the value is correct, false otherwise
     */
    public boolean checkEmail (String email){
        if (email == null || email.isEmpty()){
            return false;
        }
        String emailLike = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern emailPattern = Pattern.compile(emailLike);
        Matcher matcher = emailPattern.matcher(email);
        if (matcher.matches()) {
            return true;
        } else {
            System.out.println("Invalid Email");
        }
        return false;
    }

    /**
     * checks if the inserted bic has an appropriate pattern
     * @param bic the value inserted by the user
     * @return true if the value is correct, false otherwise
     */
    public boolean checkBic (String bic){
        String bicLike= "^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$";
        Pattern bicPattern = Pattern.compile(bicLike);
        if (bic == null || bic.isEmpty()){
            return false;
        }
        Matcher matcher = bicPattern.matcher(bic);
        return matcher.matches();
    }

    /**
     * checks if the inserted iban has an appropriate pattern
     * @param iban the value inserted by the user
     * @return true if the value is correct, false otherwise
     */
    public boolean checkIban (String iban) {
        String ibanLike = "^([A-Z]{2}[0-9]{2})(?:[ ]?([0-9]{4})){4}$";
        Pattern ibanPattern = Pattern.compile(ibanLike);
        if (iban == null || iban.isEmpty()){
            return false;
        }
        Matcher matcher = ibanPattern.matcher(iban);
        return matcher.matches();
    }

    /**
     * checks if the participant name is already in the list of participants
     * @param name the value inserted by the user
     * @return true if the value is correct, false otherwise
     */
    public boolean checkParticipantName(String name, Event event){
        Set<Participant> participantList = event.getParticipants();
        for(Participant participant: participantList)
            if(participant.getName().equals(name)) {
                return true;
            }
        return false;
    }

    /**
     * Setter for participantId
     * @param id id of the participant
     */
    public void saveId(Long id){
        participantId = id;
    }

    /**
     * Gets a participant based on id
     * @param id id of the participant
     * @return the participant
     */
    public Participant findById(long id, Event event){
        Set<Participant> participantList = event.getParticipants();
        Participant participantF = null;
        for(Participant participant: participantList)
            if(participant.getId() == id) {
                participantF = participant;
                break;
            }
        return participantF;
    }

}
