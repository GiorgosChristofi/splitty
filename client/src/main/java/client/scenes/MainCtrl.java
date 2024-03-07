package client.scenes;

import client.utils.Translation;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import commons.Event;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Pair;
import java.util.Locale;

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


    @Inject
    Translation translation;
    @Inject
    @Named("client.language")
    String language;

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

        this.managementOvervirewPasswordScene = new Scene(managementOverviewPassworrdUI.getValue());
        this.managementOverviewScreenScene = new Scene(managementOverviewScreenUI.getValue());

        showOverview();
        primaryStage.show();
    }

    public void showOverview() {
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
     * @param event the event to join
     */
    public void joinEvent(Event event){
        //TODO implement
        primaryStage.setScene(eventScene);
        eventScreenCtrl.setEvent(event);
        eventScreenCtrl.setParticipants(event);
        eventScreenCtrl.setParticipantsForExpenses(event);
        primaryStage.setTitle("Event Screen");
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

    public void switchToAddExpense() {
        expenseScreenCtrl.resetAll();
        expenseScreenCtrl.setEvent(eventScreenCtrl.getEvent());
        primaryStage.setScene(expenseScene);
    }

    public void openEditTitle() {
        editTitleCtrl.setEvent(eventScreenCtrl.getEvent());
        primaryStage.setScene(editTitleScene);
    }

    public void switchToAddParticipant() {
        participantScreenCtrl.setEvent(participantScreenCtrl.getEvent());
        primaryStage.setScene(participantScene);
    }

    public void switchToAddParticipantExistent() {
        participantScreenCtrl.setEvent(participantScreenCtrl.getEvent());
        participantScreenCtrl.setParticipant(participantScreenCtrl.getParticipant());
        primaryStage.setScene(participantScene);
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
}
