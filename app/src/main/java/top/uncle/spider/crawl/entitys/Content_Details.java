package top.uncle.spider.crawl.entitys;

import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/*
 * 抓取章节内容页面
 * 章节标题
 * 章节内容 
 * 上一章节链接
 * 下一章节链接
 */
public class Content_Details implements Serializable {
	private static final long serialVersionUID = -5722729769690572176L;
	private String content;
	private String title;
	private String next;
	private String pre;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getNext() {
		return next;
	}
	public void setNext(String next) {
		this.next = next;
	}
	public String getPre() {
		return pre;
	}
	public void setPre(String pre) {
		this.pre = pre;
	}
	@Override
	public String toString() {
		return "Content_Details [content=" +StringUtils.abbreviate( content, 30) + ", title=" + title + ", next=" + next + ", pre=" + pre + "]";
	}
	
}
