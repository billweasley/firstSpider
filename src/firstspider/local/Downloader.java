package firstspider.local;

import java.io.*;
import java.net.*;
import java.util.*;

public class Downloader extends Observable implements Runnable {

    private static final int MAX_BUFFER_SIZE = 1024;

    public static final String STATUSES[] = {"Downloading",
        "Paused", "Complete", "Cancelled", "Error"};

    public static final int DOWNLOADING = 0;
    public static final int PAUSED = 1;
    public static final int COMPLETE = 2;
    public static final int CANCELLED = 3;
    public static final int ERROR = 4;

    private URL url; // download URL
    private int size; // size of download in bytes
    private int status; // current status of download
    private String destination;
    private Map cookie;
    private String fileName;

    public Downloader(URL url, String destination, Map cookie, String fileName) {
        this.url = url;
        size = -1;
        status = DOWNLOADING;
        this.destination = destination;
        this.cookie = cookie;
        this.fileName = fileName;
    }

    public int getSize() {
        return size;
    }

    public int getStatus() {
        return status;
    }

    public void pause() {
        status = PAUSED;
        stateChanged();
    }

    public void resume() {
        status = DOWNLOADING;
        stateChanged();
        download();
    }

    public void cancel() {
        status = CANCELLED;
        stateChanged();
    }

    private void error() {
        System.out.println("Download Error");
        status = ERROR;
        stateChanged();
    }

    public Thread download() {
        Thread thread = new Thread(this);
        return thread;
    }

    @Override
    public void run() {
        RandomAccessFile file = null;
        InputStream stream = null;

        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            System.out.println("开始下载: | Start downloading: " + fileName + "  \t (Thread:" + Thread.currentThread().getId() + " Launched)");

            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586");
            connection.setRequestProperty("connection", "keep-alive");
            connection.setRequestMethod("GET");
            connection.setRequestProperty("accept-encoding", "identity");
            connection.setConnectTimeout(50000);
            String tempCookie = "";
            for (Object property : cookie.keySet()) {
                tempCookie = tempCookie + (String) property + "=" + cookie.get(property) + ";";
            }
            connection.setRequestProperty("cookie", tempCookie);

            connection.connect();

            if (connection.getResponseCode() / 100 != 2) {
                System.out.println("Response Code : " + connection.getResponseCode());
                error();
            }

            int contentLength = connection.getContentLength();
            if (contentLength < 1) {
                System.out.println("Content Length : " + connection.getContentLength());
                error();
            }

            if (size == -1) {
                size = contentLength;
            }

            createFolder(destination.replace("/", "_"));
            file = new RandomAccessFile(destination.replace("/", "_") + fileName.replace("/", "_").replace("\\", "_"), "rw");

            stream = connection.getInputStream();
            while (status == DOWNLOADING) {
                byte buffer[];
                if (size > MAX_BUFFER_SIZE) {
                    buffer = new byte[MAX_BUFFER_SIZE];
                } else {
                    buffer = new byte[size];
                }

                int read = stream.read(buffer);
                if (read == -1) {
                    break;
                }

                file.write(buffer, 0, read);
            }

            if (status == DOWNLOADING) {
                status = COMPLETE;
                System.out.println("下载完成: | Download Completed: " + this.fileName + "\t (Thread:" + Thread.currentThread().getId() + " Terminated)");
            }
        } catch (Exception e) {
            e.printStackTrace();
            error();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (Exception e) {
                }
            }

            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                }
            }
            stateChanged();
        }
    }

    private void stateChanged() {

        setChanged();
        notifyObservers(Thread.currentThread());
    }

    public static boolean createFolder(String parentFolderName) {

        File file = new File(parentFolderName);

        if (file.exists()) {
            return false;
        }

        if (file.mkdirs()) {
            System.out.println("已建立文件夹: | Folder has been built: " + file.getAbsolutePath());
            return true;
        } else {
            System.out.println("建立文件夹失败: | Building Folder failure " + file.getAbsolutePath());
            return false;
        }
    }
}
