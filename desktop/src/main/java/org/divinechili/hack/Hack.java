package org.divinechili.hack;

import geogebra.GeoGebra3D;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.divinechili.hack.exceptions.IllegalGameLoadException;
import org.divinechili.hack.exceptions.PluginException;
import org.divinechili.hack.plugin.IGameController;
import org.divinechili.hack.plugin.IPlugin;
import org.divinechili.hack.variables.ObservableAtomicBoolean;
import org.geogebra.desktop.main.AppD;
import org.geogebra.desktop.sound.MidiSoundD;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Hack extends Application
{

    public static Stage ProgramWrapper = null;
    public static AppD appRef = null;
    public static Thread gamepadThread = null;
    public static MidiSoundD midiSequencer = null;

    public static final String pluginsDir = "plugins";
    public static List<IPlugin> plugins = new ArrayList<>();
    public static List<String> loadedPlugins = new ArrayList<>();

    public static List<IGameController> games = new ArrayList<>();
    public static IGameController gameController = null;

    public static final ObservableAtomicBoolean bControllerConnected = new ObservableAtomicBoolean();
    public static AtomicBoolean bTick = new AtomicBoolean(false);

    private boolean playingMidi = false;

    public static void main(String... args) {
        try {
            getPlugins();
        } catch (PluginException e) {
            if(e instanceof IllegalGameLoadException) {
                System.out.println("Multiple games loaded!\nUsing first game loaded!");
                Hack.gameController = games.get(0);
                System.out.println(games.get(0).getName() + " loaded!");
            }
        } finally {
            Hack.gameController = games.get(0);
            if(Hack.gameController != null) System.out.println(gameController.getName() + " loaded!");
            games.clear();
        }
        // preInit
        for(IPlugin plugin: Hack.plugins)
            plugin.preInitialization();

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("GeoGebra Hack!");
        System.out.println("JavaFX initialization phase started successfully!");
        System.out.println("Working directory: " + new File("./").getCanonicalPath());
        Hack.ProgramWrapper = primaryStage;
        bControllerConnected.setValue(false);

        // Start geogebra
        EventQueue.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                GeoGebra3D.main(null);
            }
        });

        // Initialize plugins after geogebra is loaded
        for (IPlugin pf : Hack.plugins) {
            try {
                pf.postInitialization();
            } catch (SecurityException secEx) {
                System.out.println("A plugin tried to do something illegal during init!");
            }
        }


        if(bControllerConnected.get()) bControllerConnected.setValue(true);
        midiSequencer = new MidiSoundD(appRef);
        midiSequencer.initialize();

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                event.consume();
            }
        });

        Parent fxml = FXMLLoader.load(getClass().getResource("/wrapperGUI.fxml"));
        primaryStage.setScene(new Scene(fxml));
        primaryStage.show();
    }

    private static void getPlugins() throws PluginException {
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

                    System.out.println("Loading : " + pluginPkg.getName());

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

                        System.out.println("Loading class: " + c.getName());

                        Class[] intf = c.getInterfaces();
                        for (Class anIntf : intf) {
                            if (anIntf.getName().equals("org.divinechili.hack.plugin.IPlugin")) {
                                // the following line assumes that IPlugin has a no-argument constructor
                                IPlugin pf = (IPlugin) c.newInstance();
                                if(!loadedPlugins.contains(pf.getPluginName())) {
                                    plugins.add(pf);
                                    loadedPlugins.add(pf.getPluginName());
                                }
                            }
                            if (anIntf.getName().equals("org.divinechili.hack.plugin.IGameController")) {
                                // The following line assumes that IGameController has a no-argument constructor
                                IGameController controller = (IGameController) c.newInstance();
                                System.out.println("Found a game!");
                                games.add(controller);
                            }
                        }

                    }
                } catch (Exception ex) {
                    System.err.println("File " + file + " does not contain a valid IPlugin class.");
                } finally {
                    if(games.size() > 1) {
                        throw new IllegalGameLoadException("Tried to load multiple gamefiles");
                    }
                }
                System.out.println("\n");
            }
        }
    }
}
