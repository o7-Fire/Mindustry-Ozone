/*
 * Created by JFormDesigner on Sat Sep 26 01:19:52 CEST 2020
 */

package Ozone.Swing;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;


/**
 * @author Itzbenz
 */
public class Main extends JPanel {
    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - Itzbenz
    private JFrame frame1;
    private JTextField textField1;
    private JButton button1;
    private JButton button2;

    public Main() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - Itzbenz
        frame1 = new JFrame();
        textField1 = new JTextField();
        button1 = new JButton();
        button2 = new JButton();

        //======== frame1 ========
        {
            frame1.setTitle("Ozone Installer");
            Container frame1ContentPane = frame1.getContentPane();
            frame1ContentPane.setLayout(new MigLayout(
                    "hidemode 3",
                    // columns
                    "[fill]" +
                            "[fill]" +
                            "[fill]" +
                            "[fill]" +
                            "[fill]" +
                            "[fill]" +
                            "[fill]" +
                            "[fill]" +
                            "[fill]" +
                            "[fill]",
                    // rows
                    "[]" +
                            "[]" +
                            "[]" +
                            "[]" +
                            "[]" +
                            "[]"));

            //---- textField1 ----
            textField1.setText("Mindustry/mods");
            frame1ContentPane.add(textField1, "cell 0 2 10 1");

            //---- button1 ----
            button1.setText("Install");
            frame1ContentPane.add(button1, "cell 0 5");

            //---- button2 ----
            button2.setText("Exit");
            frame1ContentPane.add(button2, "cell 9 5");
            frame1.pack();
            frame1.setLocationRelativeTo(frame1.getOwner());
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
