package top.uncle.spider.crawl.interfaces;


import top.uncle.spider.crawl.download.ConfDownload;

public interface ImplDownload {
	//传入章节列表URL返回下载文件路径
	public String download(String url, ConfDownload conf, String novelname);
	
	
}
