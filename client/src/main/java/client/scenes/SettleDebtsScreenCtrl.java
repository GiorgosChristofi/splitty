package client.scenes;

import client.utils.SettleDebtsUtils;
import client.utils.Styling;
import client.utils.Translation;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Pair;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static javafx.geometry.Pos.CENTER_LEFT;

public class SettleDebtsScreenCtrl implements Initializable, SimpleRefreshable {
    private final MainCtrl mainCtrl;
    private final Translation translation;
    private final SettleDebtsUtils utils;
    @FXML
    private Label settleDebtsLabel;
    @FXML
    private Button goBackButton;
    @FXML
    private VBox settleVBox;
    private Event event;
    private Pair<Pane, Button> lastExpanded;

    /***
     * Constructor for the SettleDebtsScreen
     * @param mainCtrl the Main controller to use
     * @param translation the translation to use
     * @param utils the server utilities to use
     */
    @Inject
    public SettleDebtsScreenCtrl(MainCtrl mainCtrl,
                                 Translation translation, SettleDebtsUtils utils) {
        this.mainCtrl = mainCtrl;
        this.translation = translation;
        this.utils = utils;
        this.event = null;
        this.lastExpanded = null;
    }

    /***
     * Initializes the controller
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
        settleDebtsLabel.textProperty()
                .bind(translation.getStringBinding("SettleDebts.Label.title"));
        try{
            Image image = new Image(
                    new FileInputStream("client/src/main/resources/images/goBack.png"));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(15);
            imageView.setFitHeight(15);
            imageView.setPreserveRatio(true);
            goBackButton.setGraphic(imageView);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /***
     * Populates the VBox with all the debts
     */
    private void populateVBox(){
        List<Node> children = settleVBox.getChildren();
        children.clear();
        var owedShares = event.getOwedShares();
        for(Map.Entry<Participant, Integer> entry: owedShares.entrySet()){
            Participant participantOwes = entry.getKey();
            int amount = entry.getValue();

            /*
            In the current implementation, as per the basic requirements,
            there are no "debts" to anyone, only amounts owed to the group.
            This is added for extensibility later, as are all the methods that use it,
            so that transfer instructions (A sends X to B) can be generated easily
             */
            Participant participantOwedTo = participantOwes; // requisite feature not implemented

            Label debtLabel = generateDebtLabel(participantOwes, amount, participantOwedTo);
            Pane bankDetailsPane = generateBankDetailsPane(participantOwedTo);
            Button expandButton = generateExpandButton(bankDetailsPane);
            Button settleButton = generateSettleButton(participantOwes, amount, participantOwedTo);
            HBox debtBox = generateDebtBox(expandButton, debtLabel, settleButton);
            children.addLast(debtBox);
            children.addLast(bankDetailsPane);
        }

    }

    private Pane generateBankDetailsPane(Participant participant) {
        Pane pane = new Pane();
        pane.getChildren().add(new Label(participant.getBankDetails()));
        pane.setVisible(false);
        pane.setManaged(false);
        return pane;
    }

    private HBox generateDebtBox(Button expandButton, Label debtLabel, Button settleButton){
        Region spacingL = new Region();
        HBox.setHgrow(spacingL, Priority.ALWAYS);
        Region spacingR = new Region();
        HBox.setHgrow(spacingR, Priority.ALWAYS);
        HBox box = new HBox(expandButton, spacingL, debtLabel, spacingR, settleButton);
        box.setSpacing(20);
        box.setAlignment(CENTER_LEFT);
        return box;
    }

    private Button generateSettleButton(Participant participantOwes,
                                        int amount,
                                        Participant participantOwedTo) {
        Button button = new Button();
        button.textProperty().bind(translation.getStringBinding("SettleDebts.Button.received"));
        Styling.applyStyling(button, "positiveButton");
        var onClick = utils.createSettleAction(participantOwes, amount,
                participantOwedTo, event.getId());
        button.setOnAction(onClick);

        return button;
    }

    private Button generateExpandButton(Pane pane) {
        Button button = new Button();
        button.textProperty().bind(translation.getStringBinding("SettleDebts.Button.expand"));
        Styling.applyStyling(button, "positiveButton");
        //This can instead be an image button!
        button.setOnMouseClicked((action)-> {
            pane.setVisible(!pane.isVisible());
            pane.setManaged(!pane.isManaged());
            if(pane.isVisible()){
                button.setRotate(90);
                if(lastExpanded!=null) lastExpanded.getValue().getOnMouseClicked().handle(null);
                lastExpanded = new Pair<>(pane, button);
            } else {
                button.setRotate(0);
                lastExpanded = null;
            }
        });
        return button;
    }

    private Label generateDebtLabel(Participant participantOwes,
                                    int amount, Participant participantOwedTo) {
        Label label = new Label();
        String debtString = utils.createDebtString(participantOwes.getName(),
                amount, "NOT IMPLEMENTED");
        label.setText(debtString);
        return label;
    }

    /***
     * Replaces the Event data
     * @param event the new event data to use
     */
    public void refresh(Event event){
        this.event = event;
        populateVBox();
    }

    /***
     * Switches back to the event screen
     */
    public void switchToEventScreen() {
        mainCtrl.switchToEventScreen();
    }

    /***
     * Specifies if the screen should be live-refreshed
     * @return true if changes should immediately refresh the screen, false otherwise
     */
    @Override
    public boolean shouldLiveRefresh() {
        return true;
    }
}
