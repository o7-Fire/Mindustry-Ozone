package Main;

import Ozone.Main;
import arc.Core;
import arc.util.Log;
import mindustry.mod.Mod;

import java.io.File;

/**
 * @author Itzbenz
 */
public class Ozone extends Mod {

    public Ozone() {
        for (File f : Atom.Manifest.getLibs()) {
            if (f.exists())
                try {
                    Premain.EntryPoint.libraryLoader.addURL(f);
                } catch (Throwable e) {
                    Log.errTag("Ozone-PreInit", "Can't load: " + f.getAbsolutePath() + "\n" + e.toString());
                }
        }
        //gay spy
        //legit 100%
        if (Core.settings != null) {
            Core.settings.put("crashreport", false);
            Core.settings.put("uiscalechanged", false);//shut
        }
    }

    @Override
    public void init() {
        Main.init();
    }

    @Override
    public void loadContent() {
        Main.loadContent();
    }

    /*
    public Ozone() {


        //just in case
        library.mkdirs();
        try {
            //Check library
            Preload.incites(atom, Manifest.atomDownloadLink, this);
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

    }



    @Override
    public void init() {
        //Library loaded properly ?
        if (libraryURLLoaded) {
            DesktopPatcher.register();
            Main.init();
        } else if (libraryExists && mainMod != null) {//hmmm seem to use method 2
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

    */
}
