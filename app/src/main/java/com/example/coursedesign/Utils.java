package com.example.coursedesign;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

/**
 * @BelongsProject: Spider
 * @BelongsPackage: PACKAGE_NAME
 * <p>Description: </p>
 * @author: zoujian
 * @create: 2019-05-22 21:59
 * @version: 1.0
 */
public class Utils {
    private static String auto_generated_string;
    private static String password;
    private static String username;
    private static String token;
    private static String random;

    static {
        password="17441625105798";
        username="1740129420";
    }

    public void getParticularStringFromLoginPage() {
        Document document = null;
        try {
            document = Jsoup.connect("http://class.sise.com.cn:7001/sise/login.jsp").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element element = document.selectFirst("input[type=\"hidden\"]");
        String key = element.attr("name");
        String value = element.attr("value");
        auto_generated_string=key+"="+value;
//        Elements element1 = document.select("input[id=\"token\"]");
//        token=element1.attr("value");
//        Elements element2 = document.select("input[id=\"random\"]");
//        random=element2.attr("value");
    }

    public void passLoginData(String username,String password){
        Utils.username=username;
        Utils.password=password;
    }

    public String getPostData(){
        getParticularStringFromLoginPage();
//        return auto_generated_string+"&"
//                +"random="+random
//                +"token="+token
//                +"username="+username+"&"
//                +"password="+password;
        return auto_generated_string+"&"
                +"username="+username+"&"
                +"password="+password;
    }


}
