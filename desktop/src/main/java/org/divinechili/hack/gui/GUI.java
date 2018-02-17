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
import javafx.scene.text.Font;
import org.divinechili.hack.FXAppWrapper;

public class GUI  {
    @FXML
    public Button testMidi;
    @FXML
    public Button testAlert;
    @FXML
    public Label controllerStatus;

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
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if((Boolean)newValue) {
                    controllerStatus.setStyle("color: #00FF00");
                    controllerStatus.setText("Connected!");
                } else {
                    controllerStatus.setStyle("color: #FF0000");
                    controllerStatus.setText("Disconnected!");
                }
            }
        });
        /*
        FXAppWrapper.bControllerConnected.addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if(newValue) {
                    controllerStatus.setStyle("color: #00FF00");
                    controllerStatus.setText("Connected!");
                } else {
                    controllerStatus.setStyle("color: #FF0000");
                    controllerStatus.setText("Disconnected!");
                }
            }
        });
        */
    }
}
