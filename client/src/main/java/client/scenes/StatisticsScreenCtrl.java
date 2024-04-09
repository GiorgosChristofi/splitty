package client.scenes;

import client.utils.*;
import commons.Event;
import commons.Expense;
import commons.Participant;
import jakarta.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import java.net.URL;
import java.util.ResourceBundle;

public class StatisticsScreenCtrl implements Initializable, SimpleRefreshable {
    @FXML
    private AnchorPane parentPane;
    @FXML
    private Label statisticsLabel;
    @FXML
    private Label totalCostLabel;
    @FXML
    private Label expenseSumLabel;
    @FXML
    private Label pieChartLabel;
    @FXML
    private Label shareLabel;
    @FXML
    private PieChart tagPieChart;
    @FXML
    private Button goBackButton;
    private TableView<Participant> shareTable;
    private Event event;
    private final MainCtrl mainCtrl;
    private final Translation translation;
    private final ImageUtils imageUtils;

    /**
     * Constructor for StatisticsScreenCtrl
     * @param mainCtrl the main controller
     * @param translation the class that manages translations
     * @param imageUtils the ImageUtils to use
     */
    @Inject
    public StatisticsScreenCtrl(MainCtrl mainCtrl, Translation translation, ImageUtils imageUtils){
        this.mainCtrl = mainCtrl;
        this.translation = translation;
        this.imageUtils = imageUtils;
    }

    /**
     *
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
        statisticsLabel.textProperty()
                .bind(translation.getStringBinding("Stats.Label.Statistics"));
        totalCostLabel.textProperty()
                .bind(translation.getStringBinding("Stats.Label.TotalCost"));
        pieChartLabel.textProperty()
                .bind(translation.getStringBinding("Stats.Label.PieChart"));
        shareLabel.textProperty()
                .bind(translation.getStringBinding("Stats.Label.Share"));
        addGeneratedImages();
    }

    /**
     * sets the new total sum of expenses
     * sets the pie chart of the tags
     * @param event the new Event data to process
     */
    @Override
    public void refresh(Event event) {
        this.event = event;
        setTotalSumOfExpenses();
        setPieChart();
        parentPane.getChildren().remove(shareTable);
        TableView<Participant> newTable = generateShareTable(event);
        parentPane.getChildren().add(newTable);
        shareTable = newTable;
    }

    /**
     * it sets the label holding the cost of the entire event with the sum of expenses inside the event
     */
    public void setTotalSumOfExpenses(){
        int sum = event.getTotalSpending();
        expenseSumLabel.setText(FormattingUtils.getFormattedPrice(sum));
    }

    /**
     * set up the pie chart with the colors of the tags, their contribution to the pie Chart and
     * also has a way of showing the name of each tag in the piechart by using a line
     */
    private void setPieChart(){
        Map<String, Integer> tagExpenseMap = new HashMap<>();
        Map<String, String> tagColorMap = new HashMap<>();
        Set<Expense> expenses = event.getExpenses();
        for (Expense expense : expenses) {
            if(expense.getExpenseTag().getTagName().equals("money transfer"))
                continue;
            String tagName = expense.getExpenseTag().getTagName();
            int expensePrice = expense.getPriceInCents();
            String colorCode = expense.getExpenseTag().getColorCode();
            tagExpenseMap.merge(tagName, expensePrice, Integer::sum);
            tagColorMap.putIfAbsent(tagName, colorCode);
        }
        tagPieChart.getData().clear();
        tagPieChart.layout();
        for (Map.Entry<String, Integer> entry : tagExpenseMap.entrySet()) {
            PieChart.Data data = new PieChart.Data(entry.getKey(), entry.getValue());
            tagPieChart.getData().add(data);
        }
        tagPieChart.getData().forEach(data -> {
            String color = tagColorMap.get(data.getName());
            if (data.getNode() != null) {
                data.getNode().setStyle("-fx-pie-color: " + color + ";");
            } else {
                data.nodeProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        newValue.setStyle("-fx-pie-color: " + color + ";");
                    }
                });
            }
        });
        tagPieChart.setLegendVisible(false);
    }

    /***
     * Generates a table of participants and their expense shares
     * @param event the event to gather data from
     * @return a TableView with 2 columns: participant name, and expense share
     */
    public TableView<Participant> generateShareTable(Event event){
        TableView<Participant> table = new TableView<>();
        TableColumn<Participant, String> columnName = new TableColumn<>();
        TableColumn<Participant, String> columnAmount = new TableColumn<>();

        columnName.setCellValueFactory(participantValue -> new SimpleStringProperty(participantValue.getValue().getName()));

        HashMap<Participant, BigDecimal> expenseShare = event.getExpenseShare(false);
        HashMap<Participant, Integer> dataMap = RoundUtils.roundMap(expenseShare, RoundingMode.HALF_UP);
        columnAmount.setCellValueFactory(participantValue -> {
            Participant participant = participantValue.getValue();
            int shareOfParticipant = dataMap.get(participant);
            String formattedShare = FormattingUtils.getFormattedPrice(shareOfParticipant);
            return new SimpleStringProperty(formattedShare);
        });

        columnName.textProperty().bind(translation.getStringBinding("Stats.ColumnLabel.Participant"));
        columnName.setPrefWidth(100);
        table.getColumns().add(columnName);

        columnAmount.textProperty().bind(translation.getStringBinding("Stats.ColumnLabel.Share"));
        columnAmount.setPrefWidth(70);
        table.getColumns().add(columnAmount);

        table.setLayoutX(400);
        table.setLayoutY(180);
        table.setPrefHeight(200);
        table.setPrefWidth(170);
        table.setItems(FXCollections.observableList(event.getParticipants().stream().toList()));
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        //noinspection SuspiciousMethodCalls
        table.setItems(FXCollections.observableList(event.getParticipants().stream()
                .sorted(Comparator.comparing(dataMap::get).reversed())
                .toList()));
        return table;
    }

    /**
     * add the image to the go back button
     */
    private void addGeneratedImages() {
        ImageView goBackImage = imageUtils.generateImageView("goBack.png", 15);
        goBackButton.setGraphic(goBackImage);
    }

    /**
     * goes back to the event screen when pressing the go back button
     */
    public void switchToEventScreen() {
        mainCtrl.switchScreens(EventScreenCtrl.class);
    }
}
