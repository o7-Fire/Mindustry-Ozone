package Ozone.Pre;

import Ozone.Swing.Main;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static Main.Ozone.*;
import static Main.h.mindustry;

public class PreInstall {

    public static void install(Main m){
        m.label4.setText(mindustry.getAbsolutePath());
        m.labelStatus.setVisible(false);
        m.progressBar1.setVisible(false);
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        m.frame1.setSize(Math.round(width/1.3F), height/3);
        m.frame1.pack();
        m.frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m.frame1.setVisible(true);
        m.button3.addActionListener(e -> {
            m.dialog1.setVisible(true);
            m.fileChooser1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (m.fileChooser1.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                mindustry = m.fileChooser1.getSelectedFile();
            }
            m.label4.setText(mindustry.getAbsolutePath());
            m.dialog1.setVisible(false);
        });
        m.buttonExit.addActionListener(e -> System.exit(0));
        m.buttonInstall.addActionListener(e -> {
            m.labelStatus.setVisible(true);
            m.progressBar1.setVisible(true);
            m.frame1.pack();
            //mods
            File mods = new File(mindustry, "mods/");
            if (!mods.exists()) {
                System.out.println(mods.getAbsolutePath() + " doesn't exists, are you sure this is right directory");
                m.labelStatus.setText(mods.getAbsolutePath() + " doesn't exists, are you sure this is right directory");
            }
            //mods/libs
            File library = new File(mods, libs);
            //mods/Ozone.jar
            File ozone = new File(mods, "/Ozone.jar");
            library.mkdirs();
            //mods/libs/Atomic-AtomHash.jar
            File atom = new File(library, atomFile);
            if (atom.exists() && ozone.exists()) {
                m.labelStatus.setText("Already Installed");
                System.out.println("Already Installed");
                m.progressBar1.setVisible(false);
                return;
            }
            if (atom.exists()) {
                try {
                    Files.copy(new File(PreInstall.class.getProtectionDomain().getCodeSource().getLocation().getFile()).toPath(), ozone.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    m.labelStatus.setText("Finished");
                } catch (IOException ioException) {
                    m.labelStatus.setText(ioException.getMessage());
                }
                return;
            }
            try {
                Download download = new Download(new URL(AtomDownload), atom);
                new Thread(() -> {
                    try {
                        download.run();
                    } catch (Throwable g) {
                        g.printStackTrace();
                        m.labelStatus.setText(g.getMessage());
                    }
                }).start();
                m.progressBar1.setMinimum(0);
                new Thread(() -> {
                    try {
                        while (download.getSize() == -1) Thread.sleep(10);
                        m.progressBar1.setMaximum(download.getSize());
                        while (download.getStatus() != Download.DOWNLOADING) Thread.sleep(10);
                        m.labelStatus.setText("Downloading: " + AtomDownload);
                        m.frame1.pack();
                        while (download.getStatus() == Download.DOWNLOADING) {
                            Thread.sleep(10);
                            m.progressBar1.setValue(download.downloaded.get());
                        }
                        m.progressBar1.setVisible(false);
                        m.labelStatus.setText("Copying: " + PreInstall.class.getProtectionDomain().getCodeSource().getLocation().getFile() + " to: " + ozone.getAbsolutePath());
                        Files.copy(new File(PreInstall.class.getProtectionDomain().getCodeSource().getLocation().getFile()).toPath(), ozone.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        m.labelStatus.setText("Finished");

                    } catch (Throwable g) {
                        g.printStackTrace();
                    }
                }).start();
            } catch (Throwable g) {
                g.printStackTrace();
            }
        });
    }
}
