package com.unclezs.novel.Model;

/*
 *@author Uncle
 *@date 2019.06.26 21:33
 */
public class Chapter {
    private String chapterName;//章节名字
    private String chapterUrl;//章节url

    public Chapter(String chapterName, String chapterUrl) {
        this.chapterName = chapterName;
        this.chapterUrl = chapterUrl;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "chapterName='" + chapterName + '\'' +
                ", chapterUrl='" + chapterUrl + '\'' +
                '}';
    }
}
