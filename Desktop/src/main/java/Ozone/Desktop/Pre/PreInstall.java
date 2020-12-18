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

package Ozone.Desktop.Pre;

import Atom.Utility.Pool;
import Ozone.Desktop.Swing.Main;
import Premain.Catch;
import Premain.MindustryEntryPoint;
import io.sentry.Sentry;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import static Main.OzoneInstaller.mindustry;

public class PreInstall {
    static boolean yet;
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
        m.buttonExit.addActionListener(e -> {
            Pool.daemon(()->{
                try {
                    MindustryEntryPoint.main(new ArrayList<>());
                }catch (Throwable t) {
                    try {
                        Files.write(new File(MindustryEntryPoint.class.getName() + ".txt").toPath(), t.toString().getBytes());
                    }catch (Throwable ignored) { }
                    t.printStackTrace();
                    if (t.getCause() != null) t = t.getCause();
                    Sentry.captureException(t);
                    Catch.errorBox(t.toString(), "Ozone Environment");
                }}).start();
            });
        //Install Button
        m.buttonInstall.addActionListener(e -> {
            m.labelStatus.setVisible(true);
            m.labelStatus.setText("Scanning");
            m.frame1.pack();
            File mods = new File(mindustry, "mods/");
            if (!mods.exists() && !yet) {
                System.out.println(mods.getAbsolutePath() + " doesn't exists, are you sure this is right directory");
                m.labelStatus.setText(mods.getAbsolutePath() + " doesn't exists, are you sure this is right directory");
                m.frame1.pack();
                yet = true;
                return;
            }
            File ozone = new File(mods, "Ozone.jar");
            m.labelStatus.setText("Copying...");
            new Thread(() -> {
                try {
                    File self = new File(PreInstall.class.getProtectionDomain().getCodeSource().getLocation().getFile());
                    String ext = self.getName();
                    if (!ext.contains(".")) throw new RuntimeException("WTF !?!, i am not a jar");
                    ext = ext.substring(ext.indexOf('.') + 1);
                    if (!ext.startsWith("jar")) throw new RuntimeException("WTF !?!, i am not a jar");
                    Files.copy(self.toPath(), ozone.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    m.labelStatus.setText("Finished");
                    m.frame1.pack();
                }catch (Throwable ioException) {
                    m.labelStatus.setText(ioException.getMessage());
                    m.frame1.pack();
                }
            }).start();
            m.frame1.pack();
        });


    }
}
