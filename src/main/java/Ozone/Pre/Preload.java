package Ozone.Pre;

import arc.Events;
import arc.backend.sdl.jni.SDL;
import arc.util.Log;
import mindustry.Vars;
import mindustry.desktop.DesktopLauncher;
import mindustry.game.EventType;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;

//have you load library today ?
public class Preload {
    private static volatile boolean init = false;

    public static void restart() {
        try {
            final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
            final File currentJar = new File(DesktopLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI());

            /* is it a jar file? */
            if (!currentJar.getName().endsWith(".jar"))
                throw new RuntimeException(currentJar.getAbsolutePath() + " is not a jar");

            /* Build command: java -jar application.jar */
            final ArrayList<String> command = new ArrayList<>();
            command.add(javaBin);
            command.add("-jar");
            command.add(currentJar.getPath());

            final ProcessBuilder builder = new ProcessBuilder(command);
            builder.start();
        } catch (Throwable ignored) {
        }
        System.exit(0);
    }

    public static boolean incites(String AtomHash, String AtomDownload, Object clz) {
        if (init) return true;
        init = true;
        try {
            if (!(clz.getClass().getClassLoader() instanceof URLClassLoader))
                throw new RuntimeException(clz + " Classloader is not URLClassloader, how it could be ???");
            File ozone = new File(clz.getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
            File parentFile = ozone.getParentFile();
            File library = new File(parentFile, "Library");
            library.mkdirs();
            File atom = new File(library, "Atomic-" + AtomHash + ".jar");
            if (!atom.exists()) {
                Log.infoTag("Ozone", "Downloading Library");
                SDL.SDL_ShowSimpleMessageBox(64, "Ozone", atom.getAbsolutePath() + " doesn't exists. Downloading library (7 MB), click OK to continue");
                //how to download a file synchronously
                URL jitpack = new URL(AtomDownload);
                ReadableByteChannel rbc = Channels.newChannel(jitpack.openStream());
                FileOutputStream fos = new FileOutputStream(atom);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                //done lol
                //godless Channels and FileOutputStream
                if (atom.exists()) {
                    SDL.SDL_ShowSimpleMessageBox(64, "Ozone", "You need to restart mindustry");
                    restart();
                } else {
                    SDL.SDL_ShowSimpleMessageBox(64, "Ozone", "Atom library is being assembled at cloud, please wait and restart mindustry");
                }
            }
            Log.infoTag("Ozone", "Loading library");
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(clz.getClass().getClassLoader(), atom.toURI().toURL());
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
            Log.err(t);
            Log.infoTag("Ozone", "Cant load Atom library, aborting");
            Events.on(EventType.ClientLoadEvent.class, s -> {
                Vars.ui.showInfoText("Failed to load ozone", "See log for more information");
                Vars.ui.showException("Failed to load ozone", t);
            });
        }
        return false;
    }
}
