package Ozone.Desktop.Pre;

import Ozone.Desktop.Swing.Main;
import Ozone.Manifest;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import static Main.h.mindustry;

public class PreInstall {

    public static void install(Main m) {
        m.label4.setText(mindustry.getAbsolutePath());
        m.labelStatus.setVisible(false);
        m.progressBar1.setVisible(false);
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        m.frame1.setSize(Math.round(width / 1.3F), height / 3);
        m.frame1.pack();
        m.frame1.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m.frame1.setVisible(true);
        if (new File(mindustry, "mods/Ozone.jar").exists())
            m.buttonInstall.setText("Update");
        //shitty file chooser
        m.button3.addActionListener(e -> {
            m.dialog1.setVisible(true);
            m.fileChooser1.setSelectedFile(mindustry);
            m.fileChooser1.setFileHidingEnabled(false);
            m.fileChooser1.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (m.fileChooser1.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                mindustry = m.fileChooser1.getSelectedFile();
            }
            m.label4.setText(mindustry.getAbsolutePath());
            m.dialog1.setVisible(false);
            m.frame1.pack();
        });
        m.buttonExit.addActionListener(e -> System.exit(0));

        //Install Button
        m.buttonInstall.addActionListener(e -> {
            m.labelStatus.setVisible(true);
            m.progressBar1.setVisible(true);
            m.labelStatus.setText("Scanning");
            m.frame1.pack();
            //mods
            File mods = new File(mindustry, "mods/");
            if (!mods.exists()) {
                System.out.println(mods.getAbsolutePath() + " doesn't exists, are you sure this is right directory");
                m.labelStatus.setText(mods.getAbsolutePath() + " doesn't exists, are you sure this is right directory");
                m.frame1.pack();
                return;
            }
            //mods/libs
            File library = new File(mods, Manifest.libs);
            //mods/Ozone.jar
            File ozone = new File(mods, "Ozone.jar");
            library.mkdirs();
            //mods/libs/Atomic-AtomHash.jar
            File atom = new File(library, Manifest.atomFile);
            if (ozone.exists()) {
                try {
                    m.labelStatus.setText("Updating");
                    m.frame1.pack();
                    Files.copy(new File(PreInstall.class.getProtectionDomain().getCodeSource().getLocation().getFile()).toPath(), ozone.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (Throwable t) {
                    m.labelStatus.setText(t.getMessage());
                    m.frame1.pack();
                }
            }
            if (atom.exists() && ozone.exists()) {
                m.labelStatus.setText("Atom already downloaded & Ozone already updated");
                m.progressBar1.setVisible(false);
                m.frame1.pack();
                return;
            }
            if (atom.exists()) {
                try {
                    Files.copy(new File(PreInstall.class.getProtectionDomain().getCodeSource().getLocation().getFile()).toPath(), ozone.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    m.labelStatus.setText("Finished");
                    m.frame1.pack();
                } catch (IOException ioException) {
                    m.labelStatus.setText(ioException.getMessage());
                    m.frame1.pack();
                }
                return;
            }
            try {
                DownloadSwing download = new DownloadSwing(new URL(Manifest.atomDownloadLink), atom);
                download.display(m.progressBar1);
                m.frame1.pack();
                download.run();
                m.labelStatus.setText("Installed");
                m.progressBar1.setVisible(false);
                m.frame1.pack();
            } catch (Throwable g) {
                g.printStackTrace();
                m.labelStatus.setText(g.toString());
            }
            m.frame1.pack();
        });
    }
}
