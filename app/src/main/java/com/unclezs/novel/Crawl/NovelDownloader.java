package com.unclezs.novel.Crawl;


import com.unclezs.novel.Adapter.DownloadAdapter;
import com.unclezs.novel.Model.DownloadConfig;
import com.unclezs.novel.Util.CharacterUtil;
import com.unclezs.novel.Util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 文本小说下载器
 */
public class NovelDownloader implements DownloadAdapter {
    private List<String> list;
    private List<String> nameList;
    private DownloadConfig config;
    private NovelSpider spider;
    private List overNum = Collections.synchronizedList(new ArrayList<>());//完成数量
    private ExecutorService service;
    private String imgPath;//缩略图
    private String path;//下载位置
    private List<Boolean> isShutdown = new ArrayList<>();//监听停止
    private boolean stoped = false;
    private String text="";

    public NovelDownloader(List<String> list, List<String> nameList, DownloadConfig config, NovelSpider spider) {
        this.list = list;
        this.config = config;
        this.spider = spider;
        this.nameList = nameList;
        //爬取图片
        this.imgPath = spider.getConfig().get("imgUrl");
    }

    @Override
    public void start() {
        //获取目录
        String tempPath = config.getPath() + "/block/" + spider.getConfig().get("title") + "/";//分块目录位置
        path = config.getPath();//下载位置
        System.out.println(config);
        File f = new File(tempPath);
        f.mkdirs();
        //计算需要线程数量
        int threadNum = (int) Math.ceil(list.size() * 1.0 / config.getPerThreadDownNum());
        //如果不合并则单线程下载
        service = Executors.newFixedThreadPool(threadNum % 50);//最多50个线程
        //任务分块
        int st;
        int end = 0;
        List<Future> Task = new ArrayList<>();//任务监控
        for (int i = 0; i < threadNum; i++) {
            isShutdown.add(true);
            if (i == threadNum - 1) {//最后一次不足每个线程下载章节数量，则全部下载
                st = end;
                end = list.size();
            } else {//每次增加配置数量
                st = end;
                end += config.getPerThreadDownNum();
            }
            final int taskId = i;
            final int sIndex = st;
            final int eIndex = end;
            Task.add(service.submit(() -> {
                String path;
                path = tempPath + sIndex + "-" + eIndex + ".uncle";
                //分块下载
                try (PrintWriter out = new PrintWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(new File(path)), spider.getConfig().get("charset")))) {
                    StringBuffer content;
                    for (int j = sIndex; j < eIndex; j++) {
                        if (!isShutdown.get(taskId)) {
                            out.close();
                            return null;
                        }
                        content = new StringBuffer();
                        for (int k = 0; k < 10; k++) {//重试10次
                            try {
                                content.append(spider.getContent(list.get(j), spider.getConfig().get("charset")));
                                break;
                            } catch (Exception e) {
                                if (k == 9) {
                                    System.out.println("下载失败" + list.get(j));
                                }
                                Thread.sleep(config.getSleepTime()*1000);
                            }
                        }
                        if (spider.getConf().isNcrToZh()) {//NCR转中文
                            String tmp = CharacterUtil.NCR2Chinese(content.toString());
                            content = new StringBuffer();
                            content.append(tmp);
                        }
                        if (spider.getConf().isTraToSimple()) {//繁体转简体
                            String tmp = CharacterUtil.traditional2Simple(content.toString());
                            content = new StringBuffer();
                            content.append(tmp);
                        }
                        out.println(nameList.get(j));
                        out.println(content.toString());
                        out.flush();
                        overNum.add(j);
                        Thread.sleep(config.getSleepTime()*1000);
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                return null;
            }));
        }
        service.shutdown();
        for (Future future : Task) {
            try {
                future.get();
            } catch (Exception e) {
                System.out.println("线程异常终止");
            }
        }
        FileUtil.mergeFiles(spider.getConfig().get("title"), config.getPath(), tempPath, spider.getConfig().get("charset"));
    }

    //已经下载数量
    @Override
    public int getOverNum() {
        return overNum.size();
    }

    //停止下载
    @Override
    public void stop() {
        this.stoped = true;
        if (service != null) {
            //全部标志为停止
            for (int i = 0; i < isShutdown.size(); i++) {
                isShutdown.set(i, false);
            }
            service.shutdown();
            service.shutdownNow();
            new File(config.getPath() + "/block/" + spider.getConfig().get("title")).delete();
            System.gc();
        }
    }

    public boolean isStoped() {
        return stoped;
    }

    //章节总数量
    @Override
    public int getMaxNum() {
        return list.size();
    }

    //获取爬虫
    public NovelSpider getSpider() {
        return spider;
    }

    //获取下载配置
    public DownloadConfig getConfig() {
        return config;
    }

    @Override
    public String getImgPath() {
        return imgPath;
    }

    @Override
    public String getTitle() {
        return spider.getConfig().get("title");
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getType() {
        return "文本文件";
    }

    @Override
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
