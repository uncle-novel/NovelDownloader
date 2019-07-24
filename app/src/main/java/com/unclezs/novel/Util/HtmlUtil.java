package com.unclezs.novel.Util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HtmlUtil {
    /**
     * 相对路径转绝对路径
     *
     * @param base 根路径
     * @param url  相对路径
     * @return 绝对地址
     */
    public static String getAbsUrl(String base, String url) {
        if (!base.endsWith("/")) {
            base = base + "/";
        }
        String absurl = "";
        try {
            URL fromurl = new URL(base);
            absurl = new URL(fromurl, url).toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return absurl;
    }

    /**
     * 获取网页编码
     *
     * @param html 解码的网页源码（正则匹配<meta>标签的编码）
     * @return 编码格式
     */
    public static String getEncode(String html) {
        String code = "gbk";
        try {
            Pattern r = Pattern.compile("charset=[\"\']{0,1}([\\w\\-]{2,8}?)[\"\']");
            Matcher m = r.matcher(html);
            while (m.find()) {
                if (m.group(1).length() > 2) {
                    code = m.group(1);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return code.replace("\"", "").trim();
    }

    public static String getHtml(final String url, String charset) throws IOException {
        String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36";

        //延迟与重连
        URL u=new URL(url);
        HttpURLConnection connection= (HttpURLConnection) u.openConnection();
        connection.setInstanceFollowRedirects(true);//允许重定向
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.addRequestProperty("User-Agent",userAgent);
        InputStream is=connection.getInputStream();
        InputStreamReader reader = new InputStreamReader(is,charset);
        BufferedReader bufferedReader=new BufferedReader(reader);
        StringBuffer buffer=new StringBuffer();
        String s;
        while ((s=bufferedReader.readLine())!=null){
            buffer.append(s);
            buffer.append("\r\n");
        }
        return buffer.toString();
    }
}
