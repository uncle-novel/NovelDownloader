package top.uncle.spider.crawl.download;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import top.uncle.spider.SwipeListLayout;
import top.uncle.spider.crawl.entitys.Chapter;


public final class GlobalValue {
	public static List<String> progress=new ArrayList<>();
	public static List<Chapter> list=null;
	public static int num=0;
	public static boolean ondownloading=true;
	public static Set<SwipeListLayout> sets=new HashSet<>();
}
