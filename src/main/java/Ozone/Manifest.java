package Ozone;

import Atom.Reflect.Reflect;
import Ozone.UI.CommandsListFrag;
import Ozone.UI.OzoneMenu;
import arc.net.Client;
import mindustry.Vars;
import mindustry.net.ArcNetProvider;
import mindustry.net.Net;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class Manifest {
    public static final String atomHash = "fe1b50cdb9";
    public static final String atomFile = "Atomic-" + atomHash + ".jar", libs = "libs";
    public static final String jitpack = "https://jitpack.io/com/github/o7-Fire/Atomic-Library/";
    public static String atomType = "Atomic";
    public static final String atomDownloadLink = "https://jitpack.io/com/github/o7-Fire/Atomic-Library/" + Manifest.atomType + "/" + Manifest.atomHash + "/" + Manifest.atomType + "-" + Manifest.atomHash + ".jar";
    public static OzoneMenu menu;
    public static CommandsListFrag commFrag;
    public static ArrayList<Class<?>> settings = new ArrayList<>();
    public static String lastServer = "";
    public static final String date = new Date().toString();

    public static String getCurrentServerIP() {
        if (!Vars.net.active()) return lastServer;
        try {
            Net.NetProvider n = Reflect.getField(Vars.net.getClass(), "provider", Vars.net);
            if (!(n instanceof ArcNetProvider)) return null;
            ArcNetProvider arc = (ArcNetProvider) n;
            Client c = Reflect.getField(arc.getClass(), "client", arc);
            return c.getRemoteAddressTCP().getHostName();
        } catch (Throwable ignored) {

        }
        return lastServer;

    }

    public static ArrayList<Field> getSettings() {
        ArrayList<Field> f = new ArrayList<>();
        //must static class
        for (Class<?> c : settings)
            f.addAll(Arrays.asList(c.getDeclaredFields()));
        return f;
    }
}
