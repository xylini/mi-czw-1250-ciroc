package pl.edu.agh.timekeeper.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Pair;
import pl.edu.agh.timekeeper.db.dao.ApplicationDao;
import pl.edu.agh.timekeeper.db.dao.RestrictionDao;
import pl.edu.agh.timekeeper.model.Application;
import pl.edu.agh.timekeeper.model.MyTime;
import pl.edu.agh.timekeeper.model.Restriction;
import pl.edu.agh.timekeeper.model.RestrictionBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class AddRestrictionController {

    @FXML
    private Pane mainPane;

    @FXML
    private TextField restrictionNameField;

    @FXML
    private HBox restrictionHBox;

    @FXML
    private RadioButton appRadioButton;

    @FXML
    private RadioButton groupRadioButton;

    @FXML
    private VBox scrollBox;

    @FXML
    private TextField hoursDailyField;

    @FXML
    private TextField minutesDailyField;

    @FXML
    private Button okButton;

    @FXML
    private RestrictionsListController restrictionsListController;

    private TextField applicationPathField = new TextField();

    private ComboBox groupComboBox = new ComboBox();

    private final ToggleGroup groupRadioButtons = new ToggleGroup();

    private final ApplicationDao applicationDao = new ApplicationDao();

    private final RestrictionDao restrictionDao = new RestrictionDao();

    private ObservableList<Pair<MyTime, MyTime>> rangeRestrictions = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        appRadioButton.setToggleGroup(groupRadioButtons);
        groupRadioButton.setToggleGroup(groupRadioButtons);

        if (!restrictionHBox.getChildren().contains(applicationPathField)) {
            Button browseButton = new Button("Browse");
            browseButton.setOnAction(this::browseClicked);
            applicationPathField.setPrefSize(250, 26);
            restrictionHBox.getChildren().addAll(applicationPathField, browseButton);
        }

        groupRadioButtons.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            restrictionHBox.getChildren().clear();
            if (appRadioButton.equals(newValue)) {
                restrictionHBox.getChildren().add(applicationPathField);
            } else if (groupRadioButton.equals(newValue)) {
                restrictionHBox.getChildren().add(groupComboBox);
            }
        });

        hoursDailyField.setTextFormatter(getHourTextFormatter());
        minutesDailyField.setTextFormatter(getMinuteTextFormatter());

        okButton.disableProperty().bind(Bindings.isEmpty(applicationPathField.textProperty())
                .or(Bindings.isEmpty(restrictionNameField.textProperty()))
                .or(Bindings.isEmpty(rangeRestrictions)
                        .and(Bindings.isEmpty(hoursDailyField.textProperty())
                                .and(Bindings.isEmpty(minutesDailyField.textProperty())))));
    }

    @FXML
    private void okClicked(ActionEvent actionEvent) {
        RadioButton selectedRadioButton = (RadioButton) groupRadioButtons.getSelectedToggle();
        if(!selectedRadioButton.equals(appRadioButton)) return; //TODO handle group case

        String applicationPath = applicationPathField.getText();
        String restrictionName = restrictionNameField.getText();

        Optional<Application> appOpt = applicationDao.getByPath(applicationPath);
        Application app = appOpt.orElseGet(() -> new Application(applicationPath, applicationPath));

        if(app.getRestriction() != null){
            // TODO: show message "Restriction for this application already exists"
            System.out.println("Restriction for this application already exists");
            return;
        }

        if(restrictionsListController.getRestrictionListView().getItems().contains(restrictionName)){
            // TODO: show message "Restriction with given name already exists"
            System.out.println("Restriction with given name already exists");
            return;
        }

        MyTime dailyLimit = getMyTime(hoursDailyField, minutesDailyField);

        Restriction restriction = buildRestriction(app, rangeRestrictions, dailyLimit);
        if(appOpt.isEmpty()) applicationDao.create(app);
        restrictionDao.create(restriction);
        restrictionsListController.getRestrictionListView().getItems().add(restrictionName);
        ((Stage) okButton.getScene().getWindow()).close();
    }

    private void browseClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file to impose a restriction");
        Stage stage = (Stage) mainPane.getScene().getWindow();
        Optional<File> file = Optional.ofNullable(fileChooser.showOpenDialog(stage));
        file.ifPresent((f) -> {
            applicationPathField.setText(f.getAbsolutePath());
        });
    }

    public void setRestrictionsListController(RestrictionsListController restrictionsListController) {
        this.restrictionsListController = restrictionsListController;
    }

    @FXML
    private void addButtonClicked(ActionEvent actionEvent) {
        int indexHBox = scrollBox.getChildren().size() - 1;
        HBox box = new HBox();
        box.setSpacing(5);
        box.setPadding(new Insets(5));
        box.setAlignment(Pos.CENTER_LEFT);
        List<TextField> textFields = new ArrayList<>(Arrays.asList(
                getHourTextField(), getMinuteTextField(), getHourTextField(), getMinuteTextField()));
        for (int index = 0; index < textFields.size(); index++) {
            textFields.get(index).setPrefSize(36, 25);
            if (index % 2 == 0) {
                textFields.get(index).setPromptText("HH");
            } else {
                textFields.get(index).setPromptText("MM");
            }
        }
        Label toLabel = new Label("To");
        //Delete button
        Button deleteButton = new Button();
        ImageView deleteImg = new ImageView("images/delete.png");
        deleteImg.setFitWidth(20);
        deleteImg.setFitHeight(20);
        deleteButton.setGraphic(deleteImg);
        deleteButton.setOnMouseClicked(deleteEvent);
        //
        toLabel.setPadding(new Insets(0, 0, 0, 20));
        box.getChildren().addAll(
                new Label("From"), textFields.get(0), new Label(":"), textFields.get(1),
                toLabel, textFields.get(2), new Label(":"), textFields.get(3), deleteButton);
        rangeRestrictions.add(null);
        IntStream.range(0, 4).forEach(i -> textFields.get(i).textProperty()
                .addListener(getTimeTextFieldChangeListener(textFields)));
        scrollBox.getChildren().add(indexHBox, box);
    }

    private EventHandler<MouseEvent> deleteEvent = new EventHandler<>() {
        @Override
        public void handle(final MouseEvent ME) {
            Object button = ME.getSource();
            if (button instanceof Button) {
                HBox parent = (HBox) ((Button) button).getParent();
                int index = scrollBox.getChildren().indexOf(parent);
                scrollBox.getChildren().remove(parent);
                rangeRestrictions.remove(index);
            }
        }
    };

    private MyTime getMyTime(TextField hours, TextField minutes){
        if(hours.getText().equals("") && minutes.getText().equals("")){
            return null;
        }
        int h = 0;
        int m = 0;
        if(!hours.getText().equals("")){
            h = Integer.valueOf(hours.getText());
        }
        if(!minutes.getText().equals("")){
            m = Integer.valueOf(minutes.getText());
        }
        return new MyTime(h, m);
    }

    private TextField getHourTextField(){
        TextField textField = new TextField();
        textField.setTextFormatter(getHourTextFormatter());
        return textField;
    }

    private TextField getMinuteTextField(){
        TextField textField = new TextField();
        textField.setTextFormatter(getMinuteTextFormatter());
        return textField;
    }

    private TextFormatter<Integer> getHourTextFormatter(){
        return new TextFormatter<>(change ->
                (change.getControlNewText().matches("((1[0-9])|(2[0-3])|[0-9])?")) ? change : null);
    }

    private TextFormatter<Integer> getMinuteTextFormatter() {
        return new TextFormatter<>(change ->
                (change.getControlNewText().matches("(([1-5][0-9])|[0-9])?")) ? change : null);
    }

    private Restriction buildRestriction(Application app, List<Pair<MyTime, MyTime>> rangeRestrictions, MyTime dailyLimit){
        RestrictionBuilder restrictionBuilder = new RestrictionBuilder()
                .setApplication(app)
                .setName(restrictionNameField.getText());
        if(!rangeRestrictions.isEmpty()) {
            restrictionBuilder
                    .setStart(rangeRestrictions.get(0).getKey())
                    .setEnd(rangeRestrictions.get(0).getValue());
        }
        if(dailyLimit != null){
            restrictionBuilder.setLimit(dailyLimit);
        }
        return restrictionBuilder.build();
    }

    private ChangeListener<String> getTimeTextFieldChangeListener(List<TextField> textFields){
        return (observable, oldValue, newValue) -> {
            MyTime start = getMyTime(textFields.get(0), textFields.get(1));
            MyTime end = getMyTime(textFields.get(2), textFields.get(3));
            Pair<MyTime, MyTime> newTime = (start != null && end != null && end.isAfter(start))? new Pair<>(start, end) : null;
            rangeRestrictions.set(scrollBox.getChildren().size()-2, newTime);

        };
    }
}
