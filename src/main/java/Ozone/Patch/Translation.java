package Ozone.Patch;

import static Ozone.Interface.registerWords;
public class Translation {
    public static void patch(){
        registerWords("setting.ozone.antiSpam.name", "Enable Anti-Spam");
        registerWords("setting.ozone.colorPatch.name", "Enable Colorized Text");
        registerWords("setting.ozone.commandsPrefix.name", "Commands Prefix");
        registerWords("ozone.menu", "Ozone Menu");
        String[] singlet1 = {"String", "Integer", "Float", "Long", "Boolean"};
        for (String s : singlet1)
            registerWords(s, "[" + s + "]");
    }
}
