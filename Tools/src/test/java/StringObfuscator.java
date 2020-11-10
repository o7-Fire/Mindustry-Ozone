
/*
 * Copyright 2020 Itzbenz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import Atom.Time.Countdown;
import Atom.Utility.Meth;
import Atom.Utility.Random;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

//Personal use
public class StringObfuscator {
    public static final boolean test = false;

    public static double avg(ArrayList<Long> arr) {
        double sum = 0;
        for (long l : arr) {
            sum += l;
        }
        return sum / arr.size();
    }

    public static void main(String[] args) throws Throwable {
        if (test) {
            File victim = new File("Tools/src/test/java/StringObfuscatorTest.java");
            writeFile(victim, obfuscate(victim));
            return;
        }
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
        obfuscate(core);
        obfuscate(desk);
        obfuscate(droid);
    }

    public static void obfuscate(List<File> files) throws IOException {
        for (File f : files) {
            if (!getExtension(f).equals("java")) continue;
            System.out.println(f.getAbsolutePath());
            writeFile(f, obfuscate(f));
        }
    }

    public static String getExtension(File f) {
        String extension = "";
        int i = f.getName().lastIndexOf('.');
        if (i > 0) {
            extension = f.getName().substring(i + 1);
        }
        return extension;
    }

    public static void writeFile(File f, String data) throws IOException {
        FileWriter f2 = new FileWriter(f, false);
        f2.write(data);
        f2.close();
    }

    public static String obfuscate(File f) throws FileNotFoundException {
        CompilationUnit compilationUnit = StaticJavaParser.parse(f);
        compilationUnit.findAll(StringLiteralExpr.class).forEach(node -> {
            if (!node.replace(StaticJavaParser.parseExpression(obfuscate(node.getValue()))))
                throw new RuntimeException("Holy shit cant do shit " +
                        "\n" + "File: " + f.getAbsolutePath() +
                        "\n" + "Obfuscated: " + obfuscate(node.getValue()) +
                        "\n" + "Original: " + node.getValue() +
                        "\n" + "Detailed node: " + node.toString());
        });
        return compilationUnit.toString();
    }

    // "pac" = new String(new byte[]{102144/912, 97 , 99})
    public static String obfuscate(String s) {

        String temp = "new String(new byte[]{";
        String teem = "})";
        StringBuilder sb = new StringBuilder();
        String startOffset = Random.getString(Random.getInt(s.length()));
        String endOffset = Random.getString(Random.getInt(s.length()));
        boolean shouldOffset = Random.getBool();
        int offset = 0;
        int length = 0;
        if (s.isEmpty()) return temp + teem;
        sb.append(temp);

        if (shouldOffset && !startOffset.isEmpty()) {
            //start offset
            for (int c : startOffset.toCharArray()) {
                if (Random.getBool())
                    sb.append(c);
                else
                    sb.append("(byte)Math.round(Math.sqrt(").append(c * c).append("))");
                sb.append(',');
                offset++;
            }
        }
        //aa,
        //actual
        for (int c : s.toCharArray()) {
            if (Random.getBool())
                sb.append(c);
            else
                sb.append("(byte)Math.round(Math.sqrt(").append(c * c).append("))");
            sb.append(',');
            length++;
        }
        //aa,bb,
        if (shouldOffset && !endOffset.isEmpty()) {
            //end offset
            for (int c : endOffset.toCharArray()) {
                if (Random.getBool())
                    sb.append(c);
                else
                    sb.append("(byte)Math.round(Math.sqrt(").append(c * c).append("))");
                sb.append(',');
            }
        }
        if (sb.charAt(sb.length() - 1) == ',')
            sb.deleteCharAt(sb.length() - 1);
        if (!shouldOffset)
            sb.append(teem);
        else {
            sb.append("}").append(", ");
            if (Random.getBool())
                sb.append("(int)Math.round(Math.sqrt(").append(offset * offset).append("))");
            else
                sb.append(offset);
            sb.append(", ");
            if (Random.getBool())
                sb.append("(int)Math.round(Math.sqrt(").append(length * length).append("))");
            else
                sb.append(length);
            sb.append(")");
        }
        return sb.toString();

    }

    public static ArrayList<String> yeet(char s, String data) {

        ArrayList<String> dats = new ArrayList<>();
        if (!data.contains(String.valueOf(s))) return dats;
        try {
            Pattern pattern = Pattern.compile("([\"'])(?:(?=(\\\\?))\\2.)*?\\1");
            dats.addAll(Arrays.asList(pattern.split(data)));
            return dats;
        } catch (Throwable t) {
        }
        dats.clear();
        boolean f = false, skip = false;


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
