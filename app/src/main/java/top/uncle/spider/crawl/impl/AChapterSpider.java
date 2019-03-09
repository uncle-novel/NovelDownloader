package top.uncle.spider.crawl.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import top.uncle.spider.crawl.entitys.Chapter;
import top.uncle.spider.crawl.interfaces.ImplChapterSpider;
import top.uncle.spider.crawl.utils.SpiderUtils;


public class AChapterSpider implements ImplChapterSpider {
	//实现爬取章节列表方法接口
	@Override
	public List<Chapter> getChapterlist(String url) {
		try {
			String content= SpiderUtils.Chaptercrawl(url);
			Document doc = Jsoup.parse(content);
			doc.setBaseUri(url);
			Elements elements = doc.select(SpiderUtils.getConText(url).get("chapter-list"));
			List<Chapter> chapters = new ArrayList<>();
			for(Element element:elements) {
				Chapter chapter=new Chapter();
				chapter.setTitle(element.text());
				chapter.setUrl(element.absUrl("href"));
//				System.out.println(chapter.getTitle()+"--"+chapter.getUrl());
				chapters.add(chapter);
			}
			if(SpiderUtils.getConText(url).get("needsort").equals("true")) {
				Collections.sort(chapters,new sortsChapter());
			}
			return chapters;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	//章节列表乱序重排序（笔下文学）
	private class sortsChapter implements Comparator<Chapter> {

		@Override
		public int compare(Chapter o1, Chapter o2) {
			String o1Url=o1.getUrl();
			String o2Url=o2.getUrl();
			String o1index = o1Url.substring(o1Url.lastIndexOf('/')+1,o1Url.lastIndexOf('.'));
			String o2index = o2Url.substring(o2Url.lastIndexOf('/')+1,o2Url.lastIndexOf('.'));
			return Integer.parseInt(o1index)-Integer.parseInt(o2index);
		}
		
	}
}
