package com.example.coursedesign;

import android.widget.LinearLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @BelongsProject: Spider
 * @BelongsPackage: PACKAGE_NAME
 * <p>Description: ·</p>
 * @author: zoujian
 * @create: 2019-05-22 12:50
 * @version: 1.0
 */
public class Server {

    //post的网址
    private static String PATH = "http://class.sise.com.cn:7001/sise/login_check_login.jsp";
    static List<String> cookies;  //保存获取的cookie
    private static URL url;
    private static String STUDENTID_PATH = "http://class.sise.com.cn:7001/sise/module/student_states/student_select_class/main.jsp";
    private static String mainPageHtml;
    public static int reponse_code = -1;

    static {
        try {
            url = new URL(PATH);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public static String sendPostMessage(String post_data, String encode) {
        byte[] mdata = post_data.getBytes();
        try {
//            System.out.println("~~~~~~~~~~~~~~`" + post_data);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.setDoInput(true);//表示从服务器获取数据
            connection.setDoOutput(true);//表示向服务器写数据
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setRequestProperty("Host", "class.sise.com.cn:7001");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
//            connection.setRequestProperty("Referer","http://class.sise.com.cn:7001/sise/loginHelper.jsp");
            connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
            connection.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8,fr;q=0.7");
            connection.connect();
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(mdata, 0, mdata.length);
            int responseCode = connection.getResponseCode();
            Server.reponse_code = responseCode;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // 获取返回的cookie
                cookies = connection.getHeaderFields().get("Set-Cookie");
                System.out.println("cookie:" + cookies);
                mainPageHtml = changeInputeStream(connection.getInputStream(), encode);
                return mainPageHtml;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String changeInputeStream(InputStream inputStream, String encode) {
        //通常叫做内存流，写在内存中的
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        String result = "";
        if (inputStream != null) {
            try {
                while ((len = inputStream.read(data)) != -1) {
                    data.toString();
                    outputStream.write(data, 0, len);
                }
                //result是在服务器端设置的doPost函数中的
                result = new String(outputStream.toByteArray(), encode);
                outputStream.flush();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return result;
    }

    public String getRawCurriculumData(String encode) {
        InputStream inputStream = null;
        String PATH = "http://class.sise.com.cn:7001/sise/module/student_schedular/student_schedular.jsp";
        try {
            URL url = new URL(PATH);
            if (url != null) {
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.setRequestMethod("GET");
                for (String cookie : cookies) {
                    httpURLConnection.addRequestProperty("Cookie", cookie.split(";", 2)[0]);//把cookie添加到请求属性。
                }

                int responsecode = httpURLConnection.getResponseCode();
                if (responsecode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                    return changeInputeStream(inputStream, encode);//把输入流转换成String
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public HashMap<String, String> parseRawCurriculumData(String html) {
        HashMap<String, String> map = new HashMap<>();
        Document doc = Jsoup.parse(html);
        Elements course_table = doc.select("table:nth-child(5)");
        int rows = 9;
        int columns = 6;
        String todayis = "";
        for (int i = 2; i <= rows; i++) {
            Elements tr_element = course_table.select("tr:nth-child(" + i + ")");
            for (int j = 2; j <= columns; j++) {
                String course_key = "c" + String.valueOf((i - 1) * 2 - 1) + String.valueOf((i - 1) * 2) + String.valueOf(j - 1);
                Elements td_element = tr_element.select("td:nth-child(" + j + ")");
                String course_info = td_element.text();
                if (!course_info.isEmpty()) {
                    map.put(course_key, course_info);
//                    System.out.println(course_info);
                }
            }
        }
        CourseData.map=map;
        return map;
    }

    public String getNowWeek(String html){
        Document doc = Jsoup.parse(html);
        Element element2 = doc.selectFirst("span:contains(教学周)");
        String week = element2.text().split(" ")[1];
        return week.substring(1,week.length()-1);
    }

    public List<List<String>> parseRawCurriculumData2(String html){
        List<List<String>> lists = new ArrayList<>();
        List<String> monday = new ArrayList<>();
        List<String> tuesday = new ArrayList<>();
        List<String> wednesday= new ArrayList<>();
        List<String> thursday  = new ArrayList<>();
        List<String> friday = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        Elements course_table = doc.select("table:nth-child(5)");
        Element element1 = doc.selectFirst("span:contains(学号)");
        Element element2 = doc.selectFirst("span:contains(教学周)");
        Element element3 = doc.selectFirst("font:contains(星)");
        String myname = element1.text().split(" ")[3];
        String week = element2.text().split(" ")[1];
        String dayOfweek = element3.text().split(" ")[1];
        CourseData.myname=myname;
        CourseData.week=week;
        CourseData.dayOfWeek=dayOfweek;
        int rows = 9;
        int columns = 6;
        for (int i = 2; i <= rows; i++) {
            Elements tr_element = course_table.select("tr:nth-child(" + i + ")");
            for (int j = 2; j <= columns; j++) {
                Elements td_element = tr_element.select("td:nth-child(" + j + ")");
                String course_info = td_element.text();
                if(j==2){
                    monday.add(course_info);
                }else if (j==3){
                    tuesday.add(course_info);
                }else if(j==4){
                    wednesday.add(course_info);
                }else if(j==5){
                    thursday.add(course_info);
                }else if(j==6){
                    friday.add(course_info);
                }
            }
        }
        lists.add(monday);
        lists.add(tuesday);
        lists.add(wednesday);
        lists.add(thursday);
        lists.add(friday);
        CourseData.courselist=lists;
        return lists;
    }

    public String getStudentId(String encode, int mode) {
        InputStream inputStream;
        String PATH = STUDENTID_PATH;
        String string = "";
        try {
            URL url = new URL(PATH);
            if (url != null) {
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.setRequestMethod("GET");
                for (String cookie : cookies) {
                    httpURLConnection.addRequestProperty("Cookie", cookie.split(";", 2)[0]);//把cookie添加到请求属性。
                }

                int responsecode = httpURLConnection.getResponseCode();
                System.out.println(responsecode);
                if (responsecode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                    string = changeInputeStream(inputStream, encode);//把输入流转换成String
                }
                Document document = Jsoup.parse(string);
                String title = "";
                if (mode == 1) {
                    title = "个人信息查询";
                } else if (mode == 2) {
                    title = "考试时间查看";
                }
                Elements elements = document.select("tr[title=\"" + title + "\"]");
                Elements elements1 = elements.select("tr");
                String string1 = elements.html();
                String regex = "studentid=([^']+)";
                Matcher matcher = Pattern.compile(regex).matcher(string1);
//                System.out.println(string);
                if (matcher.find()) {
                    return matcher.group(1).trim();
                } else {
                    System.out.println("sorry!");
                    return "";
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getServerHtmlByPath(String PATH, String encode) {
        InputStream inputStream = null;
        try {
            URL url = new URL(PATH);
            if (url != null) {
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.setRequestMethod("GET");
                for (String cookie : cookies) {
                    httpURLConnection.addRequestProperty("Cookie", cookie.split(";", 2)[0]);//把cookie添加到请求属性。
                }

                int responsecode = httpURLConnection.getResponseCode();
//                System.out.println(responsecode);
                if (responsecode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                    return changeInputeStream(inputStream, encode);//把输入流转换成String
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getRawPersonalData(String encode) {
        InputStream inputStream = null;
        String PATH = "http://class.sise.com.cn:7001/SISEWeb/pub/course/courseViewAction.do?method=doMain&studentid=";
        PATH += getStudentId("gb2312", 1);
        return getServerHtmlByPath(PATH, encode);
    }

    public String getGzcode(String encode) {
        InputStream inputStream;
        String PATH = STUDENTID_PATH;
        String string = "";
        try {
            URL url = new URL(PATH);
            if (url != null) {
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setConnectTimeout(3000);
                httpURLConnection.setRequestMethod("GET");
                for (String cookie : cookies) {
                    httpURLConnection.addRequestProperty("Cookie", cookie.split(";", 2)[0]);//把cookie添加到请求属性。
                }
                int responsecode = httpURLConnection.getResponseCode();
                System.out.println(responsecode);
                if (responsecode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                    string = changeInputeStream(inputStream, encode);//把输入流转换成String
                }
                Document document = Jsoup.parse(string);
                String title = "考勤";
                Elements elements = document.select("tr[title=\"" + title + "\"]");
                Elements elements1 = elements.select("tr");
                String string1 = elements.html();
                String regex = "gzcode=([^']+)";
                Matcher matcher = Pattern.compile(regex).matcher(string1);
//                System.out.println(string);
                if (matcher.find()) {
                    return matcher.group(1).trim();
                } else {
                    System.out.println("sorry!");
                    return "";
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
//        String post_data = new Utils().getPostData();
//        String result1 = sendPostMessage(post_data,"gb2312");
//        String result=getCurriculum("gb2312");
//        getSubjectInfo(result);
//        System.out.println(result);
    }


}
