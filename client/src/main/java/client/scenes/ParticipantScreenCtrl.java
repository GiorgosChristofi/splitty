package client.scenes;

import client.utils.ServerUtils;
import client.utils.Translation;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParticipantScreenCtrl implements Initializable, SimpleRefreshable {
    private static String bicLike= "^[A-Z]{6}[A-Z0-9]{2}([A-Z0-9]{3})?$";
    private static String ibanLike = "^([A-Z]{2}[0-9]{2})(?:[ ]?([0-9]{4})){4}$";
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final Translation translation;
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
    private static final Pattern bicPattern = Pattern.compile(bicLike);
    private static final Pattern ibanPattern = Pattern.compile(ibanLike);
    private Event event;
    private long participantId;

    /**
     * constructor
     * @param server -> the server
     * @param mainCtrl main controller
     * @param translation for translating buttons and fields
     */
    @Inject
    public ParticipantScreenCtrl(ServerUtils server, MainCtrl mainCtrl, Translation translation) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.translation = translation;
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
        resetErrorFields();
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
            System.out.println(participantId);
            if(participantId!=0) {
                Participant participant = findById(participantId);
                if (participant != null && participant.getName().equals(newValue))
                    noName.textProperty().bind(translation.getStringBinding("empty"));
                else {
                    if (checkParticipantName(newValue)) {
                        noName.textProperty().bind(translation.getStringBinding("Participants.Label.sameName"));
                        noName.setStyle("-fx-fill: #ff7200;");
                    }
                }
            }
            else if (checkParticipantName(newValue)) {
                noName.textProperty().bind(translation.getStringBinding("Participants.Label.sameName"));
                noName.setStyle("-fx-fill: #ff7200;");
            }
        });
        bindFieldsToEnter();
    }

    /**
     * Binds the nameField, emailField, bicField, ibanField and holderField to the Enter key
     */

    private void bindFieldsToEnter() {
        nameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                if(nameField.getText()==null || nameField.getText().isEmpty())
                    confirmEdit();
                else emailField.requestFocus();
            }
        });
        emailField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confirmEdit();
            }
        });
        bicField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confirmEdit();
            }
        });
        ibanField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confirmEdit();
            }
        });
        holderField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                confirmEdit();
            }
        });
    }

    /**
     * method for clicking 'ok' in add/edit participant screen
     * if the participant is new it is added to the event
     * otherwise edits the participant
     * A few checks are made regarding the values inserted by the user
     */
    public void confirmEdit() {
        Participant participant = addParticipant();
        resetErrorFields();
        boolean ok = true;
        if(participant.getName() == null || participant.getName().isEmpty()) {
            noName.textProperty()
                    .bind(translation.getStringBinding("Participants.Label.noName"));
            ok = false;
        }
        if(participant.getEmail() != null &&participant.getEmail().equals("empty")){
            noEmail.textProperty()
                    .bind(translation.getStringBinding("Participants.Label.noEmail"));
            ok = false;
        }
        else{
            if(participant.getEmail() != null && participant.getEmail().equals("wrongEmail")){
                noEmail.textProperty()
                      .bind(translation.getStringBinding("Participants.Label.wrongEmail"));
                ok = false;
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
                participantId = 0;
                mainCtrl.switchScreens(ParticipantListScreenCtrl.class);
            }
            clearFields();
        }
    }

    /**
     * takes you back to the event screen if 'abort' is clicked
     */
    public void cancel() {
        clearFields();
        mainCtrl.switchScreens(EventScreenCtrl.class);
    }

    /**
     * resets the errors for the wrong values
     */
    public void resetErrorFields(){
        noName.textProperty().bind(translation.getStringBinding("empty"));
        noEmail.textProperty().bind(translation.getStringBinding("empty"));
        wrongBic.textProperty().bind(translation.getStringBinding("empty"));
        wrongIban.textProperty().bind(translation.getStringBinding("empty"));
    }

    /***
     * Removes text from all details fields
     */
    public void clearFields(){
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
    public Participant addParticipant(){
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
                System.out.println("wrongIban");
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
                System.out.println("wrongBic");
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
    public void setParticipant(long id) {
        Set<Participant> participantList = event.getParticipants();
        Participant participantFin = null;
        for(Participant participant: participantList){
            if(participant.getId() == id) {
                participantFin = participant;
                break;
            }
        }
        if(participantFin == null) return;
        nameField.setText(participantFin.getName());
        holderField.setText(participantFin.getLegalName());
        ibanField.setText(participantFin.getIban());
        bicField.setText(participantFin.getBic());
        emailField.setText(participantFin.getEmail());
        participantId = id;
    }

    /**
     * updates the event instance
     * @param event -> new details
     */
    public void refresh(Event event){
        this.event = event;
        resetErrorFields();
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
    public boolean checkParticipantName(String name){
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
    public Participant findById(long id){
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
