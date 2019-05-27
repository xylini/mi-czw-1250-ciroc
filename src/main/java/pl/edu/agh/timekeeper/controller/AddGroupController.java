package pl.edu.agh.timekeeper.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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

    private ObservableList<String> appPathList = FXCollections.observableArrayList();

    private Set<Application> applicationsSet = new HashSet<>();

    private void makeBrowseButton() {
        this.browseButton = new Button("Add application");
        browseButton.setOnAction(this::browseClicked);
    }

    @FXML
    private void initialize() {
        makeBrowseButton();
        listAppVBox.getChildren().add(browseButton);

    }

    private void browseClicked(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select file to impose a restriction");
        Stage stage = (Stage) mainPane.getScene().getWindow();
        List<File> list = fileChooser.showOpenMultipleDialog(stage);
        if (list != null)
            for (File file : list) {
                listAppVBox.getChildren().add(makeHBox(file.getAbsolutePath()));
            }
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
        ApplicationDao applicationDao = new ApplicationDao();
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
        GroupDao groupDao = new GroupDao();
        groupDao.create(group);
        ((Stage) okButton.getScene().getWindow()).close();
    }

    private HBox makeHBox(String path) {
        HBox hBox = new HBox();
        TextField nameApp = new TextField(path);
        nameApp.setPrefWidth(250);
        hBox.getChildren().addAll(
                nameApp,
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
}
