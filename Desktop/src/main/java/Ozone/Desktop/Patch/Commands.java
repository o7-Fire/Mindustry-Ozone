package Ozone.Desktop.Patch;

import Atom.Utility.Utility;

import javax.tools.ToolProvider;
import java.util.ArrayList;

import static Ozone.Commands.Commands.*;

public class Commands {
    private static volatile boolean init = false;

    public static void Init() {
        if (init) return;
        init = true;
        register("javac", new Command(Commands::javac));
    }

    public static void javac(ArrayList<String> arg) {
        if (ToolProvider.getSystemJavaCompiler() == null) {
            tellUser("no javac detected, are you sure using JDK ?");
            return;
        }
        String code = Utility.joiner(arg.toArray(new String[0]), " ");
    }

}
