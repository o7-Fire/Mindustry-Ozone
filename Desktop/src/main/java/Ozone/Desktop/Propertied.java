package Ozone.Desktop;

import java.util.HashMap;

public class Propertied {
    public static HashMap<String, String> h = new HashMap<>();

    static {
        try {
            for (String s : new String(ClassLoader.getSystemResourceAsStream("Manifest.properties").readAllBytes()).split("\n"))
                if (s.startsWith("#")) continue;
                else h.put(s.split("=")[0], s.split("=")[1]);
        } catch (Throwable ignored) {
        }
    }
}
