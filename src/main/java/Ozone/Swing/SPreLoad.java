/*
 * Created by JFormDesigner on Sat Sep 26 05:18:24 CEST 2020
 */

package Ozone.Swing;

import java.awt.*;
import javax.swing.*;
import net.miginfocom.swing.*;

/**
 * @author Itzbenz
 */
public class SPreLoad extends JFrame {
    public SPreLoad() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Itzbenz
        frame1 = new Frame();
        label1 = new JLabel();
        progressBar1 = new JProgressBar();

        //======== frame1 ========
        {
            frame1.setResizable(false);
            frame1.setTitle("Downloading");
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
            frame1.add(progressBar1, "cell 0 1 2 2");
            frame1.pack();
            frame1.setLocationRelativeTo(frame1.getOwner());
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Itzbenz
    public Frame frame1;
    public JLabel label1;
    public JProgressBar progressBar1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
