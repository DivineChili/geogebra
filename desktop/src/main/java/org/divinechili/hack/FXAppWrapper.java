package org.divinechili.hack;

import com.sun.org.apache.xpath.internal.operations.Bool;
import geogebra.GeoGebra3D;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableIntegerValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.divinechili.hack.variables.ObservableAtomicBoolean;
import org.divinechili.hack.variables.ObservableAtomicInteger;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.sound.MidiSoundD;

import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FXAppWrapper extends Application
{
    public static void main(String... args) { launch(args); }
    public static Stage ProgramWrapper = null;
    public static AppD appRef = null;
    public static Thread gamepadThread = null;
    public static MidiSoundD midiSequencer = null;

    public static final ObservableAtomicBoolean bControllerConnected = new ObservableAtomicBoolean();
    public static AtomicBoolean bTick = new AtomicBoolean(false);

    private boolean playingMidi = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("GeoGebra Hack!");
        System.out.println("JavaFX initialization phase started successfully!");
        FXAppWrapper.ProgramWrapper = primaryStage;
        bControllerConnected.setValue(false);

        // Start geogebra
        EventQueue.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                GeoGebra3D.main(null);
            }
        });
        if(bControllerConnected.get()) bControllerConnected.setValue(true);
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
