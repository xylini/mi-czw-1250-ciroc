package pl.edu.agh.timekeeper.timer;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pl.edu.agh.timekeeper.model.Application;

public class TimerView extends Application {
    private Stage timerStage;// = new Stage();
    private Text text;// = new Text("0:00");
    private BorderPane borderPane;// = new BorderPane();

    public TimerView(String startText, Integer width, Integer height, Double initial_x, Double initial_y){
        this.text = new Text(startText);
        this.borderPane = new BorderPane(this.text);
        this.borderPane.setCenter(text);
        this.timerStage = new Stage();
        this.timerStage.initStyle(StageStyle.UNDECORATED);
        this.timerStage.setAlwaysOnTop(true);
        this.timerStage.setScene(new Scene(borderPane, width, height));
        this.timerStage.setX(initial_x);
        this.timerStage.setY(initial_y);
        this.timerStage.show();
    }

    public void setText(String text){
        this.text.setText(text);
    }

    public void setCoordinates(Double x, Double y){
        this.timerStage.setX(x);
        this.timerStage.setY(y);
    }
}
