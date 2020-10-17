package Ozone.Pre;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicInteger;

//probably will be relocated to Atom library
public class Download implements Runnable {

    // These are the status names.
    public static final String[] STATUSES = {"Downloading",
            "Paused", "Complete", "Cancelled", "Error"};
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
    private long lastRecordTime = 0, lastRecord = 0;

    // Constructor for Download.
    public Download(URL url, File file) {
        this.url = url;
        size = -1;
        downloaded = new AtomicInteger();
        status = DOWNLOADING;
        this.file = file;

    }


    // Get this download's size.
    public int getSize() {
        return size;
    }

    // Get this download's progress.
    public float getProgress() {
        return ((float) downloaded.get() / size) * 100;
    }

    // Get this download's status.
    public int getStatus() {
        return status;
    }

    // Pause this download.
    public void pause() {
        status = PAUSED;
        stateChanged();
    }

    // Resume this download.
    public void resume() {
        status = DOWNLOADING;
        stateChanged();
    }

    // Cancel this download.
    public void cancel() {
        status = CANCELLED;
        stateChanged();
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
        try {
            outputStream = new FileOutputStream(file);
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
            }


            stream = connection.getInputStream();
            while (status == DOWNLOADING) {
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

        }
    }

    private void stateChanged() {

    }
}