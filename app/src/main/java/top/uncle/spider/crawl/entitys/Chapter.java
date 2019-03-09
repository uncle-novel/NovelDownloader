package top.uncle.spider.crawl.entitys;

import java.io.Serializable;

/*
  * 小说章节实体
 */
public class Chapter implements Serializable {
	private static final long serialVersionUID = 2848149613436865943L;
		private String url;
		private String title;
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		@Override
		public String toString() {
			return "Chapter [url=" + url + ", title=" + title + "]";
		}
}
