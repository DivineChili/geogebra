package org.geogebra.desktop.divinechili;

import geogebra.GeoGebra3D;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.sound.MidiSoundD;

import java.awt.*;

public class FXAppWrapper extends Application
{
    public static void main(String... args) { launch(args); }
    public static Stage ProgramWrapper = null;
    public static AppD appRef = null;
    public static Thread gamepadThread = null;

    private boolean playingMidi = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("JavaFX initialization phase started successfully!");
        FXAppWrapper.ProgramWrapper = primaryStage;
        // Start geogebra
        EventQueue.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                GeoGebra3D.main(null);
            }
        });

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
                System.exit(0);
            }
        });

        HBox panel = new HBox();
        final TextField midiFile = new TextField();
        midiFile.setPromptText("Midi file...");
        final Button playMidi = new Button(new String("►"));
        panel.getChildren().addAll(midiFile,playMidi);

        final MidiSoundD sequencer = new MidiSoundD(appRef);
        sequencer.initialize();
        playMidi.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                playingMidi = !playingMidi;
                if(playingMidi) {
                    sequencer.playMidiFile(midiFile.getText());
                } else if (!playingMidi) {
                    sequencer.stop();
                }
            }
        });

        primaryStage.setScene(new Scene(panel));
        primaryStage.show();
    }
}
