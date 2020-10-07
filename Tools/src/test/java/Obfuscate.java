import java.io.File;
import java.util.ArrayList;

public class Obfuscate {

    public static void main(String[] args) {
        File root = new File("src/main/java/Ozone/");
        if(!root.exists())throw new RuntimeException("gay");
        ArrayList<File> files = recurse(root);



    }

    public static ArrayList<File> recurse(File f){
        ArrayList<File> files = new ArrayList<>();
        for(File c : f.listFiles()){
            if(c.isDirectory())
                files.addAll(recurse(f));
            else
                files.add(c);
        }
        return files;
    }
}
