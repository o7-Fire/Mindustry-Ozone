package Main;

import Ozone.Main;
import Ozone.Pre.Preload;
import arc.Core;
import arc.Events;
import arc.util.Log;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.mod.Mod;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.net.URLClassLoader;

public class Ozone extends Mod {
    public final static String AtomHash = "a2961d4c26", atomFile = "Atomic-" + AtomHash + ".jar", libs = "libs";
    public final static String AtomDownload = "https://jitpack.io/com/github/o7-Fire/Atomic-Library/Atomic/" + AtomHash + "/Atomic-" + AtomHash + ".jar";
    public boolean libraryURLLoaded;
    public boolean libraryExists;
    public URLClassLoader classloader;
    public Mod mainMod = null;
    //get location of this Ozone mods
    public static File ozone = new File(Ozone.class.getProtectionDomain().getCodeSource().getLocation().getFile());
    //get Mods directory from mods/Ozone.jar
    public static File parentFile = ozone.getParentFile();
    //mods/libs directory
    public static File library = new File(parentFile, libs);
    //mods/libs/Atomic-AtomHash.jar
    public static File atom = new File(library, atomFile);

    public Ozone() {
        //gay spy, actually no
        if (Core.settings != null) {
            Core.settings.put("crashreport", false);
            Core.settings.manualSave();
        }

        //just in case
        library.mkdirs();
        try {
            //Check library
            Preload.incites(atom, AtomDownload, this);
            //no error thrown ? good
            libraryURLLoaded = true;
        } catch (FileNotFoundException t) {
            libraryURLLoaded = false;
            Log.err("Cant download Atom library: " + t.toString());
        } catch (Throwable t) {
            //Exception just summoned
            libraryURLLoaded = false;
            t.printStackTrace();
            //this shit ain't reliable
            Log.err(t);
            Log.infoTag("Ozone", "Cant load Atom library, try using method 2");
        }
        //we already download it or not ?
        libraryExists = atom.exists();
        //oh already loaded ? no need to continue
        if (libraryURLLoaded) return;
        try {
            //oh we already download it, but its not yet loaded
            if (libraryExists) {
                //make new classloader
                classloader = new URLClassLoader(new URL[]{ozone.toURI().toURL(), atom.toURI().toURL()}, ClassLoader.getSystemClassLoader());
                Class<?> main = classloader.loadClass("MainBackup.OzoneBackup");
                mainMod = (Mod) main.getDeclaredConstructor().newInstance();
                //oh we did it nice
                return;
            } else {
                //wtf man network error ? can't download ? library removed ?
            }
        } catch (Throwable t) {
            //gabe itch
            Log.err(t);
        }
        //sike inform users
        Events.on(EventType.ClientLoadEvent.class, s -> Vars.ui.showInfoText("Failed to load ozone", "See log for more information"));

    }

    @Override
    public void init() {
        //Library loaded properly ?
        if (libraryURLLoaded)
            Main.init();
        else if (libraryExists && mainMod != null) {//hmmm seem to use method 2
            //alternative
            mainMod.init();
        }
    }

    //same as above
    @Override
    public void loadContent() {
        if (libraryURLLoaded)
            Main.loadContent();
        else if (libraryExists && mainMod != null) {
            mainMod.init();
        }
    }


}
