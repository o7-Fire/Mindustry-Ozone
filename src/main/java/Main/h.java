package Main;

import Ozone.Swing.Main;

public class h {
    private static Main m;

    public static void main(String[] args) throws InterruptedException {
        m = new Main();
        m.setEnabled(true);
        m.setVisible(true);
        while (true) {
            Thread.sleep(1000);
            System.out.println("Alive");
        }
    }

}
