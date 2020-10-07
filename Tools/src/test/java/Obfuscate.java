import Atom.Meth;
import Atom.Random;
import Atom.Time.Countdown;
import Atom.Utility.Utility;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

public class Obfuscate {
    public static double avg(ArrayList<Long> arr) {
        double sum = 0;
        for (long l : arr) {
            sum += l;
        }
        return sum / arr.size();
    }

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
            StringBuilder sb = new StringBuilder();
            for (String s : Files.readAllLines(f.toPath())) {
                ArrayList<String> ar = yeet('"', s);
                if (ar.isEmpty()) {
                    sb.append(s).append("\n");
                    continue;
                }
                for (String sa : ar) {
                
                }
                System.out.println(Utility.joiner(ar, ", "));
            }
        }

    }

    // "pac" = new String(new byte[]{102144/912, 97 , 99})
    public static String obfuscate(String s) {
        String temp = "new String(new byte[]{";
        String teme = "})";
        StringBuilder sb = new StringBuilder();
        sb.append(temp);
        for (int c : s.toCharArray()) {
            int rand = Random.getInt();
            int main = c * rand;
            String gay = main + "/" + rand;
            sb.append(gay);
            sb.append(',');
        }
        sb.append(teme);
        return sb.toString();
    }

    public static ArrayList<String> yeet(char s, String data) {
        boolean f = false, skip = false;
        ArrayList<String> dats = new ArrayList<>();
        if (!data.contains(String.valueOf(s))) return dats;
        StringBuilder sb = new StringBuilder();
        for (char c : data.toCharArray()) {
            if (!skip)
                if (c == s) {
                    f = !f;
                    sb.append(s);
                    continue;
                }
            if (skip) skip = false;
            if (c == '\\') skip = true;
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

    @Test
    public void name() {
        ArrayList<Long> obfuscated = new ArrayList<>();
        ArrayList<Long> normal = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            Countdown.start();
            String s = new String(new byte[]{984928 / 8794, 852339 / 8787, 447579 / 4521, 768902 / 7186, 555228 / 5724, 795469 / 7723, 294920 / 2920, 318112 / 9941, 416488 / 5272, 87352 / 716, 677100 / 6100, 944350 / 8585, 399354 / 3954, 11730 / 255, 43148 / 644, 291153 / 2623, 986777 / 9053, 929225 / 8525, 556586 / 5738, 347490 / 3159, 820900 / 8209, 669185 / 5819, 240779 / 4081,});
            obfuscated.add(Countdown.stop());
            System.out.println(Countdown.result());
            System.out.println("vs");
            Countdown.start();
            String lol = new String("package Ozone.Commands;");
            normal.add(Countdown.stop());
            System.out.println(Countdown.result());
        }
        System.out.println("obfuscated avg, max, min: " + avg(obfuscated) + ", " + Meth.max(obfuscated.toArray(new Long[0])) + ", " + Meth.min(obfuscated.toArray(new Long[0])));
        System.out.println("normal avg, max, min: " + avg(normal) + ", " + Meth.max(normal.toArray(new Long[0])) + ", " + Meth.min(normal.toArray(new Long[0])));
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
