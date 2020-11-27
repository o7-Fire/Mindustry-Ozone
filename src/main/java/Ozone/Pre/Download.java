

package Ozone.Pre;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.concurrent.atomic.AtomicInteger;

//probably will be relocated to Atom library
public class Download implements Runnable {

    // Max size of download buffer.
    private static final int MAX_BUFFER_SIZE = 1024;
    private final URL url; // download URL
    public AtomicInteger downloaded; // number of bytes downloaded
    private int size; // size of download in bytes
    private File file;
    private long lastRecordTime = 0, lastRecord = 0;
    private boolean displayed = false;

    // Constructor for Download.
    public Download(URL url, File file) {
        this.url = url;
        size = -1;
        downloaded = new AtomicInteger();
        this.file = file;
    }

    // Get this download's size.
    public int getSize() {
        return size;
    }

    // Download file.
    public void run() {

        InputStream stream = null;
        FileOutputStream outputStream = null;
        File temp = new File(file.getParent(), System.currentTimeMillis() + ".temp");
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
                throw new IOException("Response Code: " + connection.getResponseCode());
            }

            // Check for valid content length.
            int contentLength = connection.getContentLength();
            if (contentLength < 1) {
                throw new IOException("Invalid content length");
            }

            if (size == -1) {
                size = contentLength;

            }
            stream = connection.getInputStream();
            while (true) {
                byte[] buffer;
                if (size - downloaded.get() > MAX_BUFFER_SIZE) {
                    buffer = new byte[MAX_BUFFER_SIZE];
                }else {
                    buffer = new byte[size - downloaded.get()];
                }

                // Read from server into buffer.
                int read = stream.read(buffer);
                if (read == -1)
                    break;

                // Write buffer to file.
                outputStream.write(buffer, 0, read);
                downloaded.addAndGet(read);
            }

        }catch (Exception e) {
            throw new RuntimeException(e);
        }finally {
            // Close file.
            if (outputStream != null) {
                try {
                    outputStream.close();
                }catch (Exception e) {
                }
            }

            // Close connection to server.
            if (stream != null) {
                try {
                    stream.close();
                }catch (Exception e) {
                }
            }

            try {
                file.getParentFile().mkdirs();
                if(temp.exists())
                Files.copy(temp.toPath(), file.toPath());
            }catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}