package firstspider.network;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetCourse_1617 implements GetCourse {

    @Override
    public Map<String, String> getCourse(Map<String, String> session) {
        Connection con3 = Jsoup.connect("http://ice.xjtlu.edu.cn/my").timeout(300000);
        Connection.Response visit = null;
        Document d2 = null;
        Elements courseTitles = null;
        try {
            visit = con3.ignoreContentType(true).method(Connection.Method.GET).cookies(session).execute();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (visit != null) {
            d2 = Jsoup.parse(visit.body());
        }
        if (d2 != null) {
            courseTitles = d2.getElementsByClass("coursebox").select("h3");   // For 16-17 Edition
        }
        Map<String, String> courseMap = new TreeMap();
        for (Element ele : courseTitles) {
            courseMap.put(ele.select("a").attr("title"), ele.select("a").attr("href"));    // For 16-17 Edition
        }
        return courseMap;
    }

}
