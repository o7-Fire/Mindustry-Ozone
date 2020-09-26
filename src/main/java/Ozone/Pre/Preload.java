package Ozone.Pre;

import Ozone.Swing.SPreLoad;
import arc.backend.sdl.jni.SDL;
import arc.util.Log;
import mindustry.desktop.DesktopLauncher;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

//have you load library today ?
public class Preload {
    private static volatile boolean init = false;

    public static void restart() {
        SDL.SDL_ShowSimpleMessageBox(64, "Ozone", "You need to restart mindustry");
        try {
            //get JRE or something
            final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            //get Mindustry Jar
            final File currentJar = new File(DesktopLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            //it is a jar ?
            if (!currentJar.getName().endsWith(".jar"))
                throw new RuntimeException(currentJar.getAbsolutePath() + " is not a jar");

            //java -jar path/to/Mindustry.jar
            final ArrayList<String> command = new ArrayList<>();
            command.add(javaBin);
            command.add("-jar");
            command.add(currentJar.getPath());

            final ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
        } catch (Throwable ignored) {
        }
        //exits is priority
        System.exit(0);
    }

    public static boolean checkLibrary(String AtomDownload, File atom) {
        //try to download if doesn't exists
        if (!atom.exists())
            try {
                //Inform user
                Log.infoTag("Ozone", "Downloading Library");
                //there is no "no" option :3
                SDL.SDL_ShowSimpleMessageBox(64, "Ozone", atom.getAbsolutePath() + " doesn't exists. Downloading library (7 MB), click OK to continue");
                //how to download a file synchronously
                URL jitpack = new URL(AtomDownload);
                Download download = new Download(jitpack, atom);
                SPreLoad s = new SPreLoad();
                new Thread(() -> {
                    try {
                        while (download.getSize() == -1) Thread.sleep(10);
                        s.progressBar1.setMaximum(download.getSize());
                        while (download.getStatus() != Download.DOWNLOADING) Thread.sleep(10);
                        s.frame1.setVisible(true);
                        s.label1.setText("Downloading: " + AtomDownload);
                        s.frame1.pack();
                        while (download.getStatus() == Download.DOWNLOADING) {
                            Thread.sleep(10);
                            s.progressBar1.setValue(download.downloaded.get());
                        }
                        s.frame1.setVisible(false);
                    } catch (Throwable t) {
                    }
                });
                download.run();
                //its exists
                if (atom.exists()) {
                    SDL.SDL_ShowSimpleMessageBox(64, "Ozone", "Atom library has been downloaded: " + atom.getAbsolutePath());
                    //Q: Why restart ???
                    //A: its just cool to restart
                    restart();
                }
                //if its reach to here, then its must not exists and there no internet connection ? wtf
            } catch (Throwable t) {
                //oh no internet error
                SDL.SDL_ShowSimpleMessageBox(64, "Ozone", "Atom library can't be downloaded: " + t.toString());
                Log.err(t);
            }
        //reliable shit
        return atom.exists();
    }

    public static void incites(File atom, String AtomDownload, Object clz) throws NoSuchMethodException, MalformedURLException, InvocationTargetException, IllegalAccessException, FileNotFoundException {
        //don't run more than once
        if (init) return;
        init = true;
        //check if we are on right classloader, sometime java is full of surprise
        if (!(clz.getClass().getClassLoader() instanceof URLClassLoader))
            throw new RuntimeException(clz + " Classloader is not URLClassloader, how it could be ???");
        //check library needed to load Ozone
        if (!checkLibrary(AtomDownload, atom))
            throw new FileNotFoundException("Atom Library can't be downloaded/not found");
        //Inform users
        Log.infoTag("Ozone", "Loading library");
        //add Atom to URL classloader to be used
        //good ol reflection
        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
        method.setAccessible(true);
        method.invoke(clz.getClass().getClassLoader(), atom.toURI().toURL());
        //shit we did it without any error
    }
}
