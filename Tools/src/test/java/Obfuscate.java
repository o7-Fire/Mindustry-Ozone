import Atom.Utility.Utility;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class Obfuscate {

    public static void main(String[] args) throws Exception {
        File root = new File("src/main/java/Ozone/");
        if (!root.exists()) throw new RuntimeException("gay");
        ArrayList<File> files = recurse(root);
        for (File f : files) {
            System.out.println(f.getAbsolutePath());
        }
        System.out.println("Total: " + files.size());
        System.out.println("Commit git crime");
        Process p = Runtime.getRuntime().exec("git commit -m \"yeet on earth, the compiler mean death\"");

        for (File f : files) {
            for (String s : Files.readAllLines(f.toPath())) {
                ArrayList<String> ar = yeet('"', s);
                if (ar.isEmpty()) continue;
                System.out.println(Utility.joiner(ar, ", "));
            }
        }

    }

    public static ArrayList<String> yeet(char s, String data) {
        boolean f = false;
        ArrayList<String> dats = new ArrayList<>();
        if (!data.contains(String.valueOf(s))) return dats;
        StringBuilder sb = new StringBuilder();
        for (char c : data.toCharArray()) {
            if (c == s) {
                f = !f;
                sb.append(s);
                continue;
            }
            if (f) sb.append(c);
            else {
                if (!sb.toString().replaceAll(String.valueOf(s), "").isEmpty()) {
                    dats.add(sb.toString());
                }
                sb = new StringBuilder();
            }
        }
        return dats;
    }

    public static ArrayList<File> recurse(File f) {
        ArrayList<File> files = new ArrayList<>();
        for (File c : f.listFiles()) {
            if (c.isDirectory())
                files.addAll(recurse(c));
            else
                files.add(c);
        }
        return files;
    }
}
