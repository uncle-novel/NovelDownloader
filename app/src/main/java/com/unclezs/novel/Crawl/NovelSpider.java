package com.unclezs.novel.Crawl;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.unclezs.novel.Model.AnalysisConfig;
import com.unclezs.novel.Model.NovelInfo;
import com.unclezs.novel.Util.BloomFileter;
import com.unclezs.novel.Util.CharacterUtil;
import com.unclezs.novel.Util.HtmlUtil;
import com.unclezs.novel.Util.SortUrl;
import com.unclezs.novel.Util.UrlFilter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 小说解析器
 * Created by Uncle
 * 2019.07.20.
 */
public class NovelSpider implements Serializable {
    private AnalysisConfig conf;//配置信息
    private String defCharset = "utf-8";
    private String novelTitle = "uncle小说";
    private String imgUrl = "";
    private String sign = "[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……& amp;*（）——+|{}【】‘；：”“’。，、？|-]";//标点符号

    public NovelSpider() {
    }

    public NovelSpider(AnalysisConfig conf) {
        this.conf = conf;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    //获取解析配置
    public void setConf(AnalysisConfig conf) {
        this.conf = conf;
    }

    public AnalysisConfig getConf() {
        return conf;
    }

    //查询小说列表
    public List<NovelInfo> queryNovelList(String name) throws IOException {
        String jsonText = HtmlUtil.getHtml("https://unclezs.com:8443/novel/novel/info?name=" + name, "utf-8");
        Log.i("信息", jsonText);
        List<NovelInfo> list = JSON.parseArray(jsonText, NovelInfo.class);
        if (list == null) return null;
        String image = crawlDescImage(name);//抓取图片
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setImgUrl(image);
        }
        return list;
    }

    //抓小说图片
    public String crawlDescImage(String name) {
        try {
            name = URLEncoder.encode(name, "UTF-8");
            String html = HtmlUtil.getHtml("https://www.qidian.com/search?kw=" + name, "UTF-8");
            Document document = Jsoup.parse(html);
            document.setBaseUri("http:");
            Element element = document.select(".res-book-item").first();
            String url = element.select("img").first().absUrl("src");
            return url;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    //获取小说名字
    public String getTitle(String html) {
        String title;
        Pattern pattern = Pattern.compile("(.{1,10}?)最新");
        Document document = Jsoup.parse(html);
        Element element = document.select("title").first();
        String text = element.text().trim();
        Matcher m = pattern.matcher(text);
        if (m.find()) {
            title = m.group(1);
        } else {
            title = text.substring(0, text.length() > 10 ? 10 : text.length());
        }
        return title.replaceAll(sign, "");
    }

    //获取章节标题和编码
    public Map<String, String> getConfig() {
        Map<String, String> conf = new HashMap<>();
        conf.put("charset", defCharset);
        conf.put("title", novelTitle);
        conf.put("imgUrl", imgUrl);
        return conf;
    }

    // 获取章节列表
    public Map<String, String> getChapterList(String url) throws IOException {
        List<String> urls = new ArrayList<>();//存url
        Map<String, String> title = new HashMap<>();//存标题
        Map<String, String> chapter = new LinkedHashMap<>();//存章节标题（有序）
        String charset = "gbk";//默认编码
        BloomFileter filter = new BloomFileter(BloomFileter.MisjudgmentRate.HIGH, 10000, null);//布隆过滤器去重复url
        //抓取源码自动识别网页编码
        String html = HtmlUtil.getHtml(url, charset);
        charset = HtmlUtil.getEncode(html);
        if (!(charset.toLowerCase().equals("gbk"))) {
            html = HtmlUtil.getHtml(url, charset);
        }
        //根据用户输入章节头尾删减html
//        html = getDelHtml(conf.getChapterHead(), conf.getChapterTail(), html);
        //解析章节列表的所有url
        Document document = Jsoup.parse(html);
        document.setBaseUri(url);
        Elements aTags = document.select("a");
        for (Element e : aTags) {
            String href = e.absUrl("href");
            if (!"".equals(href.trim()) && !"".equals(e.text().trim()) && !filter.addIfNotExist(href)) {//剔除空标签、布隆去重
                urls.add(href);
                title.put(href, e.text().trim());
            }
        }
        //过滤出不需要的
        if (conf.isChapterFilter()) {
            urls = UrlFilter.getChapterFilterRes(urls);
        }
        //乱序重排
        if (conf.isChapterSort()) {
            Collections.sort(urls, new SortUrl());
        }
        //预处理完后放入map集合
        for (String s : urls) {
            String names = title.get(s);
            chapter.put(s, names);
        }
        //更新题目与小说名字
        novelTitle = getTitle(html);
        imgUrl=crawlDescImage(novelTitle);
        defCharset = charset;
        return chapter;
    }

    //获取正文内容
    public String getContent(String url, String charset) throws IOException {
        StringBuffer content = new StringBuffer();
        String ch_punctuation = "~\\u000A\\u0009\\u00A0\\u0020\\u3000\\uFEFF";//中文标点符号
        String unicode_azAZ09 = "\\uFF41-\\uFF5a\\uFF21-\\uFF3a\\uFF10-\\uFF19";//unicode符号
        String chinese = "\\u4E00-\\u9FFF";//中文
        String html;
        //抓取网页源码
        html = HtmlUtil.getHtml(url, charset);
        if (html == null) {
            return "";
        }
        //自定义范围
//        html = getDelHtml(conf.getContentHead(), conf.getContentTail(), html);
        //两种规则爬取
        switch (conf.getRule()) {
            case "1":
                Pattern compile = Pattern.compile("[pvri\\-/\"]>([^字<*][\\pP\\w\\pN\\pL\\pM"
                        + unicode_azAZ09 + chinese + ch_punctuation
                        + "]{3,}[^字\\w>]{0,2})(<br|</p|</d|<p|<!|<d|</li)", Pattern.CASE_INSENSITIVE);
                Matcher m = compile.matcher(html);
                while (m.find()) {
                    String c;
                    if (((c = m.group(1).replaceAll("&[#\\w]{3,6}[;:]{0,1}", " ")).length()) > 0)
                        content.append(c + "\r\n");
                }
                break;
            case "2":
                Pattern compile2 = Pattern.compile("([^/][\\s\\S]*?>)([\\s\\S]*?)(<)", Pattern.CASE_INSENSITIVE);
                Matcher m2 = compile2.matcher(html);
                while (m2.find()) {
                    if ((Pattern.matches("[\\s\\S]*?[^字\\w<*][" + chinese + "]{1,}[\\s\\S]*?", m2.group(2)) || Pattern.matches("[\\s\\S]*?&#\\d{4,5}[\\s\\S]*?", m2.group(2)))
                            && (m2.group(1).endsWith("br />") || m2.group(1).endsWith("br/>") || m2.group(1).endsWith("br>") || m2.group(1).endsWith("abc\">") || m2.group(1).endsWith("p>") || m2.group(1).endsWith("v>") || m2.group(1).endsWith("->"))
                            && m2.group(2).replaceAll("&[#\\w]{3,6}[;:]{0,1}", " ").trim().length() > 0)
                        content.append(m2.group(2).replaceAll("&[#\\w]{3,6}[;:]{0,1}", " ") + "\r\n");
                }
                break;
            default:
                Whitelist whitelist = new Whitelist();
                whitelist.addTags("p", "br", "div");
                String parse = Jsoup.clean(html, whitelist);
                parse = parse.replaceAll("&[#\\w]{3,6}[;:]{0,1}", "{空格}");
                parse = parse.replaceAll("(<br/>|<br>|<br />)", "");
                parse = parse.replaceAll("(\n|\r\n|<p>)", "{换行}");
                Document document = Jsoup.parse(parse);
                Elements divs = document.select("div");
                String text = "";
                int maxLen = 0;
                for (Element div : divs) {
                    String ownText = div.ownText();
                    if (ownText.length() > maxLen) {
                        text = ownText;
                        maxLen = ownText.length();
                    }
                }
                content.append(text.replace("{换行}", "\r\n").replace("{空格}", " "));
                break;
        }
        //缩进处理
        String[] strings = content.toString().split("\n");
        content = new StringBuffer();
        for (String s : strings) {
            content.append("    ").append(s.trim()).append("\n");
        }
        //转码
        String text = content.toString();
        if (conf.isNcrToZh()) {//ncr转中文
            text = CharacterUtil.NCR2Chinese(text);
        }
        if (conf.isTraToSimple()) {//繁体转简体
            text = CharacterUtil.traditional2Simple(text);
        }
        //去广告
        for (String ad : conf.getAdStr().split("\n")) {
            text = text.replace(ad, "");
        }
        return "  " + text.trim();
    }

    //获取添加范围后的html
    private String getDelHtml(String header, String tail, String src) {
        int end = tail != null && tail.length() > 1 ? src.indexOf(tail) : src.length();
        int st = header != null && header.length() > 1 ? src.indexOf(header) : 0;
        if (st == -1)
            st = 0;
        if (end == -1)
            end = src.length();
        if (st != 0)
            st -= 5;
        if (end != src.length())
            end += 5;
        return src.substring(st, end);
    }

    public void setNovelTitle(String novelTitle) {
        this.novelTitle = novelTitle;
    }
}
