package top.uncle.spider.crawl.entitys;

/**
 * Created by uncle on 2018/11/25.
 */
public class NovelInfo {
    private String url;
    private String name;
    private String author;
    private String from;
    private String addtime;
    private int lastchapter;
    private String istop;
    private int nowlct;
    private int nowst;

    public int getNowlct() {
        return nowlct;
    }

    public void setNowlct(int nowlct) {
        this.nowlct = nowlct;
    }

    public int getNowst() {
        return nowst;
    }

    public void setNowst(int nowst) {
        this.nowst = nowst;
    }

    public String getIstop() {
        return istop;
    }

    public void setIstop(String istop) {
        this.istop = istop;
    }

    public NovelInfo() {
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }

    public int getLastchapter() {
        return lastchapter;
    }

    public void setLastchapter(int lastchapter) {
        this.lastchapter = lastchapter;
    }
}
