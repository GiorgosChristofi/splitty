package client.scenes;

import client.utils.ServerUtils;
import client.utils.Translation;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import commons.Event;
import commons.Expense;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.util.Locale;
import java.util.Set;

public class MainCtrl {

    private Stage primaryStage;
    private QuoteOverviewCtrl overviewCtrl;
    private Scene overview;
    private StartupScreenCtrl startupScreenCtrl;
    private EventScreenCtrl eventScreenCtrl;
    private ExpenseScreenCtrl expenseScreenCtrl;
    private EditTitleCtrl editTitleCtrl;
    private Scene startupScene;
    private Scene add;
    private AddQuoteCtrl addCtrl;
    private Scene eventScene;
    private Scene expenseScene;

    private Scene participantScene;
    private ParticipantScreenCtrl participantScreenCtrl;

    private Scene editTitleScene;
    private Scene managementOvervirewPasswordScene;
    private ManagementOverviewPasswordCtrl managementOverviewPasswordCtrl;
    private Scene managementOverviewScreenScene;
    private ServerUtils serverUtils;

    @Inject
    Translation translation;
    @Inject
    @Named("client.language")
    String language;
    @Inject
    public MainCtrl (ServerUtils serverUtils) {
        this.serverUtils = serverUtils;
    }

    private String eventCode;

    @Inject
    public MainCtrl(Translation translation, ServerUtils server) {
        this.translation = translation;
        this.serverUtils = server;
        this.eventCode = null;
    }
    public void initialize(Stage primaryStage, Pair<StartupScreenCtrl, Parent> overview,
                           Pair<EventScreenCtrl, Parent> eventUI, Pair<ExpenseScreenCtrl, Parent> expenseUI,

                           Pair<ParticipantScreenCtrl, Parent> participantUI, Pair<EditTitleCtrl, Parent> editTitlePair,
                           Pair<ManagementOverviewPasswordCtrl, Parent> managementOverviewPassworrdUI, Pair<ManagementOverviewScreenCtrl, Parent> managementOverviewScreenUI) {


        translation.changeLanguage(Locale.forLanguageTag(language));
        this.primaryStage = primaryStage;
        this.startupScreenCtrl = overview.getKey();
        this.startupScene = new Scene(overview.getValue());
        this.eventScene = new Scene(eventUI.getValue());
        this.eventScreenCtrl = eventUI.getKey();
        this.expenseScene = new Scene(expenseUI.getValue());
        this.expenseScreenCtrl = expenseUI.getKey();

        this.participantScene = new Scene(participantUI.getValue());
        this.participantScreenCtrl = participantUI.getKey();

        this.editTitleCtrl = editTitlePair.getKey();
        this.editTitleScene = new Scene(editTitlePair.getValue());
        showMainScreen();
        this.managementOvervirewPasswordScene = new Scene(managementOverviewPassworrdUI.getValue());
        this.managementOverviewScreenScene = new Scene(managementOverviewScreenUI.getValue());
        primaryStage.show();
    }

    public void showMainScreen() {
        startupScreenCtrl.refreshEvents();
        primaryStage.setTitle("Main Menu");
        primaryStage.setScene(startupScene);
    }

    public void showAdd() {
        //primaryStage.setTitle("Quotes: Adding Quote");
        //primaryStage.setScene(add);
        //add.setOnKeyPressed(e -> addCtrl.keyPressed(e));
    }

    /**
     * When called the view changes to the event specified as input.
     * join an event (either used when creating or joining one) and updating the fields in the event screen
     */
//    public void joinEvent(Event event) {
//        //TODO implement
//        event = serverUtils.getEvent(event.getId());
//        Set<Expense> expensesForEvent = serverUtils.getExpensesForEvent(event.getId());
//        primaryStage.setScene(eventScene);
//        eventScreenCtrl.setEvent(event);
//        eventScreenCtrl.setParticipants(event);
//        eventScreenCtrl.setParticipantsForExpenses(event);
//        eventScreenCtrl.addExpenseToEventScreen(event);
//    }
    public void switchToEventScreen(){
        Event event = serverUtils.getEvent(eventCode);
        eventScreenCtrl.refresh(event);
        primaryStage.setScene(eventScene);
        primaryStage.setTitle("Event Screen");
        //System.out.println(expensesForEvent);
    }

    /**
     * switch the primary screen to the main screen
     */
    public void switchBackToMainScreen(){
        primaryStage.setScene(startupScene);
        primaryStage.setTitle("Main Menu");
    }

    /**
     * Gets startup screen
     * @return the startup screen
     */
    public Scene getMainMenuScene(){
        return startupScene;
    }

    public Scene getEventScene() {
        return eventScene;
    }

    public Scene getExpenseScene() {
        return expenseScene;
    }

    public void switchToAddExpense() {
        Event event = serverUtils.getEvent(eventCode);
        expenseScreenCtrl.resetAll();
        expenseScreenCtrl.refresh(event);
        primaryStage.setScene(expenseScene);
    }
//    public void switchToEditExpense(Expense expense) {
//        expenseScreenCtrl.setEvent(eventScreenCtrl.getEvent());
//        expenseScreenCtrl.setCurrentExpense(expense);
//        primaryStage.setScene(expenseScene);
//    }

    public void openEditTitle() {
        Event event = serverUtils.getEvent(eventCode);
        editTitleCtrl.refresh(event);
        primaryStage.setScene(editTitleScene);
    }

    public void switchToAddParticipant() {
        Event event = serverUtils.getEvent(eventCode);
        participantScreenCtrl.refresh(event);
        primaryStage.setScene(participantScene);
    }

    public void switchToAddParticipantExistent() {
        //TODO: Implement editing participants
    }

    /**
     * switch to the log in page for the management overview
     */
    public void switchToMnagamentOverviewPasswordScreen(){
        primaryStage.setScene(managementOvervirewPasswordScene);
        primaryStage.setTitle("Log in");
    }

    /**
     * go to the management overview screen
     */
    public void switchToManagementOverviewScreen(){
        primaryStage.setScene(managementOverviewScreenScene);
        primaryStage.setTitle("Management Overview");
    }

    public void switchEvents(String eventCode) {
        this.eventCode = eventCode;
    }
}
