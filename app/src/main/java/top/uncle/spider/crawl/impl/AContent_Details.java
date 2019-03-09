package top.uncle.spider.crawl.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.Map;

import top.uncle.spider.crawl.entitys.Content_Details;
import top.uncle.spider.crawl.interfaces.ImplContent_Details;
import top.uncle.spider.crawl.utils.SpiderUtils;


public class AContent_Details implements ImplContent_Details {

	@Override
	public Content_Details getContent_Details(String url){
		try {
			Content_Details content_Details=new Content_Details();
			String contentss= SpiderUtils.Chaptercrawl(url);
			contentss=contentss.replaceAll("&nbsp;", " ")
					.replace("<br />","{换行}").replace("<br/>","{换行}")
					.replace("<br>","{换行}").replaceAll("&#\\d{1,8};","")
					.replace("</p>","{换行}").replace("<p>","{换行}");
			Map<String,String> context=SpiderUtils.getConText(url);
			Document doc = Jsoup.parse(contentss);
			//章节内容
			doc.setBaseUri(url);
			String content=null;
			if(SpiderUtils.getConText(url).get("content-own")=="true") {
				content=doc.select(context.get("content")).first().ownText().replace("{换行}", "\n");
			}else {
				content=doc.select(context.get("content")).text().replace("{换行}", "\n");
			}
			//章节标题
			String[] setitle =getSelector(context.get("title-chapter"));
			String title=doc.select(setitle[0]).get(Integer.parseInt(setitle[1])).text();
			//存入实体
			content_Details.setContent(content);
			content_Details.setTitle(title);
			return content_Details;
		} catch (Exception e) {
			throw new RuntimeException("内容详情解析出错,请检查爬取规则是否正确！");
		}
	}
	/*
	 * 健壮配置文件
	 * 如果规则没有写坐标，默认为0
	 */
	private String[] getSelector(String selector) {
		String[] select=new String[2];
		select[0]=selector.split("\\,")[0];
		if(selector.split("\\,").length==1) {
			select[1]="0";
		}else {
			select[1]=selector.split("\\,")[1];
		}
		return select;
	}
}
