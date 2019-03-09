package top.uncle.spider.crawl.impl;


import android.annotation.TargetApi;
import android.os.Build;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import top.uncle.spider.crawl.download.ConfDownload;
import top.uncle.spider.crawl.download.GlobalValue;
import top.uncle.spider.crawl.entitys.Chapter;
import top.uncle.spider.crawl.entitys.Content_Details;
import top.uncle.spider.crawl.interfaces.ImplDownload;
import top.uncle.spider.crawl.utils.SpiderUtils;


public class ADownload implements ImplDownload {
	//下载器
	@Override
	public String download(String url, ConfDownload conf, String novelname) {
		//章节列表
		AChapterSpider acs=new AChapterSpider();
		List<Chapter> chapters=acs.getChapterlist(url);
		GlobalValue.num=chapters.size()-1;
		GlobalValue.list=chapters;
		//加载配置设置线程量
		int size=conf.getSize();
		int MaxThread=(int) Math.ceil(chapters.size()*1.0/size);
		//小说分块放入map
		Map<String,List<Chapter>> DownloadTask=new HashMap<>();
		for(int i=0;i<MaxThread;i++) {
			int sindex=i*conf.getSize();
			int eindex=0;
			if(i==(MaxThread-1)) {
				eindex=chapters.size()-1;
			}else {
				eindex=i*(conf.getSize())+conf.getSize()-1;
			}
			DownloadTask.put(sindex+"-"+eindex, chapters.subList(sindex, eindex+1));
		}
		//目录不存在就创建目录
		File p=new File(conf.getPath()+"/tmp");
		p.delete();
		p.mkdirs();
		//把线程放入线程池
		ExecutorService eService=Executors.newFixedThreadPool(MaxThread);
		Set<String> keyset=DownloadTask.keySet();
		List<Future<String>> tasks=new ArrayList<>();
		for(String key:keyset) {
			tasks.add(eService.submit(new DownloadCallable(DownloadTask.get(key),conf.getPath()+"/tmp/"+key+".txt",conf.getTryTimes())));
		}
		eService.shutdown();
		//监控是否下载完成
		for(Future<String> future:tasks) {
			try {
				future.get();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		GlobalValue.ondownloading=false;
		//文件合并，删除分块，返回小说下载路径
		return SpiderUtils.mergeFiles(novelname,conf.getPath(), true);
	}

}

class DownloadCallable implements Callable<String>{
	private List<Chapter> chapters;
	private String path;
	private int tryTimes;
    public DownloadCallable(List<Chapter> chapters,String path,int tryTimes) {
		this.path=path;
		this.chapters=chapters;
		this.tryTimes=tryTimes;
	}
	@TargetApi(Build.VERSION_CODES.KITKAT)
	@Override
	public String call() throws Exception {
		try(PrintWriter out=new PrintWriter(new File(path))){
			for(Chapter chapter:chapters) {
				AContent_Details details=new AContent_Details();
				Content_Details contents=null;
				for(int i=0;i<tryTimes;i++) {
					try {
						contents=details.getContent_Details(chapter.getUrl());
						out.println(contents.getTitle());
						out.println(contents.getContent());
						GlobalValue.progress.add("1");
						break;
					}catch (Exception e) {
						if(i==tryTimes-1) {
							System.err.println(chapter.getTitle()+"---下载失败了");
						}
					}
				}
			}
		}catch (Exception e) {
			throw new RuntimeException("下载失败");
		}
		return path;
	}
	
}
