package firstspider.UI;

import java.io.*;
import javax.swing.*;
import javax.swing.text.*;

public class ConsoleTextArea extends JTextArea {

    public ConsoleTextArea(InputStream[] inStreams) {
        for (int i = 0; i < inStreams.length; ++i) {
            startConsoleReaderThread(inStreams[i]);
        }
    }

    public ConsoleTextArea() throws IOException {
        final LoopedStreams ls = new LoopedStreams(); // 重定向System.out和System.err
        PrintStream ps = new PrintStream(ls.getOutputStream());
        System.setOut(ps);
        //System.setErr(ps);
        startConsoleReaderThread(ls.getInputStream());
    }

    private void startConsoleReaderThread(InputStream inStream) {

        final BufferedReader br = new BufferedReader(new InputStreamReader(inStream));
        new Thread(new Runnable() {
            @Override
            public void run() {
                StringBuffer sb = new StringBuffer();
                try {
                    String s;

                    while ((s = br.readLine()) != null) {
                        Document doc = getDocument();
                        boolean caretAtEnd = false;
                        caretAtEnd = getCaretPosition() == doc.getLength() ? true : false;
                        sb.setLength(0);
                        insert(sb.append(s).append('\n').toString(), 0);
                        if (caretAtEnd) {
                            setCaretPosition(0);
                        }
                        paintImmediately(getBounds());
                    }
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null, "从BufferedReader读取错误：" + e);
                    System.exit(1);
                }
            }
        }
        ).start();
    }

}

class LoopedStreams {

    private PipedOutputStream pipedOS = new PipedOutputStream();
    private boolean keepRunning = true;
    private ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream() {
        @Override
        public void close() {
            keepRunning = false;
            try {
                super.close();
                pipedOS.close();
            } catch (IOException e) {
                System.exit(1);
            }
        }
    };
    private PipedInputStream pipedIS = new PipedInputStream() {
        @Override
        public void close() {
            keepRunning = false;
            try {
                super.close();
            } catch (IOException e) {
                System.exit(1);
            }
        }
    };

    public LoopedStreams() throws IOException {
        pipedOS.connect(pipedIS);
        startByteArrayReaderThread();
    }

    public InputStream getInputStream() {
        return pipedIS;
    } // getInputStream()

    public OutputStream getOutputStream() {
        return byteArrayOS;
    } // getOutputStream()

    private void startByteArrayReaderThread() {
        new Thread(new Runnable() {
            public void run() {
                while (keepRunning) {
                    // 检查流里面的字节数
                    if (byteArrayOS.size() > 0) {
                        byte[] buffer = null;
                        synchronized (byteArrayOS) {
                            buffer = byteArrayOS.toByteArray();
                            byteArrayOS.reset(); // 清除缓冲区
                        }
                        try {
                            // 把提取到的数据发送给PipedOutputStream
                            pipedOS.write(buffer, 0, buffer.length);
                        } catch (IOException e) {

                            System.exit(1);
                        }
                    } else // 没有数据可用，线程进入睡眠状态
                    {
                        try {
                            // 每隔1秒查看ByteArrayOutputStream检查新数据
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
        }).start();
    } // startByteArrayReaderThread()
} // LoopedStreams
    
 // ConsoleTextArea
