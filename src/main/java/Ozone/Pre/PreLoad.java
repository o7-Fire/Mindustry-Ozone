package Ozone.Pre;

import Atom.Manifest;
import Atom.Random;
import arc.Events;
import arc.backend.sdl.jni.SDL;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.EventType;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class PreLoad {
    public final static String AtomHash = "02051bd3ca";
    public static URLClassLoader atomicClassloader = null;
    public final static String AtomDownload = "https://jitpack.io/com/github/o7-Fire/Atomic-Library/Atomic/" + AtomHash + "/Atomic-" + AtomHash + ".jar";
    private static volatile boolean init = false;

    public static boolean init() {
        if (init) return true;
        init = true;
        try {
            File ozone = new File(PreLoad.class.getProtectionDomain().getCodeSource().getLocation().getFile());
            File parentFile = ozone.getParentFile();
            File atom = new File(parentFile, "Atomic-" + AtomHash + ".jar");
            if (!atom.exists()) {
                Log.infoTag("Ozone", "Downloading Library");
                SDL.SDL_ShowSimpleMessageBox(64, "Ozone", atom.getAbsolutePath() + " doesn't exists. Downloading library from: \n" + AtomDownload);
                URL jitpack = new URL(AtomDownload);
                ReadableByteChannel rbc = Channels.newChannel(jitpack.openStream());
                FileOutputStream fos = new FileOutputStream(atom);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            }
            try {
                if (!(PreLoad.class.getClassLoader() instanceof URLClassLoader))
                    throw new RuntimeException("Classloader is not URLClassloader");
                Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                method.setAccessible(true);
                method.invoke(PreLoad.class.getClassLoader(), atom.toURI().toURL());
                if (Manifest.checkIntegrity())
                    return true;

            } catch (Throwable t) {
                Log.err(t);
                Log.err("Using second strategy to load library");
            }

            try {
                atomicClassloader = new URLClassLoader(new URL[]{atom.toURI().toURL(), new URL(AtomDownload), ozone.toURI().toURL()});
                Class<?> scl = atomicClassloader.loadClass("Atom.Classloader.SystemURLClassLoader");
                Method sclm = scl.getDeclaredMethod("loadJar", URLClassLoader.class, File.class);
                Object sclo = scl.getDeclaredConstructor().newInstance();
                sclm.invoke(sclo, atomicClassloader, atom);
                Random.getRandomHexColor();
                if (Manifest.checkIntegrity()) return true;

            } catch (Throwable t) {
                Log.err("Failed to load Atomic Library");
                throw new RuntimeException(t);
            }
        } catch (Throwable t) {
            Log.err(t);
            Log.infoTag("Ozone", "Cant load ozone, aborting");
            Events.on(EventType.ClientLoadEvent.class, s -> {
                Vars.ui.showException("Failed to load ozone", t);
                Vars.ui.showInfoText("Failed to load ozone", "See log for more information");
            });
        }
        return false;
    }
}

