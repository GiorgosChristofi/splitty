package client.scenes;

import client.utils.*;
import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import jakarta.persistence.EntityNotFoundException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

import static javafx.geometry.Pos.CENTER_RIGHT;

public class EventScreenCtrl implements Initializable, SimpleRefreshable{
    @FXML
    private TextField invitationCode;
    @FXML
    private Button addExpense;
    @FXML
    private Button allExpenses;
    @FXML
    private Button fromButton;
    @FXML
    private Button inButton;
    @FXML
    private Button settleDebtsButton;
    @FXML
    private Label eventNameLabel;
    @FXML
    private Label participantsLabel;
    @FXML
    private Label participantsName;
    @FXML
    private Label expenseLabel;
    @FXML
    private Button editParticipant;
    @FXML
    private Button addParticipant;
    @FXML
    private Button goBackButton;
    @FXML
    private ComboBox<String> cBoxParticipantExpenses;
    @FXML
    private Label errorInvalidParticipant;
    @FXML
    private ListView<HBox> expensesLogListView;
    @FXML
    private ComboBox<Locale> languageIndicator;
    @FXML
    private Button addTagButton;
    @FXML
    private Button showStatisticsButton;
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final Translation translation;
    private final LanguageIndicatorCtrl languageCtrl;
    private final ImageUtils imageUtils;
    private Event event;
    private final Map<Long, HBox> hBoxMap;
    private Button selectedExpenseListButton;
    @FXML
    private Button testEmailButton;
    private EmailHandler emailHandler;
    @FXML
    private Label emailFeedbackLabel;
    @FXML
    private Button emailInviteButton;
    @FXML
    private Button transferMoneyButton;
    /**
     * Constructor
     *
     * @param server      the ServerUtils instance
     * @param mainCtrl    the MainCtrl instance
     * @param translation the Translation to use
     * @param languageCtrl the LanguageIndicator to use
     * @param imageUtils  the ImageUtils to use
     */
    @Inject
    public EventScreenCtrl(ServerUtils server, MainCtrl mainCtrl, Translation translation,
                           LanguageIndicatorCtrl languageCtrl, ImageUtils imageUtils) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.translation = translation;
        this.imageUtils = imageUtils;
        this.event = null;
        this.hBoxMap = new HashMap<>();
       // this.buttonsHBox = new HBox();
        this.allExpenses = new Button();
        this.fromButton = new Button();
        this.inButton = new Button();
        this.languageCtrl = languageCtrl;
        this.selectedExpenseListButton = null;
    }

    /**
     * Initialize basic features of the application, bind text to be translated,
     * set images for edit, add participants  and go back buttons
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
        invitationCode.setEditable(false);
        testEmailButton.textProperty().bind(translation.getStringBinding("Event.Button.TestEmail"));
        participantsName.textProperty()
            .bind(translation.getStringBinding("Participants.DisplayName.EventScreen"));
        expenseLabel.textProperty()
            .bind(translation.getStringBinding("Expense.Label.Display.EventScreen"));
        addExpense.textProperty()
            .bind(translation.getStringBinding("Event.Button.AddExpense"));
        errorInvalidParticipant.textProperty()
            .bind(translation.getStringBinding("empty"));
        settleDebtsButton.textProperty()
            .bind(translation.getStringBinding("Event.Button.SettleDebts"));
        addTagButton.textProperty()
            .bind(translation.getStringBinding("Event.Button.AddTag"));
        showStatisticsButton.textProperty()
            .bind(translation.getStringBinding("Event.Button.Statistics"));
        initializeEditTitle();
        addGeneratedImages();
        initializeParticipantsCBox();
        emailHandler = new EmailHandler();
        if (emailHandler.isConfigured()) {
            enableEmailFeatures();

        }
        emailFeedbackLabel.textProperty().bind(translation.getStringBinding("empty"));
        emailInviteButton.textProperty().bind(translation.getStringBinding("Event.Button.InviteByEmail"));
        languageCtrl.initializeLanguageIndicator(languageIndicator);
        transferMoneyButton.textProperty().bind(translation.getStringBinding("Event.Button.TransferMoney"));
    }

    /**
     * Enables the email features
     */
    private void enableEmailFeatures() {
        Styling.removeStyling(testEmailButton, "disabledButton");
        Styling.removeStyling(emailInviteButton, "disabledButton");
        emailInviteButton.setOnAction(event -> mainCtrl.switchScreens(EmailInviteCtrl.class));
        testEmailButton.setOnAction(event -> sendTestEmail());
    }

    /**
     * Adds generated images to the buttons
     */
    private void addGeneratedImages() {
        ImageView editParticipantImage = imageUtils.generateImageView("editing.png", 15);
        editParticipant.setGraphic(editParticipantImage);
        ImageView addParticipantImage = imageUtils.generateImageView("add-participant.png", 15);
        addParticipant.setGraphic(addParticipantImage);
        ImageView goBackImage = imageUtils.generateImageView("goBack.png", 15);
        goBackButton.setGraphic(goBackImage);
    }

    /**
     * Initializes the Combobox so the participant chosen is
     * reflected in the filter buttons
     */
    public void initializeParticipantsCBox() {
        cBoxParticipantExpenses.valueProperty()
            .addListener((observable, oldValue, newValue) -> {
                boolean validParticipant = false;
                Set<Participant> participants = event.getParticipants();
                for(Participant participant: participants)
                    if(participant.getName().equalsIgnoreCase(newValue)){
                        validParticipant = true;
                        break;
                    }
                if(validParticipant) {
                    errorInvalidParticipant.textProperty()
                        .bind(translation.getStringBinding("empty"));
                    setButtonsNames(newValue);
                }
                else {
                    if(newValue == null || newValue.isEmpty())
                        setButtonsNames("...");
                    else {
                        errorInvalidParticipant.textProperty()
                            .bind(translation.getStringBinding(
                                "Event.Label.Error.InvalidParticipant"));
                    }
                }
            });
    }

    private void initializeEditTitle() {
        eventNameLabel.setOnMouseClicked(mouseEvent -> {
            if(mouseEvent.getClickCount() == 2)
                mainCtrl.switchScreens(EditTitleCtrl.class);
        });
    }

    /***
     * Updates all data viewed
     * @param event the Event to extract data from
     */
    public void refresh(Event event){
        this.event = event;
        updateEventText();
        updateParticipants();
        updateParticipantsDropdown();
        refreshExpenseList();
        errorInvalidParticipant.textProperty()
                .bind(translation.getStringBinding("empty"));
        //buttonsHBox.getChildren().clear();
        languageCtrl.refresh(languageIndicator);
    }

    /**
     * Open the title editing screen.
     */
    public void editTitle() {
        mainCtrl.switchScreens(EditTitleCtrl.class);
    }

    /**
     * UI for inviting participants that needs to be implemented when the button is pressed
     */
    public void inviteParticipants(){
        //TODO: implement UI for inviting participants
    }

    /**
     * UI for editing current participants that needs to be implemented when the button is pressed
     */
    public void editCurrentParticipants(){
        mainCtrl.switchScreens(ParticipantListScreenCtrl.class);
    }

    /**
     * UI for adding a participant that needs to be implemented when the button is pressed
     */
    public void addParticipants(){
        mainCtrl.switchScreens(ParticipantScreenCtrl.class);
    }

    /**
     * UI for adding an expense that needs to be implemented when the button is pressed
     */
    public void addExpense(){
        mainCtrl.switchScreens(ExpenseScreenCtrl.class);
    }

    /**
     * set the name of the event in the event screen
     */
    private void updateEventText() {
        eventNameLabel.setText(event.getTitle());
        invitationCode.setText(event.getId());
    }

    /**
     * set the participants in the event screen that are part of the current event
     */
    private void updateParticipants(){
        StringBuilder participantsText = new StringBuilder();
        Iterator<Participant> participantIterator = event.getParticipants().iterator();
        while(participantIterator.hasNext()){
            Participant current = participantIterator.next();
            if(participantIterator.hasNext()) participantsText.
                    append(current.getName()).append(", ");
            else participantsText.append(current.getName());
        }
        if(participantsText.toString().isEmpty()) {
            participantsLabel.setText("There are no participants");
        }
        else participantsLabel.setText(participantsText.toString());
    }

    /**
     * show the participants of the current event
     */
    private void updateParticipantsDropdown(){
        List<String> names = new ArrayList<>();
        var boxItems = cBoxParticipantExpenses.getItems();
        //listViewExpensesParticipants.getItems().clear();
        for (Participant current : event.getParticipants()) {
            names.add(current.getName());
        }
        boxItems.retainAll(names);
        names.removeAll(boxItems);
        boxItems.addAll(names);
    }

    /**
     * In the context of this app, when choosing one of the participants
     * we want to see 3 buttons, 2 of which are related to the name of the participant.
     * This method generates those buttons and adds them to the hBox
     * @param name the name from the buttons
     */
    public void setButtonsNames(String name) {
        allExpenses.textProperty().set("All");
        fromButton.textProperty().set("From " + name);
        inButton.textProperty().set("Including " + name);
    }

    /**
     * the action when we press the "All" button
     */
    public void showAllExpenses(){
        this.selectedExpenseListButton = allExpenses;
        refreshExpenseList();
    }

    private void refreshExpenseList() {
        Button selectedButton = selectedExpenseListButton;
        if(selectedButton == null) selectedButton = allExpenses;
        if(selectedButton == allExpenses) showAllExpenseList();
        else if(selectedButton == fromButton) fromFilter();
        else if(selectedButton == inButton) includingFilter();
    }

    /**
     * Generates a list of hBoxes stating with a human-readable String
     * that presents a quick overview of the expense presented
     * the method throws an error
     */
    public void showAllExpenseList() {
        expensesLogListView.getItems().clear();
        //We only want to load this in once! IO is expensive.
        Image removeImage = imageUtils.loadImageFile("x_remove.png");
        for(Expense expense: event.getExpenses()) {
            if(!expense.getParticipantsInExpense().isEmpty()){
                HBox expenseBox = generateExpenseBox(expense, removeImage);
                expensesLogListView.getItems().add(expenseBox);
            }
        }
    }

    /**
     *
     * @param expense the expense for which we need to generate the HBox
     * @param removeImage the Image to use for the X icon
     * @return the generated hBox, containing the expense details
     */
    public HBox generateExpenseBox(Expense expense, Image removeImage) {
        String log = "";
        if(expense.getPriceInCents() > 0)
            log = generateTextForExpenseLabel(expense);
        else if(expense.getPriceInCents() < 0)
            log = generateTextForMoneyTransfer(expense);
        Label expenseText = generateExpenseLabel(expense.getId(), log);
        ImageView xButton = generateRemoveButton(expense.getId(), removeImage);
        Label dateLabel = new Label();
        dateLabel.setPrefWidth(40);
        dateLabel.setWrapText(true);
        Calendar expenseCalendar  = Calendar.getInstance();
        expenseCalendar.setTime(expense.getDate());
        Calendar now = Calendar.getInstance();
        SimpleDateFormat shortDate = new SimpleDateFormat("dd/MM");
        SimpleDateFormat fullDate = new SimpleDateFormat("dd/MM/yyyy");
        if(expenseCalendar.get(Calendar.YEAR) != now.get(Calendar.YEAR))
            dateLabel.setText(shortDate.format(expense.getDate()));
        else
            dateLabel.setText(fullDate.format(expense.getDate()));
        HBox xHBox = new HBox(xButton);
        HBox.setHgrow(xHBox, javafx.scene.layout.Priority.ALWAYS);
        xHBox.setAlignment((CENTER_RIGHT));
        HBox expenseBox = new HBox(dateLabel, expenseText, xHBox);
        expenseBox.setPrefWidth(expensesLogListView.getPrefWidth());
        expenseBox.setSpacing(10);
        hBoxMap.put(expense.getId(), expenseBox);
        expenseBox.setPrefWidth(expensesLogListView.getPrefWidth());
        expenseBox.setAlignment(Pos.CENTER_LEFT);
        return expenseBox;
    }

    /**
     * Generates the expense description for money transfers in this format: "A paid B 60.0"
     * @param expense The money transfer
     * @return Description of money transfer
     */
    private String generateTextForMoneyTransfer(Expense expense) {
        Participant from = (Participant) expense.getParticipantsInExpense().toArray()[0];
        double amount = expense.getPriceInCents() / -100.0;
        return from.getName() +
                " paid " +
                expense.getOwedTo().getName() +
                " " + amount;
    }

    /**
     *
     * @param expense the expense for which we are generating the
     * label
     * @return the resulting generated string
     */
    public String generateTextForExpenseLabel(Expense expense) {
        String log = expense.stringOnScreen();
        Set<Participant> participants = event.getParticipants();
        boolean all = true;
        for(Participant participant: participants) {
            all = false;
            for (Participant participant1 : expense.getParticipantsInExpense())
                if (participant.getId() == participant1.getId()) {
                    all = true;
                    break;
                }
            if(!all)
                break;
        }
        if(all)
            log += '\n' + "(All)";
        else {
            if(expense.getParticipantsInExpense().isEmpty())
                removeFromList(expense.getId());
            log += '\n' + "(";
            for(Participant participant: expense.getParticipantsInExpense())
                if(event.getParticipants().contains(participant)) {
                    log += participant.getName();
                    log += " ";
                }
            log += ")";
        }
        return log;
    }

    /**
     * Creates a label that will later be added to the hBox from the expense listview
     * Furthermore, it allows the client to access set expense and edit it
     * @param expenseId the id of the expense we are creating a label for
     * @param expenseTitle the title of the expense
     * @return the created label
     */
    public Label generateExpenseLabel(long expenseId, String expenseTitle) {
        Label expense = new Label(expenseTitle);
        expense.setOnMouseEntered(mouseEvent -> mainCtrl.getEventScene().setCursor(Cursor.HAND));
        expense.setOnMouseExited(mouseEvent -> mainCtrl.getEventScene().setCursor(Cursor.DEFAULT));
        expense.setOnMouseClicked(mouseEvent -> mainCtrl.switchToEditExpense(expenseId));
        return expense;
    }

    /**
     * Generates a remove "button" for the expenses from the listview
     * Furthermore, it allows the client to remove set event by pressing the 'X'
     * @param expenseId the id of the expense to be generated
     * @param image the Image to be placed on the button
     * @return the generated button
     * this exception is thrown
     */
    public ImageView generateRemoveButton (long expenseId, Image image) {
        ImageView imageView = imageUtils.generateImageView(image, 15);
        imageView.setPickOnBounds(true);
        imageView.setOnMouseEntered(mouseEvent -> mainCtrl.getEventScene().setCursor(Cursor.HAND));
        imageView.setOnMouseExited(mouseEvent -> mainCtrl.getEventScene().setCursor(Cursor.DEFAULT));
        imageView.setOnMouseClicked(mouseEvent -> removeFromList(expenseId));
        return imageView;
    }

    /**
     * Deletes an expense from the server. It also reflects it in the client
     * by deleting the set expense from the listview
     * @param expenseId the id of the expense we want to delete
     */
    public void removeFromList(long expenseId){
        server.deleteExpenseForEvent(event.getId(), expenseId);
        HBox hBox = hBoxMap.get(expenseId);
        hBoxMap.remove(expenseId);
        expensesLogListView.getItems().remove(hBox);
    }


    /**
     * UI for settling current debts
     */
    public void settleDebts() {
        mainCtrl.switchScreens(SettleDebtsScreenCtrl.class);
    }

    /**
     * go back to the main screen
     */
    public void switchToMainScreen() {
        mainCtrl.showMainScreen();
    }

    /**
     * Sends a test email to the user
     */
    public void sendTestEmail() {
        if (!emailHandler.isConfigured()){
            return;
        }
        Styling.changeStyling(emailFeedbackLabel, "errorText", "successText");
        emailFeedbackLabel.textProperty()
                .bind(translation.getStringBinding("Event.Label.EmailFeedback.Sending"));
        Thread t = new Thread(() ->
        {
            boolean emailSent = emailHandler.sendTestEmail();
            Platform.runLater(() -> {
                if (emailSent) {
                    Styling.changeStyling(emailFeedbackLabel, "errorText", "successText");
                    emailFeedbackLabel.textProperty()
                            .bind(translation.getStringBinding("Event.Label.EmailFeedback.Success"));
                } else if (emailHandler.isConfigured()) {
                    Styling.changeStyling(emailFeedbackLabel, "successText", "errorText");
                    emailFeedbackLabel.textProperty()
                            .bind(translation.getStringBinding("Event.Label.EmailFeedback.Fail"));
                }
            });
        });
        t.start();
    }
    /**
     * Filters the expenses, showing only the ones that were paid by a certain participant
     */
    public void fromFilter() {
        selectedExpenseListButton = fromButton;
        expensesLogListView.getItems().clear();
        String name = cBoxParticipantExpenses.getValue();
        Image removeImage = imageUtils.loadImageFile("x_remove.png");
        for(Expense expense: event.getExpenses())
            if(expense.getOwedTo().getName().equals(name)) {
                HBox expenseBox = generateExpenseBox(expense, removeImage);
                expensesLogListView.getItems().add(expenseBox);
            }
    }

    /**
     * Filters the expenses, showing the one a certain participant is part of
     */
    public void includingFilter() {
        selectedExpenseListButton = inButton;
        expensesLogListView.getItems().clear();
        String name = cBoxParticipantExpenses.getValue();
        Set<Participant> eventParticipants = event.getParticipants();
        Participant selectedParticipant = null;
        for(Participant participant: eventParticipants) {
            if(participant.getName().equals(name)) {
                selectedParticipant = participant;
                break;
            }
        }
        if(selectedParticipant == null)
            throw new EntityNotFoundException("The participant doesn't exist");
        Set<Expense> eventExpenses = event.getExpenses();
        Image removeImage = imageUtils.loadImageFile("x_remove.png");
        for(Expense expense: eventExpenses) {
            Set<Participant>participantsInExpense = expense.getParticipantsInExpense();
            if(participantsInExpense.contains(selectedParticipant)) {
                HBox expenseBox = generateExpenseBox(expense, removeImage);
                expensesLogListView.getItems().add(expenseBox);
            }
        }
    }

    /**
     * Transfer to money transfer screen.
     */
    public void transferMoney() {
        mainCtrl.switchScreens(TransferMoneyCtrl.class);
    }

    /**
     * when pressing the add Tag button it switches to that screen
     */
    public void switchToAddTag() {
        mainCtrl.switchScreens(AddTagCtrl.class);
    }

    /**
     * when pressing on the Show statistics button it switches to the statistics screen
     */
    public void switchToStatistics() {
        mainCtrl.switchScreens(StatisticsScreenCtrl.class);
    }
}
