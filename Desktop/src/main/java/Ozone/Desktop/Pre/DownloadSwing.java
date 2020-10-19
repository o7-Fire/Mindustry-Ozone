package Ozone.Desktop.Pre;

import Ozone.Desktop.Swing.SPreLoad;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicInteger;

//probably will be relocated to Atom library
public class DownloadSwing implements Runnable {


    // These are the status codes.
    public static final int DOWNLOADING = 0;
    public static final int PAUSED = 1;
    public static final int COMPLETE = 2;
    public static final int CANCELLED = 3;
    public static final int ERROR = 4;

    // Max size of download buffer.
    private static final int MAX_BUFFER_SIZE = 1024;
    private final URL url; // download URL
    public AtomicInteger downloaded; // number of bytes downloaded
    private int size; // size of download in bytes
    private int status; // current status of download
    private File file;
    private SPreLoad swing = null;
    public JProgressBar progressBar = null;
    public JLabel label = null;
    public JFrame frame = null;
    private long lastRecordTime = 0, lastRecord = 0;
    private boolean displayed = false;

    // Constructor for Download.
    public DownloadSwing(URL url, File file) {
        this.url = url;
        size = -1;
        downloaded = new AtomicInteger();
        status = DOWNLOADING;
        this.file = file;

    }

    public void display(JFrame frame) {
        this.frame = frame;
    }

    public void display(JLabel label) {
        this.label = label;
        this.label.setText("Connecting...." + url.toString());
        if (frame != null) frame.pack();
    }

    public void display(JProgressBar progressBar) {
        this.progressBar = progressBar;
        this.progressBar.setMinimum(0);
        this.progressBar.setMaximum(100);
        this.progressBar.setValue(0);
        this.progressBar.setVisible(true);
        if (frame != null) frame.pack();

    }


    public void display() {
        if (displayed) return;
        displayed = true;
        swing = new SPreLoad();
        swing.progressBar1.setMinimum(0);
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        swing.frame1.setSize(Math.round(width / 1.3F), height / 3);
        swing.label2.setText("Connecting....");
        swing.label1.setText("Downloading: " + url.toString());
        swing.frame1.pack();
        swing.progressBar1.setMaximum(100);
        swing.frame1.setVisible(true);
        swing.frame1.pack();
    }

    private void setMax() {
        if (progressBar != null)
            progressBar.setMaximum(getSize());
        if (swing != null) {
            swing.progressBar1.setMaximum(getSize());
            if (getSize() < 10000000)
                swing.label2.setText(file.getName() + " " + (getSize() / 1000) / 1000F + " KB");
            else
                swing.label2.setText(file.getName() + " " + (getSize() / 1000000) / 1000F + " MB");
            swing.frame1.pack();
        }
        if (label != null) {
            if (getSize() < 10000000)
                label.setText(file.getName() + " " + (getSize() / 1000) / 1000F + " KB");
            else
                label.setText(file.getName() + " " + (getSize() / 1000000) / 1000F + " MB");
        }
        if (frame != null) frame.pack();

    }

    private void updateStatus() {
        if (progressBar != null)
            progressBar.setValue(downloaded.get());


        if ((System.currentTimeMillis() - lastRecordTime) > 800) {
            if (lastRecord != 0) {
                float down = downloaded.get() - lastRecord;
                down = down / 100000;
                if (swing != null)
                    swing.label2.setText(down + " Mb/Second");
                if (label != null)
                    label.setText(down + " Mb/Second");
            }
            lastRecord = downloaded.get();
            lastRecordTime = System.currentTimeMillis();
        }
        if (swing != null)
            swing.progressBar1.setValue(downloaded.get());
        if (frame != null) frame.pack();
    }

    // Get this download's size.
    public int getSize() {
        return size;
    }

    // Mark this download as having an error.
    private void error() {
        status = ERROR;
        stateChanged();
    }

    // Download file.
    public void run() {

        InputStream stream = null;
        FileOutputStream outputStream = null;
        File temp = new File(file.getParentFile(), this.toString() + System.currentTimeMillis() + ".temp");
        temp.deleteOnExit();
        try {
            outputStream = new FileOutputStream(temp);
            // Open connection to URL.
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();

            // Specify what portion of file to download.
            connection.setRequestProperty("Range",
                    "bytes=" + downloaded + "-");

            // Connect to server.
            connection.connect();

            // Make sure response code is in the 200 range.
            if (connection.getResponseCode() / 100 != 2) {
                error();
            }

            // Check for valid content length.
            int contentLength = connection.getContentLength();
            if (contentLength < 1) {
                error();
            }

            if (size == -1) {
                size = contentLength;
                stateChanged();
                setMax();
            }


            stream = connection.getInputStream();
            while (status == DOWNLOADING) {
                updateStatus();
                byte[] buffer;
                if (size - downloaded.get() > MAX_BUFFER_SIZE) {
                    buffer = new byte[MAX_BUFFER_SIZE];
                } else {
                    buffer = new byte[size - downloaded.get()];
                }

                // Read from server into buffer.
                int read = stream.read(buffer);
                if (read == -1)
                    break;

                // Write buffer to file.
                outputStream.write(buffer, 0, read);
                downloaded.addAndGet(read);
                stateChanged();
            }

            if (status == DOWNLOADING) {
                status = COMPLETE;
                stateChanged();
            }
        } catch (Exception e) {
            temp.delete();
            throw new RuntimeException(e);
        } finally {
            // Close file.
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                }
            }

            // Close connection to server.
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                }
            }
            try {
                if (temp.exists())
                    Files.copy(temp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } catch (Throwable t) {

            }
            if (swing != null)
                swing.frame1.setVisible(false);
            if (frame != null) frame.pack();

        }
    }

    private void stateChanged() {

    }
}