package top.uncle.spider.crawl.interfaces;


import java.util.List;

import top.uncle.spider.crawl.entitys.Chapter;


public interface ImplChapterSpider {
	//通过URL返回章节列表的list
	public List<Chapter> getChapterlist(String url);
}
