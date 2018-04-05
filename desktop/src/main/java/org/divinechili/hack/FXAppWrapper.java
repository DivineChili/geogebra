package org.divinechili.hack;

import geogebra.GeoGebra3D;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.divinechili.hack.plugin.IPlugin;
import org.divinechili.hack.plugin.PluginClassLoader;
import org.divinechili.hack.variables.ObservableAtomicBoolean;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.sound.MidiSoundD;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class FXAppWrapper extends Application
{

    public static Stage ProgramWrapper = null;
    public static AppD appRef = null;
    public static Thread gamepadThread = null;
    public static MidiSoundD midiSequencer = null;

    public static final String pluginsDir = "plugins";
    public static List<IPlugin> plugins = new ArrayList<IPlugin>();

    public static final ObservableAtomicBoolean bControllerConnected = new ObservableAtomicBoolean();
    public static AtomicBoolean bTick = new AtomicBoolean(false);

    private boolean playingMidi = false;

    public static void main(String... args) {
        getPlugins();
        System.out.println("Plugins loaded: " + plugins.size());
        for(int i = 0; i < plugins.size(); i++) {
            System.out.println("Plugin " + i);
            System.out.println("Name:\t" + plugins.get(i).getPluginName());
            System.out.println("Author:\t" + plugins.get(i).getPluginAuthor() + "\n");
        }
        launch(args);
    }

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

    private static void getPlugins() {
        File dir = new File(System.getProperty("user.dir") + File.separator + pluginsDir);

        if (dir.exists() && dir.isDirectory()) {
            // we'll only load classes directly in this directory -
            // no subdirectories, and no classes in packages are recognized
            File[] files = dir.listFiles();
            for (File file : files) {
                try {
                    // only consider files ending in ".class"
                    if (!file.getName().endsWith(".jar"))
                        continue;

                    JarFile pluginPkg = new JarFile(file.getCanonicalPath());
                    Enumeration<JarEntry> e = pluginPkg.entries();

                    System.out.println(pluginPkg.getName() + " is about to get loaded!");

                    URL[] urls = { new URL("jar:file:" + file.getCanonicalPath()+"!/") };
                    URLClassLoader cl = URLClassLoader.newInstance(urls);

                    // Loop through all classes in jar
                    while (e.hasMoreElements()) {
                        JarEntry je = e.nextElement();
                        if(je.isDirectory() || !je.getName().endsWith(".class")){
                            continue;
                        }

                        // -6 because of .class
                        String className = je.getName().substring(0,je.getName().length()-6);
                        className = className.replace('/', '.');

                        Class c = cl.loadClass(className);
                        Class[] intf = c.getInterfaces();
                        for(Class anIntf : intf) System.out.printf(" - " + anIntf.getName());
                        for (Class anIntf : intf) {
                            if (anIntf.getName().equals("org.divinechili.hack.plugin.IPlugin")) {
                                System.out.println(c.getName() + " contains IPlugin!");
                                // the following line assumes that IPlugin has a no-argument constructor
                                IPlugin pf = (IPlugin) c.newInstance();
                                plugins.add(pf);
                            }
                        }

                    }
                } catch (Exception ex) {
                    System.err.println("File " + file + " does not contain a valid IPlugin class.");
                }
            }
        }
    }
}
