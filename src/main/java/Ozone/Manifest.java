package Ozone;

import Atom.Annotation.ObfuscatorEntryPoint;
import Ozone.UI.CommandsListFrag;
import Ozone.UI.OzoneMenu;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

@ObfuscatorEntryPoint
public class Manifest {
    public final static String atomHash = "0935b89124";
    public final static String atomFile = "Atomic-" + atomHash + ".jar", libs = "libs";
    public final static String atomDownloadLink = "https://jitpack.io/com/github/o7-Fire/Atomic-Library/" + atomHash + "/Atomic-Library-" + atomHash + ".jar";
    public static OzoneMenu menu;
    public static CommandsListFrag commFrag;
    public static String currentServer = "";
    public static ArrayList<Class<?>> settings = new ArrayList<>();

    public static ArrayList<Field> getSettings() {
        ArrayList<Field> f = new ArrayList<>();
        //must static class
        for (Class<?> c : settings)
            f.addAll(Arrays.asList(c.getDeclaredFields()));
        return f;
    }
}
