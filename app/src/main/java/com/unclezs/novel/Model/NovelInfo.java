package com.unclezs.novel.Model;

import java.io.Serializable;

/**
 * Created by Uncle
 * 2019.07.20.
 */
public class NovelInfo implements Serializable {
    private String title;//标题
    private String author;//作者
    private String url;//来源
    private String imgUrl;//图片地址

    public NovelInfo() {
    }

    public NovelInfo(String title, String author, String url, String imgUrl) {
        this.title = title;
        this.author = author;
        this.url = url;
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @Override
    public String toString() {
        return "NovelInfo{" +
                "title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", url='" + url + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                '}';
    }
}
