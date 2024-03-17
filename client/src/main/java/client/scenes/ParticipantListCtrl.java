package client.scenes;

import client.utils.ServerUtils;
import client.utils.Translation;
import com.google.inject.Inject;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

public class ParticipantListCtrl implements Initializable {
    ServerUtils server;
    MainCtrl mainCtrl;
    Translation translation;

    @Inject
    public ParticipantListCtrl(MainCtrl mainCtrl, ServerUtils server, Translation translation) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.translation = translation;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        textProperty().bind(translation.getStringBinding("participantList.goBack"));
    }
    public void goBack() {
    }
}
