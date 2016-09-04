/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package firstspider.network;

import firstspider.network.GetCourse;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author bill
 */
public class GetCourse_1516 implements GetCourse {

    

    @Override
    public Map<String, String> getCourse(Map<String, String> session) {
       Connection con3 = Jsoup.connect("http://ice.xjtlu.edu.cn/my/index.php?mynumber=-2").timeout(300000);
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
            courseTitles = d2.getElementsByClass("title").select("a");     //For 15-16 Edition
        }
        Map<String, String> courseMap = new TreeMap();
        for (Element ele : courseTitles) {
             courseMap.put(ele.attr("title"), ele.attr("href"));            // For 15-16 Edition
        }
        return courseMap;
    }


    
}
