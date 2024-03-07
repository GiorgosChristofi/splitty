package client.scenes;

import client.utils.ServerUtils;
import client.utils.Translation;
import commons.Expense;
import commons.Event;
import commons.Participant;
import jakarta.inject.Inject;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.awt.*;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.scene.control.Button;
import javafx.event.ActionEvent;
import java.util.concurrent.Callable;

public class ExpenseScreenCtrl implements Initializable{
    private final ServerUtils server;
    @FXML
    private Label addEditExpense;
    @FXML
    private Label paidBy;
    @FXML
    private ComboBox<String> choosePayer;
    @FXML
    private Label purpose;
    @FXML
    private TextField expensePurpose;
    @FXML
    private Label amount;
    @FXML
    private TextField sum;
    @FXML
    private ComboBox<String> currency;
    @FXML
    private Label date;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Label splitMethod;
    @FXML
    private Label expenseType;
    @FXML
    private Checkbox splitBetweenAllCheckBox;
    @FXML
    private Label splitBetweenAllLabel;
    @FXML
    private Checkbox splitBetweenCustomCheckBox;
    @FXML
    private Label getSplitBetweenCustomLabel;
    @FXML
    private Button cancel;
    @FXML
    private Button confirm;
    private final MainCtrl mainCtrl;
    private Event currentEvent;
    private final Translation translation;
    @Inject
    public ExpenseScreenCtrl (ServerUtils server, MainCtrl mainCtrl, Translation translation) {
        this.mainCtrl = mainCtrl;
        this.translation = translation;
        this.server = server;
        //currency.setItems(FXCollections.observableArrayList("EUR"));
    }

    /**
     * responsible for setting up the fields that need to be translated
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currency.setItems(FXCollections.observableArrayList("EUR"));
    }

    public void binds() {
        addEditExpense.textProperty()
            .bind(translation.getStringBinding("Expense.Label.Display.Add"));
        paidBy.textProperty()
            .bind(translation.getStringBinding("Expense.Label.Display.paid"));
        purpose.textProperty()
            .bind(translation.getStringBinding("Expense.Label.Display.purpose"));
        amount.textProperty()
            .bind(translation.getStringBinding("Expense.Label.Display.amount"));
        date.textProperty()
            .bind(translation.getStringBinding("Expense.Label.Display.date"));
        splitMethod.textProperty()
            .bind(translation.getStringBinding("Expense.Label.Display.split"));
        splitBetweenAllLabel.textProperty()
            .bind(translation.getStringBinding("Expense.Label.Display.splitAll"));
        getSplitBetweenCustomLabel.textProperty()
            .bind(translation.getStringBinding("Expense.Label.Display.splitCustom"));
    }

    /**
     * Assign the event corresponding to the current expense
     * @param event the event
     */
    public void setEvent(Event event) {
        this.currentEvent = event;
    }

    /**
     * When pressing the Cancel button it takes the user
     * back to the Event Screen
     * @param actionEvent the action of clicking the button
     */
    public void switchToEventScreen(ActionEvent actionEvent) {
        mainCtrl.switchBackToEventScreen();
    }

    /**
     * resets the amount inserted in the amount TextField
     */
    public void resetAmount() {
        this.sum.clear();
    }

    /**
     * resets the text inserted in the purpose TextField
     */
    public void resetPurpose() {
        this.expensePurpose.clear();
    }

    /**
     * resets the date chosen for the datePicker field
     */
    public void resetDate() {
        this.datePicker.getEditor().clear();
    }

    /**
     * (Theoretically) resets the currency inserted in the currency
     * ComboBox
     */
    public void resetCurrency() {
        this.currency.getEditor().clear();
    }

    /**
     * Creates a new expense based on the information provided
     * in the ExpenseScreen
     * @return the created expense
     */
    public Expense createNewExpense() {
        String name = expensePurpose.getText();
        String priceInMoney = sum.getText();
        double price = 0;
        try {
            price = Double.parseDouble(priceInMoney);
        }
        catch (IllegalArgumentException e) {
            System.out.println("Please enter a valid number");
        }
        int priceInCents = (int) Math.ceil(price * 100);
        //change in case of wanting to implement another date system
        Date expenseDate = Date.valueOf(datePicker.getValue());
        Participant participant =
            new Participant(choosePayer.getValue(), currentEvent);
        return new Expense(name, priceInCents, expenseDate, currentEvent, participant);
    }

    public void addExpenseToEvenScreen(ActionEvent actionEvent) {
        mainCtrl.switchBackToEventScreen();
    }

    //TODO: Adding the expense to the EventScreen
    //TODO: Getting the participant that paid (after the participant UI is implemented)
    //TODO: Making the user able to choose between participants that can pay(for the custom participants button)
}
