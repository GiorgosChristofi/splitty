package client.scenes;

import client.utils.ServerUtils;
import client.utils.Translation;
import com.google.inject.Inject;
import commons.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class EditTitleCtrl implements Initializable, SimpleRefreshable {
    @FXML
    private TextField title;
    @FXML
    private Label label;
    @FXML
    private Button confirm;
    @FXML
    private Button cancel;

    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final Translation translation;
    private Event event;

    @Inject
    public EditTitleCtrl(MainCtrl mainCtrl, ServerUtils server, Translation translation) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.translation = translation;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.label.textProperty().bind(translation.getStringBinding("editTitle.label"));
        this.title.promptTextProperty().bind(translation.getStringBinding("editTitle.promptText"));
        this.cancel.textProperty().bind(translation.getStringBinding("editTitle.cancelButton"));
        this.confirm.textProperty().bind(translation.getStringBinding("editTitle.confirmButton"));
    }

    public void refresh(Event event) {
        this.event = event;
    }

    public void confirm() {
        server.editTitle(event.getId(), title.getText());
        title.clear();
        mainCtrl.switchToEventScreen();
    }

    public void cancel() {
        title.clear();
        mainCtrl.switchToEventScreen();
    }

    /***
     * Specifies if the screen should be live-refreshed
     * @return true if changes should immediately refresh the screen, false otherwise
     */
    @Override
    public boolean shouldLiveRefresh() {
        return false;
    }
}
