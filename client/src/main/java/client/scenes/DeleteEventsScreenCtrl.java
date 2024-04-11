package client.scenes;

import client.utils.*;
import com.google.inject.Inject;
import commons.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.*;
import java.util.ResourceBundle;

public class DeleteEventsScreenCtrl implements Initializable {
    @FXML
    private Label selectWhichEventsToDelete;
    @FXML
    private ListView<Event> checkEventsListView;
    @FXML
    private Button deleteSelectedEvenetsButton;
    @FXML
    private Label noEventsSelectedLabel;
    @FXML
    private Button deleteAllEventsButton;
    @FXML
    private Button goBackButton;
    private final Map<Event, Boolean> eventSelectionMap = new HashMap<>();
    private final ServerUtils server;
    private final MainCtrl mainCtrl;
    private final Translation translation;
    private final ManagementOverviewUtils utils;
    private final ImageUtils imageUtils;
    private boolean listWasInitialized = false;
    /**
     * Constructor
     *
     * @param server      the ServerUtils instance to use
     * @param mainCtrl    the MainCtrl instance to use
     * @param translation the Translation instance to use
     * @param utils       the ManagementOverviewUtils instance to use
     * @param imageUtils  the ImageUtils instance to use
     */
    @Inject
    public DeleteEventsScreenCtrl(ServerUtils server, MainCtrl mainCtrl, Translation translation,
                                  ManagementOverviewUtils utils, ImageUtils imageUtils) {
        this.server = server;
        this.mainCtrl = mainCtrl;
        this.translation = translation;
        this.utils = utils;
        this.imageUtils = imageUtils;
    }

    /**
     * Initialize basic features for the Management Overview Screen
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
        selectWhichEventsToDelete.textProperty().bind(translation.getStringBinding("DES.Select.Delete"));
        deleteSelectedEvenetsButton.textProperty().bind(translation.getStringBinding("DES.Delete.Selected.Events.Button"));
        deleteAllEventsButton.textProperty().bind(translation.getStringBinding("DES.Delete.All.Events"));
        ImageView goBackImage = imageUtils.generateImageView("goBack.png", 15);
        goBackButton.setGraphic(goBackImage);
    }

    /**
     * initialize an event list view with checkboxes
     * keep the selected events in a hash-map
     */
    public void initializeEventsCheckList() {
        if(listWasInitialized) return;
        checkEventsListView.setItems(utils.retrieveEvents());
        checkEventsListView.setCellFactory(listView -> new ListCell<>() {
            private final CheckBox checkBox = new CheckBox();
            @Override
            protected void updateItem(Event event, boolean empty) {
                super.updateItem(event, empty);
                if (empty || event == null || event.getId() == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    checkBox.setText("Title: " + event.getTitle() + ", ID: " + event.getId());
                    checkBox.setOnAction(e -> eventSelectionMap.put(event, checkBox.isSelected()));
                    if (!eventSelectionMap.containsKey(event)) {
                        eventSelectionMap.put(event, false);
                    }
                    checkBox.setSelected(eventSelectionMap.get(event));
                    setGraphic(checkBox);
                }
            }
        });
    }

    /**
     * delete all the selected events
     * popup for checking whether the admin wants to proceed or not
     */
    public void deleteSelectedEvents() {
        if(!eventSelectionMap.containsValue(true)){
            noEventsSelectedLabel.textProperty().bind(translation.getStringBinding("DES.No.Events.Selected.Label"));
            Styling.changeStyling(noEventsSelectedLabel, "successText", "errorText");
            System.out.println("No events selected");
        }
        else {
            Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationDialog.setTitle("Delete Confirmation");
            confirmationDialog.setHeaderText("Delete Selected Events");
            confirmationDialog.setContentText("Are you sure you want to delete the selected events?");
            ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
            ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);
            confirmationDialog.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
            Optional<ButtonType> result = confirmationDialog.showAndWait();
            if (result.isPresent() && result.get() == buttonTypeYes) {
                List<Event> selectedEvents = eventSelectionMap.entrySet().stream()
                        .filter(Map.Entry::getValue)
                        .map(Map.Entry::getKey)
                        .toList();
                checkEventsListView.getItems().removeAll(selectedEvents);
                for (Event current : selectedEvents) {
                    server.deleteEvent(current.getId());
                    System.out.println("The following event has been deleted: " + current.getTitle());
                }
                noEventsSelectedLabel.textProperty().bind(translation.getStringBinding("DES.Event.Deleted.Sucessfully"));
                Styling.changeStyling(noEventsSelectedLabel, "errorText", "successText");
                eventSelectionMap.clear();
            } else {
                noEventsSelectedLabel.textProperty().bind(translation.getStringBinding("DES.Event.Deletion.Cancel"));
                System.out.println("Deletion cancelled.");
                Styling.changeStyling(noEventsSelectedLabel, "successText", "errorText");
            }
        }
    }

    /**
     * delete all the existent events from the database
     * popup for confirmation of the action
     * on click delete all the events
     */
    public void deleteAllEvents() {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Delete All Confirmation");
        confirmationDialog.setHeaderText("Delete All Events");
        confirmationDialog.setContentText("Are you sure you want to delete all the events?");
        ButtonType buttonTypeYes = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNo = new ButtonType("No", ButtonBar.ButtonData.NO);
        confirmationDialog.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        Optional<ButtonType> result = confirmationDialog.showAndWait();
        if (result.isPresent() && result.get() == buttonTypeYes) {
            server.deleteAllEvents();
            checkEventsListView.getItems().clear();
            eventSelectionMap.clear();
            System.out.println("Everything was deleted successfully!");
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    /**
     * go to the managament overview screen
     * on button press go back to the management overview screen
     */
    public void goBackToManagementOverview() {
        noEventsSelectedLabel.textProperty().bind(translation.getStringBinding(""));
        mainCtrl.switchToManagementOverviewScreen();
    }
}
