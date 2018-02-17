package org.divinechili.hack;

import geogebra.GeoGebra3D;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
    public static MidiSoundD midiSequencer = null;
    public static BooleanProperty bControllerConnected;

    private boolean playingMidi = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("GeoGebra Hack!");
        System.out.println("JavaFX initialization phase started successfully!");
        FXAppWrapper.ProgramWrapper = primaryStage;
        bControllerConnected = new SimpleBooleanProperty(true);

        // Start geogebra
        EventQueue.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                GeoGebra3D.main(null);
            }
        });
        midiSequencer = new MidiSoundD(appRef);
        midiSequencer.initialize();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });

        Parent fxml = FXMLLoader.load(getClass().getResource("/wrapperGUI.fxml"));
        primaryStage.setScene(new Scene(fxml));
        primaryStage.show();
    }
}
