package org.divinechili.hack.plugin;

import java.io.*;
import java.net.URLClassLoader;

public class PluginClassLoader extends ClassLoader {
    /** This is the directory from which the classes will be loaded */
    File directory;

    /** The constructor. Just initialize the directory */
    public PluginClassLoader (File dir) {
        directory = dir;
    }

    /** A convenience method that calls the 2-argument form of this method */
    public Class loadClass (String name) throws ClassNotFoundException {
        return loadClass(name, true);
    }

    public Class loadClass (String classname, boolean resolve) throws ClassNotFoundException {
        try {
            Class c = findLoadedClass(classname);

            if (c == null) {
                try { c = findSystemClass(classname); }
                catch (Exception ex) {}
            }

            if (c == null) {
                // Figure out the filename
                String filename = classname.replace('.',File.separatorChar)+".class";

                // Create a File object. Interpret the filename relative to the
                // directory specified for this ClassLoader.
                File f = new File(directory, filename);

                // Get the length of the class file, allocate an array of bytes for it, and read it all
                int length = (int) f.length();
                byte[] classbytes = new byte[length];
                DataInputStream in = new DataInputStream(new FileInputStream(f));
                in.readFully(classbytes);
                in.close();

                // Call an inherited method to convert those bytes into a Class
                c = defineClass(classname, classbytes, 0, length);
            }

            // If the resolve argument is true, call the inherited resolveClass method.
            if (resolve) resolveClass(c);

            return c;
        }
        // throw a ClassNotFoundException error if anything goes wrong
        catch (Exception ex) { throw new ClassNotFoundException(ex.toString()); }
    }
}
