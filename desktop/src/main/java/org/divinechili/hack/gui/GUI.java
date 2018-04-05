package org.divinechili.hack.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.divinechili.hack.FXAppWrapper;
import org.divinechili.hack.plugin.IPlugin;
import org.divinechili.hack.plugin.PluginPane;

import java.util.Iterator;

public class GUI  {

    @FXML public Button exitButton;
    @FXML public VBox PluginOptionsBox;

    private boolean playingMidi = false;

    @FXML
    public void initialize() {

        // Remove all items from OptionsBox
        if(PluginOptionsBox.getChildren().size() > 0)
            PluginOptionsBox.getChildren().remove(0, PluginOptionsBox.getChildren().size());

        // Create GUI for default options
        GuiFactory guiFactory = new GuiFactory() {
            @Override
            public Node constructGui() {
                VBox container = new VBox();
                container.setSpacing(3.5d);
                // Tick Function active option
                CheckBox box = new CheckBox("Tick function active: ");
                box.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
                box.selectedProperty().addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        System.out.println("Tick function is now " + (newValue? "enabled!" : "disabled!"));
                        FXAppWrapper.bTick.set(true);
                    }
                });
                HBox sub_con = new HBox();
                sub_con.setSpacing(5.0d);
                sub_con.setPrefWidth(Region.USE_COMPUTED_SIZE);
                sub_con.setPrefHeight(Region.USE_COMPUTED_SIZE);

                Button midiButton = new Button("Midi");
                midiButton.setOnAction(new EventHandler<ActionEvent>() {
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
                Button FireAlert = new Button("Fire Alert!");
                FireAlert.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        FXAppWrapper.appRef.showMessage("Alert fired!!!");
                    }
                });

                HBox.setHgrow(midiButton, Priority.ALWAYS);
                HBox.setHgrow(FireAlert, Priority.ALWAYS);

                midiButton.setMaxWidth(Double.MAX_VALUE);
                FireAlert.setMaxWidth(Double.MAX_VALUE);

                sub_con.getChildren().addAll(midiButton, FireAlert);
                container.getChildren().addAll(box, sub_con);
                return container;
            }
        };

        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });

        PluginOptionsBox.getChildren().add(new PluginPane("Default Hack Options", guiFactory.constructGui()));

        // Loop through and construct GUI options pane for
        Iterator iter = FXAppWrapper.plugins.iterator();
        while (iter.hasNext()) {
            IPlugin pf = (IPlugin) iter.next();
            try {

                PluginOptionsBox.getChildren().add(new PluginPane(pf.getPluginName() + " [" + pf.getPluginAuthor() + "]",
                        pf.getGuiFactory().constructGui()));

            } catch (SecurityException secEx) {
                System.err.println("plugin '"+pf.getClass().getName()+"' tried to do something illegal");
            }
        }

    }
}
