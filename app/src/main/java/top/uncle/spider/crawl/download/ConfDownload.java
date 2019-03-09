package top.uncle.spider.crawl.download;

public class ConfDownload {
	private String path;
	private int size;
	private int tryTimes;
	private final int DEFAULT_SIZE=100;
	private final int DEFAULT_TRYTIMES=5;
	public ConfDownload() {
		this.size=DEFAULT_SIZE;
		this.tryTimes=DEFAULT_TRYTIMES;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getTryTimes() {
		return tryTimes;
	}
	public void setTryTimes(int tryTimes) {
		this.tryTimes = tryTimes;
	}
}
