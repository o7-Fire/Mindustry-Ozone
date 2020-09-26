package Main;

import Ozone.Pre.Download;
import Ozone.Swing.Main;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static Main.Ozone.*;

public class h {
    private static Main m;
    private static File mindustry;

    public static void main(String[] args) {
        if (System.getProperty("os.name").toUpperCase().contains("WIN"))
            mindustry = new File(System.getenv("AppData") + "/Mindustry");
        else
            mindustry = new File(System.getenv("user.home"));
        m = new Main();
        m.textField1.setText(mindustry.getAbsolutePath());
        m.labelStatus.setVisible(false);
        m.progressBar1.setVisible(false);
        m.frame1.setVisible(true);
        m.frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m.button3.addActionListener(e -> {
            m.dialog1.setVisible(true);
            m.fileChooser1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (m.fileChooser1.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                mindustry = m.fileChooser1.getSelectedFile();
            }
            m.dialog1.setVisible(false);
        });
        m.buttonExit.addActionListener(e -> System.exit(0));
        m.buttonInstall.addActionListener(e -> {
            //mods
            File mods = new File(mindustry, "mods/");
            if (!mods.exists()) {
                m.labelStatus.setText(mods.getAbsolutePath() + " doesn't exists, are you sure this is right directory");
                m.labelStatus.setVisible(true);
            }
            //mods/libs
            File library = new File(mods, libs);
            //mods/Ozone.jar
            File ozone = new File(mods, "Ozone.jar");
            library.mkdirs();
            //mods/libs/Atomic-AtomHash.jar
            File atom = new File(library, atomFile);
            if (atom.exists() && ozone.exists()) {
                m.labelStatus.setText("Already installed");
                return;
            }
            try {
                Download download = new Download(new URL(AtomDownload), atom);
                new Thread(() -> {
                    try {
                        m.progressBar1.setMinimum(0);
                        while (download.getSize() == -1) Thread.sleep(50);
                        m.progressBar1.setMaximum(download.getSize());
                        while (download.getStatus() != Download.DOWNLOADING) Thread.sleep(50);
                        while (download.getStatus() == Download.DOWNLOADING) {
                            Thread.sleep(30);
                            m.progressBar1.setValue(download.downloaded.get());
                        }

                    } catch (Throwable ignored) {
                    }

                }).start();
                m.labelStatus.setText("Downloading: " + AtomDownload);
                download.run();
                //oh yes
                m.labelStatus.setText("Copying: " + Ozone.ozone.getName() + " to: " + ozone.getAbsolutePath());
                Files.copy(Ozone.ozone.toPath(), ozone.toPath(), StandardCopyOption.REPLACE_EXISTING);
                m.labelStatus.setText("Finished");
            } catch (Throwable g) {
                //oh no
                m.labelStatus.setText(g.getMessage());
            }

        });
    }

}
