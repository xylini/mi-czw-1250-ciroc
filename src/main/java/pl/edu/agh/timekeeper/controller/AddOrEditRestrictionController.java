package pl.edu.agh.timekeeper.controller;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pl.edu.agh.timekeeper.db.dao.ApplicationDao;
import pl.edu.agh.timekeeper.db.dao.GroupDao;
import pl.edu.agh.timekeeper.db.dao.RestrictionDao;
import pl.edu.agh.timekeeper.model.*;

import java.io.File;
import java.util.*;
import java.util.stream.IntStream;

public class AddOrEditRestrictionController {

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

    private static final String IMAGE_DELETE_PATH = "images/delete.png";

    private static final String ADD_GROUP_VIEW_PATH = "/views/addGroupView.fxml";

    private TextField applicationPathField = new TextField();

    private Button browseButton;

    private ComboBox groupComboBox = new ComboBox();

    private final ToggleGroup groupRadioButtons = new ToggleGroup();

    private final GroupDao groupDao = new GroupDao();

    private ObservableList<String> groupList = FXCollections.observableArrayList();

    private AddGroupController addGroupController;

    private ObservableMap<Integer, TimePair> rangeRestrictions = FXCollections.observableHashMap();

    private BooleanProperty isEditedProperty = new SimpleBooleanProperty(false);

    private ControllerUtils controllerUtils = new ControllerUtils();

    private final ApplicationDao applicationDao = new ApplicationDao();

    private final RestrictionDao restrictionDao = new RestrictionDao();

    private String selectedApplicationName;

    private void makeBrowseButton() {
        this.browseButton = new Button("Browse");
        browseButton.setOnAction(this::browseClicked);
    }

    @FXML
    private void initialize() {
        appRadioButton.setToggleGroup(groupRadioButtons);
        groupRadioButton.setToggleGroup(groupRadioButtons);
        makeBrowseButton();
        if (appRadioButton.isSelected()) {
            restrictionHBox.getChildren().clear();
            applicationPathField.setPrefSize(250, 26);
            groupComboBox.setPrefSize(230, 26);
            restrictionHBox.getChildren().addAll(applicationPathField, browseButton);
        }

        groupDao.getAll().forEach(g -> this.groupList.add(g.getName()));

        addRadioButtonsListener();
        isEditedProperty.addListener((observable, oldValue, newValue) -> {
            restrictionNameField.setDisable(newValue);
            applicationPathField.setDisable(newValue);
            groupComboBox.setDisable(newValue);
            browseButton.setVisible(!newValue);
            appRadioButton.setVisible(!newValue);
            groupRadioButton.setVisible(!newValue);
            if (newValue)
                okButton.setText("Save restriction");
            else
                okButton.setText("Add restriction");
        });
    }

    public void setRestrictionsListController(RestrictionsListController restrictionsListController) {
        this.restrictionsListController = restrictionsListController;
    }

    private void addRadioButtonsListener() {
        groupRadioButtons.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            restrictionHBox.getChildren().clear();
            if (appRadioButton.equals(newValue)) {
                restrictionHBox.getChildren().addAll(applicationPathField, browseButton);
            } else if (groupRadioButton.equals(newValue)) {
                groupComboBox.setItems(groupList);

                MenuItem menuItemAdd = new MenuItem("Add");
                MenuItem menuItemEdit = new MenuItem("Edit selected");
                MenuItem menuItemDelete = new MenuItem("Delete selected");
                MenuButton menuButton = new MenuButton("Manage", null,
                        menuItemAdd, menuItemEdit, menuItemDelete);

                menuItemEdit.disableProperty().bind(groupComboBox.getSelectionModel().selectedItemProperty().isNull());
                menuItemDelete.disableProperty().bind(groupComboBox.getSelectionModel().selectedItemProperty().isNull());
                menuButton.disableProperty().bind(isEditedProperty);

                menuItemAdd.setOnAction(addGroupEvent);
                menuItemEdit.setOnAction(editGroupEvent);
                menuItemDelete.setOnAction(deleteGroupEvent);
                restrictionHBox.getChildren().addAll(groupComboBox, menuButton);
            }
        });

        hoursDailyField.setTextFormatter(getHourTextFormatter());
        minutesDailyField.setTextFormatter(getMinuteTextFormatter());

        okButton.disableProperty().bind(Bindings.isEmpty(restrictionNameField.textProperty())
                .or(appRadioButton.selectedProperty())
                        .and(Bindings.isEmpty(applicationPathField.textProperty()))
                .or(groupRadioButton.selectedProperty()
                        .and(groupComboBox.getSelectionModel().selectedItemProperty().isNull()))
                .or(Bindings.isEmpty(rangeRestrictions)
                        .and(Bindings.isEmpty(hoursDailyField.textProperty())
                                .and(Bindings.isEmpty(minutesDailyField.textProperty())))));
    }

    @FXML
    private void okClicked(ActionEvent actionEvent) {
        if(appRadioButton.isSelected()){
            addAppRestriction();
        } else {
            addGroupRestriction();
        }
    }

    private void browseClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file to impose a restriction");
        Stage stage = (Stage) mainPane.getScene().getWindow();
        Optional<File> file = Optional.ofNullable(fileChooser.showOpenDialog(stage));
        file.ifPresent((f) -> {
            this.selectedApplicationName = f.getName();
            applicationPathField.setText(f.getAbsolutePath());
        });
    }

    @FXML
    private void addButtonClicked(ActionEvent actionEvent) {
        List<TextField> textFields = getCleanRangeTextFields();
        createCleanHourRangeBox(textFields);
    }

    private void createCleanHourRangeBox(List<TextField> textFields) {
        int boxIndex = scrollBox.getChildren().size() - 1;
        HBox box = new HBox();
        box.setSpacing(5);
        box.setPadding(new Insets(5));
        box.setAlignment(Pos.CENTER_LEFT);
        Label toLabel = new Label("To");
        //Delete button
        Button deleteButton = controllerUtils.createButton(IMAGE_DELETE_PATH, deleteEvent);

        toLabel.setPadding(new Insets(0, 0, 0, 20));
        box.getChildren().addAll(
                new Label("From"), textFields.get(0), new Label(":"), textFields.get(1),
                toLabel, textFields.get(2), new Label(":"), textFields.get(3), deleteButton);
        IntStream.range(0, 4).forEach(i -> textFields.get(i).textProperty()
                .addListener(getTimeTextFieldChangeListener(textFields)));
        scrollBox.getChildren().add(boxIndex, box);
    }

    private List<TextField> getCleanRangeTextFields() {
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
        return textFields;
    }

    public void prepareEditScreen(Restriction restriction) {
        restrictionNameField.setText(restriction.getName());
        this.isEditedProperty.setValue(true);

        if(restriction.getApplication() != null) {
            appRadioButton.setSelected(true);
            applicationPathField.setText(restriction.getApplication().getPath());
        } else {
            groupRadioButton.setSelected(true);
            groupComboBox.getSelectionModel().select(restriction.getGroup().getName());
        }
        createRangeBoxesWithData(restriction.getBlockedHours());
        Optional<MyTime> dailyLimit = Optional.ofNullable(restriction.getLimit());
        dailyLimit.ifPresent(limit -> {
            hoursDailyField.setText(String.valueOf(limit.getHour()));
            minutesDailyField.setText(String.valueOf(limit.getMinute()));
        });
    }

    private void createRangeBoxesWithData(Collection<TimePair> blockedHours) {
        blockedHours.forEach(pair -> {
            List<TextField> fields = new ArrayList<>(Arrays.asList(
                    getHourTextField(), getMinuteTextField(), getHourTextField(), getMinuteTextField()));
            createCleanHourRangeBox(fields);
            fields.forEach(field -> field.setPrefSize(36, 25));
            fields.get(0).setText(String.valueOf(pair.getStart().getHour()));
            fields.get(1).setText(String.valueOf(pair.getStart().getMinute()));
            fields.get(2).setText(String.valueOf(pair.getEnd().getHour()));
            fields.get(3).setText(String.valueOf(pair.getEnd().getMinute()));
        });
    }

    private EventHandler<MouseEvent> deleteEvent = new EventHandler<>() {
        @Override
        public void handle(final MouseEvent ME) {
            Object button = ME.getSource();
            if (button instanceof Button) {
                HBox parent = (HBox) ((Button) button).getParent();
                int index = scrollBox.getChildren().indexOf(parent);
                while (!rangeRestrictions.containsKey(index))
                    index++;
                scrollBox.getChildren().remove(parent);
                rangeRestrictions.remove(index);
            }
        }
    };

    private EventHandler<ActionEvent> addGroupEvent = (e) -> {
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(ADD_GROUP_VIEW_PATH));
        controllerUtils.openWindow(loader, "Add new group");
        addGroupController = loader.getController();
        addGroupController.setGroupsList(groupList);
        addGroupController.setGroupComboBox(groupComboBox);
    };

    private EventHandler<ActionEvent> editGroupEvent = (e) -> {
        Group group = groupDao.getByName((String)groupComboBox.getSelectionModel().getSelectedItem()).get();
        if(group.getRestriction() != null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Group with restrictions cannot be edited.");
            alert.setContentText("Delete restriction first. Restriction name: "+group.getRestriction().getName());
            alert.showAndWait();
            return;
        }
        FXMLLoader loader = new FXMLLoader(this.getClass().getResource(ADD_GROUP_VIEW_PATH));
        controllerUtils.openWindow(loader, "Edit group");
        addGroupController = loader.getController();
        addGroupController.setGroupsList(groupList);
        addGroupController.setGroupComboBox(groupComboBox);
        addGroupController.prepareEditScreen(groupDao.getByName(
                (String)groupComboBox.getSelectionModel().getSelectedItem()).get());
    };

    private EventHandler<ActionEvent> deleteGroupEvent = (e) -> {
        Group group = groupDao.getByName((String)groupComboBox.getSelectionModel().getSelectedItem()).get();
        if(group.getRestriction() != null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText("Group with restrictions cannot be deleted.");
            alert.setContentText("Delete restriction first. Restriction name: "+group.getRestriction().getName());
            alert.showAndWait();
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are you sure to remove this group?");
        alert.getButtonTypes().setAll(ButtonType.NO, ButtonType.YES);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            groupDao.delete(group);
            groupList.remove(group.getName());
            groupComboBox.getSelectionModel().select(null);
        }
    };

    private MyTime getTimeFromTextFields(TextField hours, TextField minutes) {
        if (hours.getText().equals("") && minutes.getText().equals("")) {
            return null;
        }
        int h = 0;
        int m = 0;
        if (!hours.getText().equals("")) {
            h = Integer.valueOf(hours.getText());
        }
        if (!minutes.getText().equals("")) {
            m = Integer.valueOf(minutes.getText());
        }
        return new MyTime(h, m);
    }

    private TextField getHourTextField() {
        TextField textField = new TextField();
        textField.setTextFormatter(getHourTextFormatter());
        return textField;
    }

    private TextField getMinuteTextField() {
        TextField textField = new TextField();
        textField.setTextFormatter(getMinuteTextFormatter());
        return textField;
    }

    private TextFormatter<Integer> getHourTextFormatter() {
        return new TextFormatter<>(change ->
                (change.getControlNewText().matches("((1[0-9])|(2[0-3])|[0-9])?")) ? change : null);
    }

    private TextFormatter<Integer> getMinuteTextFormatter() {
        return new TextFormatter<>(change ->
                (change.getControlNewText().matches("(([1-5][0-9])|[0-9])?")) ? change : null);
    }


    private void addGroupRestriction(){
        String groupName = (String)groupComboBox.getSelectionModel().getSelectedItem();
        String restrictionName = restrictionNameField.getText();
        Group group = groupDao.getByName(groupName).get();
        Restriction existingRestriction = group.getRestriction();
        if(cannotBeAdded(existingRestriction, restrictionName)) return;

        MyTime dailyLimit = getTimeFromTextFields(hoursDailyField, minutesDailyField);
        if (!isEditedProperty.get()) {
            Restriction restriction = buildRestriction(rangeRestrictions.values(), dailyLimit);
            restriction.setGroup(group);
            group.setRestriction(restriction);
            addRestriction(restriction, restrictionName);
        } else {
            updateRestriction(restrictionName, dailyLimit);
        }
        ((Stage) okButton.getScene().getWindow()).close();
    }

    private void addAppRestriction(){
        String applicationPath = applicationPathField.getText();
        String restrictionName = restrictionNameField.getText();
        Optional<Application> appOpt = applicationDao.getByPath(applicationPath);
        Application app = appOpt.orElseGet(() -> new Application(this.selectedApplicationName, applicationPath));
        Restriction existingRestriction = app.getRestriction();
        if(cannotBeAdded(existingRestriction, restrictionName)) return;

        MyTime dailyLimit = getTimeFromTextFields(hoursDailyField, minutesDailyField);
        if (!isEditedProperty.get()) {
            Restriction restriction = buildRestriction(rangeRestrictions.values(), dailyLimit);
            if (appOpt.isEmpty()) applicationDao.create(app);
            restriction.setApplication(app);
            app.setRestriction(restriction);
            addRestriction(restriction, restrictionName);
        } else {
            updateRestriction(restrictionName, dailyLimit);
        }
        ((Stage) okButton.getScene().getWindow()).close();
    }

    private boolean cannotBeAdded(Restriction restriction, String restrictionName){
        if (!isEditedProperty.get()) {
            if (restriction != null) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Restriction for this application/group already exists");
                alert.show();
                return true;
            }
            if (restrictionsListController.getRestrictionListView().getItems().contains(restrictionName)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setHeaderText("Restriction with given name already exists");
                alert.show();
                return true;
            }
        }
        return false;
    }

    private void addRestriction(Restriction restriction, String restrictionName){
        restrictionDao.create(restriction);
        restrictionsListController.getRestrictionListView().getItems().add(restrictionName);
    }

    private void updateRestriction(String restrictionName, MyTime dailyLimit){
        Restriction restriction = restrictionDao.getByName(restrictionName).get();
        restriction.setLimit(dailyLimit);
        restriction.setBlockedHours(new ArrayList<>(rangeRestrictions.values()));
        restrictionDao.update(restriction);
        restrictionsListController.refreshTab(restriction);
        isEditedProperty.setValue(false);
    }

    private Restriction buildRestriction(Collection<TimePair> rangeRestrictions, MyTime dailyLimit) {
        RestrictionBuilder restrictionBuilder = new RestrictionBuilder()
                .setName(restrictionNameField.getText());
        if (!rangeRestrictions.isEmpty()) {
            restrictionBuilder
                    .addBlockedHours(rangeRestrictions);
        }
        if (dailyLimit != null) {
            restrictionBuilder.setLimit(dailyLimit);
        }
        return restrictionBuilder.build();
    }

    private ChangeListener<String> getTimeTextFieldChangeListener(List<TextField> textFields) {
        return (observable, oldValue, newValue) -> {
            MyTime start = getTimeFromTextFields(textFields.get(0), textFields.get(1));
            MyTime end = getTimeFromTextFields(textFields.get(2), textFields.get(3));
            TimePair newTime = (start != null && end != null && end.isAfter(start)) ? new TimePair(start, end) : null;
            HBox box = (HBox) textFields.get(0).getParent();
            for (int i = 0; i < scrollBox.getChildren().size(); i++) {
                if (scrollBox.getChildren().get(i).equals(box)) {
                    if (newTime != null) rangeRestrictions.put(i, newTime);
                    else rangeRestrictions.remove(i);
                    break;
                }
            }
        };
    }
}
