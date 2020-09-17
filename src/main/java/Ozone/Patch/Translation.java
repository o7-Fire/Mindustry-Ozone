package Ozone.Patch;

import static Ozone.Interface.registerWords;
public class Translation {
    public static void patch(){
        registerWords("setting.ozone.antiSpam.name", "Enable Anti-Spam");
        registerWords("setting.ozone.debugMode.name", "Enable Debug Mode");
        registerWords("setting.ozone.colorPatch.name", "Enable Colorized Text");
        registerWords("setting.ozone.commandsPrefix.name", "Commands Prefix");
        registerWords("ozone.menu", "Ozone Menu");
        registerWords("ozone.hud", "Ozone HUD");
        registerWords("ozone.javaEditor", "Java Executor");
        registerWords("ozone.javaEditor.reformat", "Reformat");
        registerWords("ozone.javaEditor.run", "Run");
        String[] singlet1 = {"String", "Integer", "Float", "Long", "Boolean", "Commands"};
        for (String s : singlet1)
            registerWords(s, "[" + s + "]");
    }
}
