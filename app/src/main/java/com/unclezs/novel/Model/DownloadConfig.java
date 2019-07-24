package com.unclezs.novel.Model;

/*
 *@author unclezs.com
 *@date 2019.07.05 19:33
 */
public class DownloadConfig {
    private String path;//下载路径
    private int perThreadDownNum;//每个西线程多少章节
    private Integer sleepTime;//每章节延迟

    public DownloadConfig(String path, int perThreadDownNum, Integer sleepTime) {
        this.path = path;
        this.perThreadDownNum = perThreadDownNum;
        this.sleepTime = sleepTime;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public int getPerThreadDownNum() {
        return perThreadDownNum;
    }

    public void setPerThreadDownNum(int perThreadDownNum) {
        this.perThreadDownNum = perThreadDownNum;
    }

    public Integer getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(Integer sleepTime) {
        this.sleepTime = sleepTime;
    }


    @Override
    public String toString() {
        return "DownloadConfig{" +
                "path='" + path + '\'' +
                ", perThreadDownNum='" + perThreadDownNum + '\'' +
                ", sleepTime=" + sleepTime +
                '}';
    }
}
