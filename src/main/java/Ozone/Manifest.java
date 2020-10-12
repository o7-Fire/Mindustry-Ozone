package Ozone;

import Atom.Annotation.ObfuscatorEntryPoint;
import Ozone.UI.CommandsListFrag;
import Ozone.UI.OzoneMenu;

@ObfuscatorEntryPoint
public class Manifest {
    public final static String atomHash = "cfa7716a8e";
    public final static String atomFile = "Atomic-" + atomHash + ".jar", libs = "libs";
    public final static String atomDownloadLink = "https://jitpack.io/com/github/o7-Fire/Atomic-Library/" + atomHash + "/Atomic-Library-" + atomHash + ".jar";
    public static OzoneMenu menu;
    public static CommandsListFrag commFrag;
    public static String currentServer = "";

}
