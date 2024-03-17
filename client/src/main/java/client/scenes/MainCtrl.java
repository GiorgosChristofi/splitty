package client.scenes;

import client.utils.ServerUtils;
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
    private Scene overview;
    private StartupScreenCtrl startupScreenCtrl;
    private EventScreenCtrl eventScreenCtrl;
    private ExpenseScreenCtrl expenseScreenCtrl;
    private EditTitleCtrl editTitleCtrl;
    private Scene startupScene;
    private Scene participantListScene;
    private Scene eventScene;
    private Scene expenseScene;
    private Scene participantScene;
    private ParticipantScreenCtrl participantScreenCtrl;
    private ParticipantListCtrl participantListCtrl;
    private Scene editTitleScene;
    private Scene managementOvervirewPasswordScene;
    private ManagementOverviewPasswordCtrl managementOverviewPasswordCtrl;
    private Scene managementOverviewScreenScene;

    private final Translation translation;
    @Inject
    @Named("client.language")
    private String language;
    private final ServerUtils server;
    private String eventCode;

    @Inject
    public MainCtrl(Translation translation, ServerUtils server) {
        this.translation = translation;
        this.server = server;
        this.eventCode = null;
    }

    public void initialize(Stage primaryStage, Pair<StartupScreenCtrl, Parent> overview,
                           Pair<EventScreenCtrl, Parent> eventUI, Pair<ExpenseScreenCtrl, Parent> expenseUI,
                           Pair<ParticipantScreenCtrl, Parent> participantUI, Pair<EditTitleCtrl, Parent> editTitlePair,
                           Pair<ParticipantListCtrl, Parent> participantListUI,
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
        this.participantListScene = new Scene(participantListUI.getValue());
        this.participantListCtrl = participantListUI.getKey();
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

    /**
     * When called the view changes to the event specified as input.
     * join an event (either used when creating or joining one) and updating the fields in the event screen
     */
    public void switchToEventScreen(){
        Event event = server.getEvent(eventCode);
        eventScreenCtrl.refresh(event);
        primaryStage.setScene(eventScene);
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
        Event event = server.getEvent(eventCode);
        expenseScreenCtrl.resetAll();
        expenseScreenCtrl.refresh(event);
        primaryStage.setScene(expenseScene);
    }

    public void openEditTitle() {
        Event event = server.getEvent(eventCode);
        editTitleCtrl.refresh(event);
        primaryStage.setScene(editTitleScene);
    }

    public void switchToAddParticipant() {
        Event event = server.getEvent(eventCode);
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
