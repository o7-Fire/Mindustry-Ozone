package Ozone.Desktop.Pre;


import Ozone.Desktop.LibraryLoader;
import Ozone.Desktop.SharedBootstrap;

import java.io.IOException;
import java.util.Objects;

public class PrePatcher {
    static String[] patchable = {
            "mindustry.graphics.LoadRenderer"
    };
    static LibraryLoader cl = SharedBootstrap.libraryLoader;

    public static void init() throws IOException {
        for (String s : patchable) {
            patch(s);
        }
    }

    public static void patch(String path) throws IOException {
        cl.defineClass(path, Objects.requireNonNull(PrePatcher.class.getClassLoader().getResourceAsStream(path.replace('.', '/') + ".class")));
    }
}
