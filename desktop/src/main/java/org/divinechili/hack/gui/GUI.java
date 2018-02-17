package org.divinechili.hack.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import org.divinechili.hack.FXAppWrapper;

public class GUI  {

    @FXML public Button testMidi;
    @FXML public Button testAlert;
    @FXML public Label controllerStatus;

    @FXML public GridPane joystickView;
    @FXML public Label controllerX;
    @FXML public Label controllerY;
    @FXML public Label controllerX2;
    @FXML public Label controllerY2;

    private boolean playingMidi = false;

    @FXML
    public void initialize() {

        testAlert.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FXAppWrapper.appRef.showMessage("Alert fired!");
            }
        });
        testMidi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                playingMidi = !playingMidi;
                if(playingMidi) {
                    FXAppWrapper.midiSequencer.playMidiFile("");
                } else if (!playingMidi) {
                    FXAppWrapper.midiSequencer.stop();
                }
            }
        });

        FXAppWrapper.bControllerConnected.addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) { if((Boolean)newValue) {
                    controllerStatus.setTextFill(Color.GREEN);
                    controllerStatus.setText("Connected!");
                    joystickView.setVisible(true);
                } else {
                    joystickView.setVisible(false);
                    controllerStatus.setTextFill(Color.RED);
                    controllerStatus.setText("Disconnected!");
                }
            }
        });
    }
}
