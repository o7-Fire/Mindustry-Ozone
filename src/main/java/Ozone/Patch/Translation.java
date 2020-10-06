package Ozone.Patch;

import java.util.HashMap;
import java.util.Map;

import static Ozone.Interface.registerWords;

public class Translation {

    public static void patch() {
        String[] normalSinglet = {"Run"};
        String[] singlet1 = {"String", "Integer", "Float", "Long", "Boolean", "Commands"};
        HashMap<String, String> settings = new HashMap<>();
        HashMap<String, String> commands = new HashMap<>();
        HashMap<String, String> keyBinds = new HashMap<>();
        settings.put("antiSpam", "[Broken]Enable Anti-Spam");
        settings.put("debugMode", "Enable Debug Mode");
        settings.put("colorPatch", "Enable Colorized Text");
        settings.put("commandsPrefix", "Commands Prefix");
        registerWords("ozone.menu", "Ozone Menu");
        registerWords("ozone.hud", "Ozone HUD");
        registerWords("ozone.javaEditor", "Java Executor");
        registerWords("ozone.javaEditor.reformat", "Reformat");
        registerWords("ozone.javaEditor.run", "Run");
        registerWords("ozone.commandsUI", "Commands GUI");
        commands.put("help", "help desk");
        commands.put("chaosKick", "vote everyone everytime everywhere");
        commands.put("taskMove", "move using current unit with pathfinding algorithm");
        commands.put("infoPos", "get current info pos");
        commands.put("infoPathfinding", "get Pathfinding overlay");
        commands.put("forceExit", "you want to crash ?");
        commands.put("taskDeconstruct", "deconstruct your block with AI");
        commands.put("sendColorize", "send Colorized text");
        commands.put("infoUnit", "get current unit info");
        commands.put("randomKick", "random kick someone");
        commands.put("shuffleSorter", "shufleelelelel");
        for (Map.Entry<String, String> s : commands.entrySet()) {
            registerWords("ozone.commands." + s.getKey(), s.getValue());
        }
        for (Map.Entry<String, String> s : settings.entrySet()) {
            registerWords("setting.ozone." + s.getKey() + ".name", s.getValue());
        }
        for (Map.Entry<String, String> s : keyBinds.entrySet()) {
            registerWords("section." + s.getKey() + ".name", s.getValue());
        }
        for (String s : singlet1) registerWords(s, "[" + s + "]");
        for (String s : normalSinglet) registerWords(s);
    }
}
