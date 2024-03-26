package client.scenes;

import client.utils.ServerUtils;
import client.utils.Translation;
import com.google.inject.Inject;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

public class ManagementOverviewPasswordCtrl implements Initializable {
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label inputPasswordLabel;
    @FXML
    private Button logInButton;
    @FXML
    private Label logInFeedback;
    @FXML
    private Button goBackToMainScreen;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final Translation translation;
    /**
     * Constructor
     * @param server the ServerUtils instance
     * @param mainCtrl the MainCtrl instance
     * @param translation the Translation to use
     */
    @Inject
    public ManagementOverviewPasswordCtrl(ServerUtils server, MainCtrl mainCtrl, Translation translation) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.translation = translation;
    }
    /**
     * Initialize basic features for the Management Overview Screen Password (log in) screen
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
        passwordField.promptTextProperty().bind(translation.getStringBinding("MOPCtrl.Password.Field"));
        inputPasswordLabel.textProperty().bind(translation.getStringBinding("MOPCtrl.Input.Password.Label"));
        bindLabel(inputPasswordLabel, "MOPCtrl.Input.Password.Label");
        logInButton.textProperty().bind(translation.getStringBinding("MOPCtrl.Log.In.Button"));
    }

    /**
     * check if the password in the passwordField matches with the one randomly generated
     * @param actionEvent on button press
     */
    public void logInCheck(ActionEvent actionEvent) {
        String inputPassword = getPasswordFieldText(passwordField);
        if(inputPassword == null || inputPassword.isEmpty() || !server.checkPassword(inputPassword)){
            bindLabel(logInFeedback, "MOPCtrl.Log.In.Feedback");
            clearPasswordField();
        }
        else{
            clearPasswordField();
            mainCtrl.switchToManagementOverviewScreen();
        }
    }

    /**
     * go back to the main menu
     * @param actionEvent on button press
     */
    public void goBackToMain(ActionEvent actionEvent) {
        mainCtrl.switchBackToMainScreen();
    }

    /**
     * Gets the text from the passwordField
     * @param passwordField the passwordField
     * @return the text from the passwordField
     */
    public String getPasswordFieldText(PasswordField passwordField){
        return passwordField.getText();
    }

    /**
     * Binds a label to a string
     * @param label the label to bind to
     * @param str the binding's name
     */
    public void bindLabel(Label label, String str){
        label.textProperty().bind(translation.getStringBinding(str));
    }

    /**
     * Clears the passwordField
     */
    public void clearPasswordField(){
        passwordField.clear();
    }
}
