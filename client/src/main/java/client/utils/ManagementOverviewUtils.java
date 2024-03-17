package client.utils;

import com.google.inject.Inject;
import commons.Event;
import commons.Expense;
import commons.Participant;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Comparator;

public class ManagementOverviewUtils {

    private final Translation translation;
    private final ServerUtils server;
    private final ObservableList<Event> events = FXCollections.observableArrayList();
    private final SimpleStringProperty ascending = new SimpleStringProperty();
    private final SimpleStringProperty descending = new SimpleStringProperty();
    private final StringProperty order = new SimpleStringProperty();
    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty creationDate = new SimpleStringProperty();
    private final StringProperty lastActivity = new SimpleStringProperty();


    @Inject
    public ManagementOverviewUtils(Translation translation, ServerUtils server) {
        this.translation = translation;
        this.server = server;
    }

    /**
     * Configure the translation for all order types.
     * @return an ObservableList which contains all the order types, each bound to their own translation.
     */
    public ObservableList<StringProperty> setOrderTypes() {
        title.bind(translation.getStringBinding("ManagementOverview.ComboBox.title"));
        creationDate.bind(translation.getStringBinding("ManagementOverview.ComboBox.creationDate"));
        lastActivity.bind(translation.getStringBinding("ManagementOverview.ComboBox.lastActivity"));
        return FXCollections.observableArrayList(title, creationDate, lastActivity);
    }

    /**
     * Retrieve events from the database and sorts them.
     * @return ObservableList of all events sorted by their title in ascending order.
     */
    public ObservableList<Event> retrieveEvents() {
        events.setAll(server.retrieveAllEvents());
        events.sort(Comparator.comparing(event -> event.getTitle().toLowerCase()));
        return events;
    }

    /**
     * Binds the sort button to its two states, translated by Translation.
     * @return The property bound to ascending.
     */
    public StringProperty bindSortButton() {
        ascending.bind(translation.getStringBinding("ManagementOverview.Button.ascending"));
        descending.bind(translation.getStringBinding("ManagementOverview.Button.descending"));
        order.bind(ascending);
        return order;
    }

    /**
     * Sort events according to the comparator and the current order
     * @param comparator Comparator to sort events with
     */
    public void sortEventsByComparator(Comparator<Event> comparator) {
        if(order.getValue().equals(ascending.getValue()))
            events.sort(comparator);
        else if(order.getValue().equals(descending.getValue()))
            events.sort(comparator.reversed());
    }

    /**
     * Sorts events
     * @param property Property that determines how to sort the events
     */
    public void sortEvents(StringProperty property) {
        if(property.getValue().equals(title.getValue()))
            sortEventsByComparator(Comparator.comparing(event -> event.getTitle().toLowerCase()));
        else if(property.getValue().equals(creationDate.getValue()))
            sortEventsByComparator(Comparator.comparing(Event::getCreationDate));
        else if(property.getValue().equals(lastActivity.getValue()))
            sortEventsByComparator(Comparator.comparing(Event::getLastActivity));
    }

    /**
     * Sort events by the same order
     * @param property Property to sort by
     */
    public void sortEventsSameOrder(StringProperty property) {
        sortEvents(property);
    }

    /**
     * Sort events in ascending order if the current order descending, and vice versa
     * @param property Property to sort by
     */
    public void sortEventsOtherOrder(StringProperty property) {
        order.bind(order.getValue().equals(ascending.getValue()) ? descending : ascending);
        sortEvents(property);
    }

    /**
     * Initializes the list of participants.
     * @param event Selected event
     * @return Observable list of the selects event's participant
     */
    public ObservableList<Participant> initializeParticipantsList(Event event) {
        return FXCollections.observableArrayList(event.getParticipants());
    }

    /**
     * Initializes the list of expenses
     * @param event Selected event
     * @return Observable list of the event's expenses
     */
    public ObservableList<Expense> initializeExpenseList(Event event) {
        return FXCollections.observableArrayList(event.getExpenses());
    }

    public ObservableList<Event> getEvents() {
        return events;
    }

    public SimpleStringProperty getAscending() {
        return ascending;
    }

    public SimpleStringProperty getDescending() {
        return descending;
    }
    public void bindAscending(StringProperty ascending) {
        this.ascending.bind(ascending);
    }

    public void bindDescending(StringProperty descending) {
        this.descending.bind(descending);
    }

    public StringProperty getOrder() {
        return order;
    }
    public void bindOrder(StringProperty order) {
        this.order.bind(order);
    }

    public void setEvents(ObservableList<Event> events) {
        this.events.setAll(events);
    }

    public void bindTitle(StringProperty title) {
        this.title.bind(title);
    }

    public void bindCreationDate(StringProperty creationDate) {
        this.creationDate.bind(creationDate);
    }

    public void bindLastActivity(StringProperty lastActivity) {
        this.lastActivity.bind(lastActivity);
    }
}
