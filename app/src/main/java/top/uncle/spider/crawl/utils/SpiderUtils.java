package top.uncle.spider.crawl.utils;

import android.annotation.TargetApi;
import android.os.Build;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class SpiderUtils {
	private static final Map<String,Map<String,String>> CONTEXT=new HashMap<>();
	static{
		init();
	}
	private SpiderUtils() {}
	@SuppressWarnings("unchecked")
	//初始化，解析配置rule.XMl
	private static void init(){
		SAXReader reader=new SAXReader();
		try {
			URL xml=new URL("https://unclez.top/novelconf/rule.xml");
			HttpURLConnection httpConn = (HttpURLConnection) xml.openConnection();
			httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
			httpConn.setRequestProperty("Charset","utf-8");
			httpConn.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
			httpConn.setConnectTimeout(10000);
			httpConn.setReadTimeout(10000);
			InputStream inputStream = httpConn.getInputStream();
			Document doc = reader.read(inputStream);
			//获取根节点sites
			Element root=doc.getRootElement();
			//获取所有site
			List<Element> sites = root.elements("site");
			for(Element site:sites) {
				List<Element> slist=site.elements();
				Map<String, String> nodes=new HashMap<>();
				for(Element node:slist) {
					String key=node.getName();
					String value=node.getTextTrim();
					nodes.put(key, value);
				}
				CONTEXT.put(nodes.get("url"),nodes);
		    }
		}catch (Exception e) {
			throw new RuntimeException("xml配置读取失败");
		}
	}
	//获取XML配置
	public static Map<String, String> getConText(String url){
		try {
			url=getRealUrl(url);
			return CONTEXT.get(url);
		}catch (Exception e) {
			throw new RuntimeException(url+"网站不支持");
		}
	}
	//通过url获取id的url
	public static String getRealUrl(String url){
		String[] str=url.split("/");
		url="";
		boolean flag=false;
		for(String s:str[2].split("\\.")) {
			if(flag)
				url+=s+".";
			flag=true;
		}
		url=url.substring(0, url.length()-1);
		return url;
	}
	public static String getSource(String url) throws Exception {
				HttpURLConnection httpConn = (HttpURLConnection) new URL(url).openConnection();
				httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
				httpConn.setRequestProperty("Charset","utf-8");
				httpConn.setConnectTimeout(10000);
				httpConn.setReadTimeout(10000);
				String content="";
				if(httpConn.getResponseCode()==200){
					InputStream inputStream = httpConn.getInputStream();
					BufferedReader bf=new BufferedReader(new InputStreamReader(inputStream,"utf-8"));
					String line;
					while ((line=bf.readLine())!=null){
						content+=line;
					}
				}
				return content;
			}
	public static String Chaptercrawl(String url) throws Exception {
		String encode=getConText(url).get("charset");
		HttpURLConnection httpConn = (HttpURLConnection) new URL(url).openConnection();
		httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
		httpConn.setRequestProperty("Charset",encode);
		httpConn.setConnectTimeout(10000);
		httpConn.setReadTimeout(10000);
		String content="";
		if(httpConn.getResponseCode()==200){
			InputStream inputStream = httpConn.getInputStream();
			BufferedReader bf=new BufferedReader(new InputStreamReader(inputStream,encode));
			String line;
			while ((line=bf.readLine())!=null){
				content+=line;
			}
		}
		return content;
	}
	//合并多线程下载的分块文件isdel若为true则删除分块文件
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public static String mergeFiles(String name, String path, boolean isdel) {
		//过滤出分块文件
		 File[] files=new File(path+"/tmp").listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				if(pathname.getName().split("\\-").length<2)
				   return false;
				else {
					return true;
				}
			}
		});
		 Arrays.sort(files, new FileSort());
		File filetemp=new File(path+"/"+name+".txt");
		if(filetemp.exists()){
			filetemp.delete();
		}
		 try(PrintWriter out=new PrintWriter(new File(path+"/"+name+".txt"),"UTF-8")){
			 for(File file:files) {
				 BufferedReader buf=new BufferedReader(new InputStreamReader(new FileInputStream(file.getAbsolutePath()), "utf-8"));
				 String line=null;
				 while((line=buf.readLine())!=null)
				    out.println(line);
				 buf.close();
				 if(isdel)
				    file.delete();
			 }
			 return path;
		} catch (Exception e) {
			throw new RuntimeException("文件合并失败");
		}
	}
	//排序合并文件的序列
	private static class FileSort implements Comparator<File> {
		@Override
		public int compare(File o1, File o2) {
			int o1index= Integer.parseInt(o1.getName().split("\\-")[0]);
			int o2index= Integer.parseInt(o2.getName().split("\\-")[0]);
			return o1index>o2index ? 1:-1;
		}
	}
}
