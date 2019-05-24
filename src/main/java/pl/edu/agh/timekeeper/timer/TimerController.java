package pl.edu.agh.timekeeper.timer;

public class TimerController {
    TimerView timerView;
    public TimerController(){
        timerView = new TimerView("0:0", 100, 50, 50.0, 50.0);
    }

    public void change(String text){
        timerView.setText(text);
    }
}
