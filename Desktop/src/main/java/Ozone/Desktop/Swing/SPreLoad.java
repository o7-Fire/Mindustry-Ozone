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

/*
 * Created by JFormDesigner on Sat Sep 26 05:18:24 CEST 2020
 */

package Ozone.Desktop.Swing;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

/**
 * @author Itzbenz
 */
public class SPreLoad extends JFrame {
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    public Frame frame1;
    public JLabel label1;
    public JLabel label2;
    public JProgressBar progressBar1;

    public SPreLoad() {
        initComponents();
    }
static {
    Main.setTheme();
}
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        frame1 = new Frame();
        label1 = new JLabel();
        label2 = new JLabel();
        progressBar1 = new JProgressBar();

        //======== frame1 ========
        {
            frame1.setResizable(false);
            frame1.setTitle("Downloading");
            frame1.setEnabled(false);
            frame1.setLayout(new MigLayout(
                    "fill,hidemode 3",
                    // columns
                    "[fill]" +
                            "[fill]",
                    // rows
                    "[]" +
                            "[]" +
                            "[]"));

            //---- label1 ----
            label1.setText("Downloading");
            frame1.add(label1, "cell 0 0 2 1");

            //---- label2 ----
            label2.setText("10 MBPS/s");
            frame1.add(label2, "cell 0 1");
            frame1.add(progressBar1, "cell 0 2 2 1");
            frame1.pack();
            frame1.setLocationRelativeTo(frame1.getOwner());
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
