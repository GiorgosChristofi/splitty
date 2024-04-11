package client.scenes;

import client.utils.*;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class EmailInviteCtrl implements Initializable, SimpleRefreshable {
    @FXML
    private Label titleLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private TextField nameTextField;
    @FXML
    private Label emailLabel;
    @FXML
    private TextField emailTextField;
    @FXML
    private Button inviteButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Label nameFeedbackLabel;
    @FXML
    private Label emailFeedbackLabel;
    private Translation translation;
    private ServerUtils server;
    private MainCtrl mainCtrl;
    private EmailHandler emailHandler;
    private Event event;
    private ConfigUtils configUtils;

    /**
     * Constructor
     * @param translation the translation to use
     * @param server the server to use
     * @param mainCtrl the main controller
     * @param emailHandler the email handler to use
     * @param configUtils the configUtils
     */
    @Inject
    public EmailInviteCtrl(Translation translation, ServerUtils server, MainCtrl mainCtrl, EmailHandler emailHandler, ConfigUtils configUtils) {
        this.translation = translation;
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.emailHandler = emailHandler;
        this.configUtils = configUtils;
    }

    /**
     * Initialize basic features for the Email Invite Screen
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
        titleLabel.textProperty().bind(translation.getStringBinding("Email.TitleLabel"));
        nameLabel.textProperty().bind(translation.getStringBinding("Email.NameLabel"));
        nameTextField.promptTextProperty().bind(translation.getStringBinding("Email.NameTextField"));
        emailTextField.promptTextProperty().bind(translation.getStringBinding("Email.EmailTextField"));
        nameFeedbackLabel.textProperty().bind(translation.getStringBinding("Empty"));
        emailFeedbackLabel.textProperty().bind(translation.getStringBinding("Empty"));
        cancelButton.textProperty().bind(translation.getStringBinding("Email.CancelButton"));
        inviteButton.textProperty().bind(translation.getStringBinding("Email.InviteButton"));
    }

    /**
     *Sends an invitation to the email address given in the email text field
     */
    public void sendInvite() {
        String name = nameTextField.getText();
        String email = emailTextField.getText();
        Participant participant = new Participant(name);
        participant.setEmail(email);
        if (name.isEmpty()) {
            nameFeedbackLabel.textProperty().bind(translation.getStringBinding("Email.NameFeedbackLabel"));
        } else {
            nameFeedbackLabel.textProperty().bind(translation.getStringBinding("Empty"));
        }
        if (email.isEmpty()) {
            emailFeedbackLabel.textProperty().bind(translation.getStringBinding("Email.EmailFeedbackLabel"));
        } else {
            emailFeedbackLabel.textProperty().bind(translation.getStringBinding("Empty"));
        }
        if (!name.isEmpty() && !email.isEmpty()) {
            Thread emailThread = setupEmailThread(email, participant);
            emailThread.start();
            clearFields();
            mainCtrl.switchScreens(EventScreenCtrl.class);
        }
    }

    /**
     * Sets up a thread to send an email so the whole app doesn't freeze while the email is sent
     * @param email the email to send the invitation to
     * @param participant the participant to add to the event
     * @return the thread that sends the email
     */
    private Thread setupEmailThread(String email, Participant participant) {
        return new Thread(() -> {
            boolean result = emailHandler.sendEmail(email, "Invited to splitty!", emailHandler.getInviteText(event));
            if (result){
                server.addParticipant(event.getId(), participant);
                Platform.runLater(() -> {
                    System.out.println("Successfully sent email!");
                    Alert a = new Alert(Alert.AlertType.INFORMATION);
                    a.contentTextProperty().bind(translation.getStringBinding("Event.Label.EmailFeedback.Success"));
                    a.titleProperty().bind(translation.getStringBinding("Email.SuccessTitle"));
                    a.headerTextProperty().bind(translation.getStringBinding("Email.EmailFeedback"));
                    a.show();
                });
            }else{
                Platform.runLater(() -> {
                    System.out.println("Error while sending email!");
                    Alert a = new Alert(Alert.AlertType.ERROR);
                    a.contentTextProperty().bind(translation.getStringBinding("Event.Label.EmailFeedback.Fail"));
                    a.titleProperty().bind(translation.getStringBinding("Email.ErrorTitle"));
                    a.headerTextProperty().bind(translation.getStringBinding("Email.EmailFeedback"));
                    a.show();
                });
            }
        });
    }

    /**
     * Cancels the invitation and switches back to the event screen
     */
    public void cancel(){
        mainCtrl.switchScreens(EventScreenCtrl.class);
    }

    /**
     * Clears the text fields
     */
    public void clearFields(){
        nameTextField.clear();
        emailTextField.clear();
    }

    /**
     * Refreshes the data of the EmailInviteCtrl
     * @param event the new Event data to process
     */
    @Override
    public void refresh(Event event) {
        this.event = event;
    }

}
