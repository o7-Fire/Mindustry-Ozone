package Ozone.Pre;

import io.sentry.Sentry;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

//probably will be relocated to Atom library
public class Download implements Runnable {
    protected static ExecutorService es = Executors.newCachedThreadPool();
    // Max size of download buffer.
    protected static int MAX_BUFFER_SIZE = 8192;
    protected URL url; // download URL
    protected AtomicLong downloaded; // number of bytes downloaded
    protected long size; // size of download in bytes
    protected File file;
    protected volatile boolean downloading = true;
    private PrintWriter pw;

    // Constructor for Download.
    public Download(URL url, File file) {
        this.url = url;
        size = -1;
        downloaded = new AtomicLong();
        this.file = file;

    }

    protected static String getUserReading(long s) {
        if (s < 10000000)
            return (s / 1000) / 1000F + " KB";
        else
            return (s / 1000000) / 1000F + " MB";
    }

    public void print(PrintWriter is) {
        if (pw != null) return;
        pw = is;
        es.submit((Runnable) this::print);
    }

    private void print(String s) {
        if (pw != null)
            pw.println(s);
    }

    public long getDownloaded() {
        return downloaded.get();
    }

    // Get this download's size.
    public long getSize() {
        return size;
    }

    private void print() {
        while (downloading) {
            try {
                Thread.sleep(2000);
                if (size < 1) continue;
                print("Downloading: " + getUserReading(downloaded.get()));
            }catch (Throwable ignored) {

            }
        }
    }

    protected void setMax(long max) {
        print("Downloading: " + getUserReading(size));
    }

    protected void updateProgress() {

    }

    // Download file.
    private void download() throws IOException {

        InputStream stream;
        FileOutputStream outputStream;
        File temp = new File(file.getParent(), System.currentTimeMillis() + ".temp");

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
        long contentLength = connection.getContentLengthLong();
        if (contentLength < 1) {
            throw new IOException("Invalid content length");
        }

        if (size == -1) {
            size = contentLength;

        }
        setMax(size);
        stream = connection.getInputStream();
        while (true) {
            byte[] buffer;
            if (size - downloaded.get() > MAX_BUFFER_SIZE) {
                buffer = new byte[MAX_BUFFER_SIZE];
            }else {
                buffer = new byte[(int) (size - downloaded.get())];
            }

            // Read from server into buffer.
            int read = stream.read(buffer);
            if (read == -1)
                break;

            // Write buffer to file.
            outputStream.write(buffer, 0, read);
            downloaded.addAndGet(read);
            updateProgress();
        }

        // Close file.
        try {
            outputStream.close();
        }catch (Exception ignored) {
        }

        // Close connection to server.
        try {
            stream.close();
        }catch (Exception ignored) {
        }


        file.getParentFile().mkdirs();
        if (temp.exists())
            Files.copy(temp.toPath(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
        else
            throw new FileNotFoundException(temp.getAbsolutePath() + " not found. transfer failure ?");

    }

    public Future<?> runAsync() {
        return es.submit(this);
    }

    @Override
    public void run() {
        try {
            download();
        }catch (IOException e) {
            Sentry.captureException(e);
            downloading = false;
            throw new RuntimeException(e);
        }
    }
}