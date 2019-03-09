package top.uncle.spider.crawl.interfaces;


import top.uncle.spider.crawl.entitys.Content_Details;

/*
 * url获取到内容页面的
 *   章节标题
 * 章节内容
 * 上一章节链接
 * 下一章节链接
 */
public interface ImplContent_Details {
	public Content_Details getContent_Details(String url) ;
}
