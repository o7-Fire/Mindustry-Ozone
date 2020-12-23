import Ozone.Pre.Download;
import org.junit.Test;

import javax.swing.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class h {
    URLClassLoader url;
    Runnable r;
    
    @Test
    public void he() throws MalformedURLException {
        JWindow window = new JWindow();
        window.getContentPane().add(new JLabel("Nice", new ImageIcon(new URL("https://cdn.discordapp.com/attachments/724060628763017296/791132176091447296/loading.gif")), SwingConstants.CENTER));
        window.pack();
        window.setLocationRelativeTo(null);
        window.setVisible(true);
        try {
            Thread.sleep(5000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        window.setVisible(false);
        JFrame frame = new JFrame();
        frame.add(new JLabel("Welcome"));
        frame.setVisible(true);
        frame.setSize(300, 100);
        window.dispose();
    }
    
    @org.junit.Test
    public void name() throws Throwable {
        File temp = new File(System.currentTimeMillis() + ".zip");
        temp.deleteOnExit();
        System.out.println(temp.getAbsolutePath());
        Download d = new Download(new URL("http://212.183.159.230/200MB.zip"), temp);
        d.run();
        
    }

}
