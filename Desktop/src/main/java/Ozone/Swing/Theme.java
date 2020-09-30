package Ozone.Swing;


import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Theme {
    public static void setTheme() {
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            return;
        } catch (Throwable ignored) {
        }
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
            return;
        } catch (Throwable ignored) {
        }
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
            return;
        } catch (Throwable ignored) {
        }
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            return;
        } catch (Throwable ignored) {
        }
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            return;
        } catch (Throwable ignored) {
        }


    }
}
