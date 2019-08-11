package com.ifengxue.novel;

import com.ifengxue.novel.chapter.BiqugeChapterBody;
import com.ifengxue.novel.chapter.BookTxtChapterBody;
import com.ifengxue.novel.chapter.ChapterBody;
import com.ifengxue.novel.chapter.HytdChapterBody;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 工厂
 */
public class NovelFactory {

  private static final Map<String, Constructor<? extends Novel>> NOVEL_CONSTRUCTOR_MAP = new HashMap<>();
  private static final Map<String, Constructor<? extends ChapterBody>> CHAPTER_BODY_CONSTRUCTOR_MAP = new HashMap<>();

  static {
    register("www.booktxt.net", BookTxtNovel.class, BookTxtChapterBody.class);// 顶点小说
    register("www.hytd.com", HytdNovel.class, HytdChapterBody.class);// 黄易天地
    register("www.biquge.com.tw", BiqugeNovel.class, BiqugeChapterBody.class);// 笔趣阁
  }

  /**
   * 注册小说站点的实现
   *
   * @param host 域名.如 www.booktxt.net;www.baidu.com
   * @param novelClass 实现了{@link Novel}接口的实现
   * @param chapterBodyClass 实现了{@link ChapterBody}接口的实现
   */
  public static synchronized void register(String host, Class<? extends Novel> novelClass,
      Class<? extends ChapterBody> chapterBodyClass) {
    Constructor<? extends Novel> novelConstructor;
    try {
      novelConstructor = novelClass.getConstructor(String.class);
    } catch (NoSuchMethodException e) {
      throw new NovelExeception(novelClass.getName() + "需要一个带字符串参数的构造器");
    }
    Constructor<? extends ChapterBody> chapterBodyConstructor;
    try {
      chapterBodyConstructor = chapterBodyClass.getConstructor(String.class);
    } catch (NoSuchMethodException e) {
      throw new NovelExeception(chapterBodyClass.getName() + "需要一个带字符串参数的构造器");
    }
    NOVEL_CONSTRUCTOR_MAP.put(host.toLowerCase(), novelConstructor);
    CHAPTER_BODY_CONSTRUCTOR_MAP.put(host.toLowerCase(), chapterBodyConstructor);
  }

  /**
   * 获取小说实例
   *
   * @param url 一般是章节列表URL
   */
  public static Novel getNovelInstance(String url) {
    try {
      return Optional.ofNullable(NOVEL_CONSTRUCTOR_MAP.get(new URL(url).getHost().toLowerCase()))
          .map(constructor -> newInstance(constructor, url)).orElseThrow(() -> new NovelExeception("无法识别的URL:" + url));
    } catch (MalformedURLException e) {
      throw new NovelExeception("不合法的URL:" + url, e);
    }
  }

  /**
   * 获取章节正文实例
   *
   * @param url 章节正文URL
   */
  public static ChapterBody getChapterBodyInstance(String url) {
    try {
      return Optional.ofNullable(CHAPTER_BODY_CONSTRUCTOR_MAP.get(new URL(url).getHost().toLowerCase()))
          .map(constructor -> newInstance(constructor, url)).orElseThrow(() -> new NovelExeception("无法识别的URL:" + url));
    } catch (MalformedURLException e) {
      throw new NovelExeception("不合法的URL:" + url, e);
    }
  }

  private static <T> T newInstance(Constructor<T> constructor, String url) {
    try {
      return constructor.newInstance(url);
    } catch (Exception e) {
      throw new NovelExeception(e);
    }
  }
}
