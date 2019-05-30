package pl.edu.agh.timekeeper.controller;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import pl.edu.agh.timekeeper.db.dao.ApplicationDao;
import pl.edu.agh.timekeeper.db.dao.GroupDao;
import pl.edu.agh.timekeeper.model.Application;
import pl.edu.agh.timekeeper.model.Group;

import java.io.File;
import java.util.*;

public class AddGroupController {

    private static final String IMAGE_DELETE_PATH = "images/delete.png";

    @FXML
    public Pane mainPane;

    @FXML
    public TextField groupNameField;

    @FXML
    public VBox groupVBox;

    @FXML
    public Button okButton;

    @FXML
    public VBox listAppVBox;

    private Button browseButton;

    private ControllerUtils controllerUtils = new ControllerUtils();

    private Map<String, String> appDict = new HashMap<>();

    private Set<Application> applicationsSet = new HashSet<>();

    private GroupDao groupDao = new GroupDao();

    private List<Group> allGroups = groupDao.getAll();

    private ObservableList<String> groups;

    private final ApplicationDao applicationDao = new ApplicationDao();

    private void makeBrowseButton() {
        this.browseButton = new Button("Add application");
        browseButton.setOnAction(this::browseClicked);
    }

    @FXML
    private void initialize() {
        makeBrowseButton();
        listAppVBox.getChildren().add(browseButton);
        groupNameField.textProperty().addListener(getGroupNameListener());
        okButton.setDisable(true);
    }

    private ChangeListener<String> getGroupNameListener() {
        return (observable, oldValue, newValue) -> {
            if (allGroups.stream().anyMatch(g -> newValue.equals(g.getName()))) {
                groupNameField.setStyle("-fx-background-color: #ff5464; -fx-tooltip-visible: true;");
                groupNameField.setTooltip(new Tooltip("Name is already used"));
                okButton.setDisable(true);
            } else if (newValue.isEmpty()) {
                groupNameField.setStyle("-fx-background-color: #ff5464; -fx-tooltip-visible: true;");
                groupNameField.setTooltip(new Tooltip("Name cannot be empty"));
                okButton.setDisable(true);
            } else {
                groupNameField.setStyle("");
                groupNameField.setTooltip(new Tooltip("Name is valid"));
                okButton.setDisable(false);
            }
        };
    }

    private void browseClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file to impose a restriction");
        Stage stage = (Stage) mainPane.getScene().getWindow();
        Optional<List<File>> list = Optional.ofNullable(fileChooser.showOpenMultipleDialog(stage));
        list.ifPresent(files -> files.forEach(
                file -> listAppVBox.getChildren().add(makeHBox(file.getAbsolutePath()))
        ));
    }

    public void okClicked(ActionEvent actionEvent) {
        for (Object child : listAppVBox.getChildren()) {
            if (child instanceof HBox)
                for (Object grandchild : ((HBox) child).getChildren())
                    if (grandchild instanceof TextField) {
                        String path = ((TextField) grandchild).getText();
                        File file = new File(path);
                        String name = file.getName();
                        name = name.substring(0, name.lastIndexOf("."));
                        appDict.put(path, name);
                    }
        }
        for (Map.Entry entry : appDict.entrySet()) {
            Optional<Application> app = applicationDao.getByPath((String) entry.getKey());
            if (app.isEmpty()) {
                Application application = new Application((String) entry.getValue(), (String) entry.getKey());
                applicationDao.create(application);
                applicationsSet.add(application);
            } else
                applicationsSet.add(app.get());
        }
        Group group = new Group(groupNameField.getText(), applicationsSet);
        groupDao.create(group);
        groups.add(group.getName());
        ((Stage) okButton.getScene().getWindow()).close();
    }

    private HBox makeHBox(String path) {
        HBox hBox = new HBox();
        TextField appPath = new TextField(path);
        appPath.setPrefWidth(250);
        hBox.getChildren().addAll(
                appPath,
                controllerUtils.createButton(IMAGE_DELETE_PATH, deleteEvent)
        );
        return hBox;
    }

    private EventHandler<MouseEvent> deleteEvent = new EventHandler<>() {
        @Override
        public void handle(final MouseEvent ME) {
            Object button = ME.getSource();
            if (button instanceof Button) {
                HBox parent = (HBox) ((Button) button).getParent();
                listAppVBox.getChildren().remove(parent);
            }
        }
    };

    public void setGroupsList(ObservableList<String> groupsList) {
        this.groups = groupsList;
    }
}
