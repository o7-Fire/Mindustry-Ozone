package Main;

import Ozone.Main;
import arc.Events;
import arc.backend.sdl.jni.SDL;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.mod.Mod;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Ozone extends Mod {


    public final static String AtomHash = "02051bd3ca";
    public final static String AtomDownload = "https://jitpack.io/com/github/o7-Fire/Atomic-Library/Atomic/" + AtomHash + "/Atomic-" + AtomHash + ".jar";
    private static volatile boolean init = false;

    @Override
    public void init() {
        if (incites())
            Main.init();
    }

    @Override
    public void loadContent() {
        if (incites())
            Main.loadContent();
    }

    public boolean incites() {
        if (init) return true;
        init = true;
        try {
            File ozone = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().getFile());
            File parentFile = ozone.getParentFile();
            File atom = new File(parentFile, "Atomic-" + AtomHash + ".jar");
            Log.infoTag("Ozone", "Loading library");
            if (!atom.exists()) {
                Log.infoTag("Ozone", "Downloading Library");
                SDL.SDL_ShowSimpleMessageBox(64, "Ozone", atom.getAbsolutePath() + " doesn't exists. Downloading library");
                URL jitpack = new URL(AtomDownload);
                ReadableByteChannel rbc = Channels.newChannel(jitpack.openStream());
                FileOutputStream fos = new FileOutputStream(atom);
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                if (atom.exists()) {
                    SDL.SDL_ShowSimpleMessageBox(64, "Ozone", "You need to restart mindustry");
                    System.exit(0);
                } else {
                    SDL.SDL_ShowSimpleMessageBox(64, "Ozone", "Atom library is being assembled at cloud, please wait and restart mindustry");
                }
            }
            Log.infoTag("Ozone", "Loading library");
            if (!(this.getClass().getClassLoader() instanceof URLClassLoader))
                throw new RuntimeException("Classloader is not URLClassloader, how it could be ???");
            Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(this.getClass().getClassLoader(), atom.toURI().toURL());
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
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
