package client.scenes;

import client.utils.*;
import com.google.inject.Inject;
import commons.Event;
import commons.Participant;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.util.Pair;

import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;

import static javafx.geometry.Pos.*;

public class SettleDebtsScreenCtrl implements Initializable, SimpleRefreshable {
    private final MainCtrl mainCtrl;
    private final Translation translation;
    private final SettleDebtsUtils utils;
    private final ImageUtils imageUtils;
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
     * @param imageUtils the image utilities to use
     */
    @Inject
    public SettleDebtsScreenCtrl(MainCtrl mainCtrl, Translation translation,
                                 SettleDebtsUtils utils, ImageUtils imageUtils) {
        this.mainCtrl = mainCtrl;
        this.translation = translation;
        this.utils = utils;
        this.imageUtils = imageUtils;
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
        ImageView backImageView = imageUtils.generateImageView("goBack.png", 20);
        goBackButton.setGraphic(backImageView);
    }

    /***
     * Populates the VBox with all the transfers
     */
    public void populateVBox(){
        List<Node> children = settleVBox.getChildren();
        children.clear();
        HashMap<Participant, BigDecimal> owedShares = event.getOwedShares();
        Set<Transfer> transfers = utils.calculateTransferInstructions(owedShares);

        Image expandButtonImage = imageUtils.loadImageFile("singlearrow.png");

        for(Transfer transfer: transfers){
            TextArea participantText = generateParticipantText(transfer.receiver());
            Pane bankDetailsPane = generateBankDetailsPane(participantText);
            ImageView expandButtonInnerImage = imageUtils.generateImageView(expandButtonImage, 25);
            Button expandButton = generateExpandButton(bankDetailsPane, expandButtonInnerImage);

            Label transferLabel = generateTransferLabel(transfer);
            Button settleButton = generateSettleButton(transfer);
            HBox transferBox = generateTransferDetailsBox(expandButton, transferLabel, settleButton);

            children.addLast(transferBox);
            children.addLast(bankDetailsPane);
        }

        if(children.isEmpty()) {
            Label emptyLabel = new Label();
            emptyLabel.textProperty().bind(translation.getStringBinding("SettleDebts.Label.noTransfers"));
            settleVBox.setAlignment(TOP_CENTER);
            children.addLast(emptyLabel);
        } else settleVBox.setAlignment(TOP_LEFT);
    }

    /***
     * Generates the expandable pane with payment details
     * @param text the TextArea containing payment instructions
     * @return a VBox Pane encapsulating payment details
     */
    public Pane generateBankDetailsPane(TextArea text) {
        VBox pane = new VBox();
        pane.getChildren().add(text);
        pane.setVisible(false);
        pane.setManaged(false);
        Styling.applyStyling(pane, "borderVBox");
        pane.setMaxWidth(400);
        return pane;
    }

    /***
     * Generates a TextArea containing bank details for a given Participant
     * @param participant the Participant to extract bank details from
     * @return a TextArea with all the Participant's bank information
     */
    public TextArea generateParticipantText(Participant participant){
        TextArea text = new TextArea();
        text.textProperty().bind(utils.getBankDetails(participant));
        text.setEditable(false);
        text.setPrefRowCount(4);
        text.setFont(new Font(14));
        Styling.applyStyling(text, "backgroundLight");
        return text;
    }

    /***
     * Generates an HBox with the Expand button, Transfer information, and Settle button
     * @param expandButton the Expand Details button
     * @param transferLabel the Label detailing who should send money to who
     * @param settleButton the Mark Received button
     * @return an HBox containing all given elements, plus the correct spacing
     */
    public HBox generateTransferDetailsBox(Button expandButton, Label transferLabel, Button settleButton){
        Region spacingL = new Region();
        HBox.setHgrow(spacingL, Priority.ALWAYS);
        Region spacingR = new Region();
        HBox.setHgrow(spacingR, Priority.ALWAYS);
        HBox box = new HBox(expandButton, spacingL, transferLabel, spacingR, settleButton);
        box.setSpacing(20);
        box.setAlignment(CENTER_LEFT);
        return box;
    }

    /***
     * Generates a Button that marks a debt payment as received and settled
     * @param transfer the Transfer information to use
     * @return a Button that settles the
     * when clicked
     */
    public Button generateSettleButton(Transfer transfer) {
        Button button = new Button();
        button.textProperty().bind(translation.getStringBinding("SettleDebts.Button.received"));
        Styling.applyStyling(button, "positiveButton");
        EventHandler<ActionEvent> onClick = utils.createSettleAction(transfer, event.getId());
        button.setOnAction(onClick);

        return button;
    }

    /***
     * Generates a Button that expands the bank details pane for a transfer
     * @param pane the Pane to expand/collapse
     * @param expandButtonInnerImage the image for the button, passed in here for better performance
     * @return a Button that expands/collapses the given Pane when clicked
     */
    public Button generateExpandButton(Pane pane, ImageView expandButtonInnerImage) {
        Button button = new Button();
        Styling.applyStyling(button, "positiveButton");
        button.setGraphic(expandButtonInnerImage);
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

    /***
     * Generates a Label detailing who should send how much money to who
     * @param transfer the Transfer dat ato use
     * @return a Label explaining who owes how much to who
     */
    public Label generateTransferLabel(Transfer transfer) {
        Label label = new Label();
        ObservableValue<String> transferString = utils.createTransferString(transfer);
        label.textProperty().bind(transferString);
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
        mainCtrl.switchScreens(EventScreenCtrl.class);
    }
}
