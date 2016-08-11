package firstspider.network;

import firstspider.local.Filename;
import firstspider.local.Downloader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.Queue;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.TreeMap;
import java.util.concurrent.ArrayBlockingQueue;

public class AddressCatcher extends Observable implements Observer, Runnable {

    private String rootPath = "\\";
    private String password = "";
    private String userName = "";
    public boolean flag = true;
    private boolean doneCatch = false;
    private final Queue threadPool = new ArrayBlockingQueue(10);
    private final List<String> addressPool = new LinkedList();
    private Map<String, String> courseMap = null;
    private Map<String, String> session = null;

    public String getRootPath() {
        return rootPath;
    }

    public Map getCourseMap() {
        return courseMap;
    }

    public void setCourseMap(Map courseMap) {
        this.courseMap = courseMap;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof Downloader) {
            Downloader dl = (Downloader) o;
            Thread th = (Thread) arg;
            if (dl.getStatus() == 2 || dl.getStatus() == 3 || dl.getStatus() == 4) {
                synchronized (threadPool) {
                    threadPool.remove(th);
                }
                if (doneCatch && threadPool.isEmpty() && addressPool.isEmpty()) {
                    flag = true;
                    stateChanged();
                    javax.swing.JOptionPane.showMessageDialog(null, "已完成。All Done. ");
                }
            }
        }
    }

    private void stateChanged() {
        setChanged();
        notifyObservers();
    }

    @Override
    public void run() {
        System.out.println("抓取线程已启动。");
        this.catchAddress();
        doneCatch = true;
        System.out.println("抓取线程已结束。");
        URL thisURL = null;
        String currentPath = null;
        String fileName = null;
        String[] values = null;
        while (!addressPool.isEmpty()) {
            values = addressPool.get(addressPool.size() - 1).split("\\|\\|\\|");
           // System.err.println(addressPool.size());
            try {
                thisURL = new URL(values[0]);
                currentPath = values[1];
                fileName = values[2];
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
            Downloader d = null;
            Thread th = null;
            try {
                d = new Downloader(thisURL, currentPath, session, fileName);
                th = d.download();
                threadPool.add(th);
                th.start();
                d.addObserver(this);
                addressPool.remove(thisURL + "|||" + currentPath + "|||" + fileName);
            } catch (IllegalStateException ies) {
            }

        }

    }

    public Map beforeCatch() {
        Map session = null;
        try {
            flag = false;
            stateChanged();
            session = this.loginer(userName, password);
            if (session == null) {
                System.out.println("Error: Please verfy your password or username.请确认你的用户名/密码。");
                flag = true;
                stateChanged();
                return null;
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println("Error: Inner I/O error. 内部I/0错误。");
            flag = true;
            stateChanged();
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error: Unknown error. 未知错误。");
            flag = true;
            stateChanged();
            return null;
        }
        this.session = session;
        return session;
    }

    private Map<String, String> loginer(String name, String password) throws IOException, Exception {
        trustAllHttpsCertificates();
        HostnameVerifier hv = (String urlHostName, SSLSession session) -> {
            System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
            return true;
        };
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
        String url = "https://ice.xjtlu.edu.cn/login/index.php";
        Connection con = Jsoup.connect(url).timeout(50000);//获取连接
        Response rs = con.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2486.0 Safari/537.36 Edge/13.10586").execute();//获取响应

        Document d1 = Jsoup.parse(rs.body());
        Map<String, String> info = new HashMap<>();
        Elements el = d1.getElementsByClass("form-input");
        Elements nameBox = el.select("[name = username]");
        nameBox.attr("value", name);
        Elements passBox = el.select("[name = password]");
        passBox.attr("value", password);
        info.put(nameBox.attr("name"), nameBox.attr("value"));
        info.put(passBox.attr("name"), passBox.attr("value"));
        Connection con2 = Jsoup.connect(url).timeout(30000);
        Response login = con2.ignoreContentType(true).method(Method.POST).data(info).cookies(rs.cookies()).execute();

        Map<String, String> map = login.cookies();
        if (map.size() > 0) {
            System.out.println("Successfully login on ice. 已成功登陆ice。");
            return map;
        } else {
            return null;
        }
    }

    public Map<String, String> getCourse() {

        Connection con3 = Jsoup.connect("http://ice.xjtlu.edu.cn/my/index.php?mynumber=-2").timeout(300000);
        Response visit = null;
        Document d2 = null;
        Elements courseTitles = null;
        try {
            visit = con3.ignoreContentType(true).method(Method.GET).cookies(session).execute();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (visit != null) {
            d2 = Jsoup.parse(visit.body());
        }
        if (d2 != null) {
            courseTitles = d2.getElementsByClass("title").select("a");
        }
        Map<String, String> courseMap = new TreeMap();
        for (Element ele : courseTitles) {
            courseMap.put(ele.attr("title"), ele.attr("href"));
        }
        this.courseMap = courseMap;
        return courseMap;

    }

    public void catchAddress() {

        URL thisURL = null;
        for (Entry<String, String> course : courseMap.entrySet()) {
            Response courseRes = null;
            try {
                courseRes = Jsoup.connect(course.getValue()).ignoreContentType(true).method(Method.GET).cookies(session).execute();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Document d3 = Jsoup.parse(courseRes.body());
            Elements section = d3.getElementsByAttributeValueContaining("id", "section-");
            String currentPath = rootPath + course.getKey() + "\\";

            for (Element sec : section) {

                String secName = "";
                try {
                    secName = sec.getElementsByClass("sectionname").first().ownText();
                } catch (NullPointerException nulle1) {
                    nulle1.printStackTrace();
                }
                currentPath = rootPath + course.getKey() + "\\" + secName + "\\";
                /*Section:文件*/
                Elements tempFile = sec.getElementsByClass("resource").select("a");
                for (Element tempFileAddr : tempFile) {
                    String tempFileAddrStr = tempFileAddr.attr("href");
                    Response innerpage = null;
                    try {
                        innerpage = Jsoup.connect(tempFileAddrStr).timeout(30000).ignoreContentType(true).method(Method.GET).cookies(session).execute();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    Document inner = Jsoup.parse(innerpage.body());
                    Element el = null;

                    try {
                        el = inner.getElementsByClass("resourceworkaround").select("a").first();
                        if (el.attr("href").contains("resource")) {
                            try {
                                thisURL = new URL(el.attr("href"));
                            } catch (MalformedURLException ex) {
                                ex.printStackTrace();
                            }
                            try {
                                Downloader d = new Downloader(thisURL, currentPath, session, el.ownText());
                                Thread th = d.download();
                                threadPool.add(th);
                                th.start();
                                d.addObserver(this);
                            } catch (IllegalStateException ies) {
                                addressPool.add(thisURL + "|||" + currentPath + "|||" + el.ownText());
                            }
                        }
                    } catch (NullPointerException e1) {
                        String fileName = "";
                        String temp = tempFileAddr.select("img").attr("src");
                        try {
                            fileName = tempFileAddr.select("span").first().ownText().replace(": ", "_");
                        } catch (NullPointerException nulle) {
                            nulle.printStackTrace();
                        }
                        if (tempFileAddrStr.contains("resource")) {
                            String infix = "";
                            if (temp.contains("powerpoint")) {
                                infix = "pptx";
                            } else if (temp.contains("document")) {
                                infix = "docx";
                            } else if (temp.contains("pdf")) {
                                infix = "pdf";
                            } else {
                                infix = "";
                            }
                            try {
                                thisURL = new URL(tempFileAddrStr);
                            } catch (MalformedURLException me1) {
                                me1.printStackTrace();
                            }
                            try {
                                Downloader d = new Downloader(thisURL, currentPath, session, new Filename(fileName, infix).toString());
                                Thread th = d.download();
                                threadPool.add(th);
                                th.start();
                                d.addObserver(this);
                            } catch (IllegalStateException ies) {
                                addressPool.add(thisURL + "|||" + currentPath + "|||" + new Filename(fileName, infix).toString());
                            }

                        }
                    }
                }
                /*Section:文件夹*/
                Elements tempFolders = sec.getElementsByClass("folder").select("a");
                for (Element tempFolderAddr : tempFolders) {
                    String tempFolderAddrStr = tempFolderAddr.attr("href");
                    String foldername = "";
                    String filename = "";
                    String url = "";
                    try {
                        foldername = tempFolderAddr.select("span").first().ownText();
                    } catch (NullPointerException nulle) {
                        nulle.printStackTrace();
                    }
                    String parentPath = currentPath;
                    currentPath = currentPath + "\\" + foldername + "\\";
                    Response folderInnerPage = null;
                    try {
                        folderInnerPage = Jsoup.connect(tempFolderAddrStr).timeout(5000).ignoreContentType(true).method(Method.GET).cookies(session).execute();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    Document inner2 = Jsoup.parse(folderInnerPage.body());
                    Elements ele = inner2.getElementsByClass("fp-filename-icon");
                    for (Element elt : ele) {
                        try {
                            filename = elt.getElementsByClass("fp-filename").first().ownText();
                            url = elt.select("a").attr("href");
                        } catch (NullPointerException nulle1) {
                            nulle1.printStackTrace();
                        }
                        if (!url.equals("") && !filename.equals("")) {
                            try {
                                thisURL = new URL(url);
                            } catch (MalformedURLException ex) {
                                ex.printStackTrace();
                            }
                            try {
                                Downloader d = new Downloader(thisURL, currentPath, session, filename);
                                Thread th = d.download();
                                threadPool.add(th);
                                th.start();
                                d.addObserver(this);
                            } catch (IllegalStateException ies) {
                                addressPool.add(thisURL + "|||" + currentPath + "|||" + filename);
                            }

                        }
                    }
                    currentPath = parentPath;
                }
            }
        }
    }

    private static void trustAllHttpsCertificates() throws Exception {
        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
        javax.net.ssl.TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
                .getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
                .getSocketFactory());
    }

    static class miTM implements javax.net.ssl.TrustManager,
            javax.net.ssl.X509TrustManager {

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(
                java.security.cert.X509Certificate[] certs) {
            return true;
        }

        @Override
        public void checkServerTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
        }

        @Override
        public void checkClientTrusted(
                java.security.cert.X509Certificate[] certs, String authType)
                throws java.security.cert.CertificateException {
        }
    }
//    private static Map<String,String> recursiveFolder(int level,Elements fileInFolder,String infolderName){
//        Map<String, String> folderTempMap = new HashMap<>();
//        if (level>=0){
//            
//            
//            for (Element ele:fileInFolder){
//             String name ="";
//            try{
//             name = ele.getElementsByClass("fp-filename").first().text();
//            }catch(NullPointerException nulle1){
//            
//            }
//           
//            String url="";
//            try{
//            url = ele.select("a").attr("href");
//            }
//            catch (NullPointerException nulle2){
//            if(!url.equals("")){
//            FileOperations.createFolder(infolderName+"\\", name);
//           // fileInFolder  = fileInFolder.getElementById("ygtv"+(++level));
//            infolderName = infolderName+"\\"+name;
//            recursiveFolder(level,fileInFolder,infolderName);
//            }            
//            }
//            
//            if(!url.equals("")&&!name.equals(""))
//                folderTempMap.put(name,url);
//           
//            }
//            level--;
//        }
//             
//      System.out.println(folderTempMap);                
//    return folderTempMap;
//    
//    }
}
