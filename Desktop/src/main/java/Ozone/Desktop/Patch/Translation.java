package Ozone.Desktop.Patch;

import static Ozone.Patch.Translation.commands;

public class Translation {
    private static volatile boolean init = false;

    public static void Init() {
        if (init) return;
        init = true;
        commands.put("javac", "run single line of java code");
        commands.put("library", "manage runtime library");
        commands.put("debug", "System.out.println(\"yeet\")");
    }
}
