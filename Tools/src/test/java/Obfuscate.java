import java.io.File;
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
        Runtime.getRuntime().exec("git commit -m \"yeet on earth, the compiler mean death\"");
        for (File f : files) {

        }

    }

    public static ArrayList<File> recurse(File f){
        ArrayList<File> files = new ArrayList<>();
        for(File c : f.listFiles()){
            if(c.isDirectory())
                files.addAll(recurse(c));
            else
                files.add(c);
        }
        return files;
    }
}
