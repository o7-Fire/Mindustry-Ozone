import Atom.Meth;
import Atom.Random;
import Atom.Struct.Stream;
import Atom.Time.Countdown;
import com.google.googlejavaformat.java.Formatter;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

//Personal use
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
        File desktop = new File("Desktop/src/main/java/Ozone/");
        File android = new File("Android/src/main/java/Ozone/");
        if (!root.exists()) throw new RuntimeException(root.getAbsolutePath() + " doesnt exists");
        if (!desktop.exists()) throw new RuntimeException(desktop.getAbsolutePath() + " doesnt exists");
        if (!android.exists()) throw new RuntimeException(android.getAbsolutePath() + " doesnt exists");

        ArrayList<File> core = recurse(root);
        ArrayList<File> desk = recurse(desktop);
        ArrayList<File> droid = recurse(android);
        System.out.println("Total: " + (core.size() + desk.size() + droid.size()));
        System.out.println("Commit git crime");
        Process p = Runtime.getRuntime().exec("git commit -m \"yeet on earth, the compiler mean death\"");
        Stream.readInputSync(p.getInputStream(), System.out::println, '\n');
        obfuscate(core);
        obfuscate(desk);
        obfuscate(droid);
    }

    public static void obfuscate(List<File> files) throws IOException {
        for (File f : files) {
            String extension = "";
            int i = f.getName().lastIndexOf('.');
            if (i > 0) {
                extension = f.getName().substring(i + 1);
            }
            if (!extension.equals("java")) continue;
            StringBuilder sb = new StringBuilder();
            for (String s : Files.readAllLines(f.toPath())) {
                s = s.trim();
                if (s.isEmpty()) continue;
                if (s.contains("//")) {
                    int loc = s.indexOf("//");
                    if (loc == 0) continue;//bruh
                    s = replace(s.substring(loc + 1), "", s);
                }
                ArrayList<String> ar = yeet('"', s);
                if (ar.isEmpty()) {
                    sb.append(s).append("\n");
                    continue;
                }
                for (String sa : ar) {
                    String mod = (obfuscate(sa));
                    String org = ("\"" + sa + "\"");
                    s = replace(org, mod, s);
                    System.out.println(mod);
                    System.out.println(org);
                    System.out.println("\n");
                }
                sb.append(s);
            }
            String g = sb.toString();
            try {
                g = new Formatter().formatSource(sb.toString());
            } catch (Throwable t) {
                t.printStackTrace();
                System.out.println(sb.toString());
                System.out.println("ERRRRRRRRRRRRRRRRRR");
            }
            System.out.println(f.getAbsolutePath());
            try {
                FileWriter f2 = new FileWriter(f, false);
                f2.write(g);
                f2.close();
            } catch (Throwable t) {
                t.printStackTrace();
            }

        }

    }

    public static String replace(String regex, String replace, String data) {
        return Pattern.compile(regex, Pattern.LITERAL).matcher(data).replaceFirst(replace);
    }

    // "pac" = new String(new byte[]{102144/912, 97 , 99})
    public static String obfuscate(String s) {

        String temp = "new String(new byte[]{";
        String teem = "})";
        StringBuilder sb = new StringBuilder();
        String startOffset = Random.getString(Random.getInt(s.length()));
        String endOffset = Random.getString(Random.getInt(s.length()));
        boolean shouldOffset = Random.getBool();
        if (s.isEmpty()) return temp + teem;
        sb.append(temp);

        if (shouldOffset && !startOffset.isEmpty()) {
            //start offset
            for (int c : startOffset.toCharArray()) {
                if (Random.getBool())
                    sb.append(c);
                else if (Random.getBool())
                    sb.append("(byte)Math.round(Math.sqrt(").append(c * c).append("))");
                sb.append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        //actual
        for (int c : s.toCharArray()) {
            if (Random.getBool())
                sb.append(c);
            else if (Random.getBool())
                sb.append("(byte)Math.round(Math.sqrt(").append(c * c).append("))");
            sb.append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        if (shouldOffset && !endOffset.isEmpty()) {
            //end offset
            for (int c : endOffset.toCharArray()) {
                if (Random.getBool())
                    sb.append(c);
                else if (Random.getBool())
                    sb.append("(byte)Math.round(Math.sqrt(").append(c * c).append("))");
                sb.append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
        }
        if (!shouldOffset)
            sb.append(teem);
        else {
            sb.append("}").append(", ").append(startOffset.length()).append(", ").append((startOffset.length() + s.length()) - endOffset.length()).append(")");
        }
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

    public static ArrayList<File> recurse(File f) {
        ArrayList<File> files = new ArrayList<>();
        for (File c : Objects.requireNonNull(f.listFiles())) {
            if (c.isDirectory())
                files.addAll(recurse(c));
            else
                files.add(c);
        }
        return files;
    }

    @Test
    public void name() {
        ArrayList<Long> obfuscated = new ArrayList<>();
        ArrayList<Long> normal = new ArrayList<>();
        ArrayList<String> yet = new ArrayList<>();
        yet.add("//yes yes no //yes yes no");
        yet.add("String s = \"literal\";//ofc");
        yet.add("String gay = \"absolute\";");
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
}
