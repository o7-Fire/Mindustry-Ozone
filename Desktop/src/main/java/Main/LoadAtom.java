package Main;

import Atom.Manifest;

import java.util.ArrayList;

public class LoadAtom {
    public static ArrayList<String> main(String[] args) {
        ArrayList<String> s = new ArrayList<>();
        Manifest.library.forEach(library -> {

            s.add(library.getDownloadURL());
        });
        return s;
    }
}
