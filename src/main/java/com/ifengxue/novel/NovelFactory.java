package com.ifengxue.novel;

import com.ifengxue.novel.chapter.BookTxtChapterBody;
import com.ifengxue.novel.chapter.ChapterBody;
import java.net.MalformedURLException;
import java.net.URL;

public class NovelFactory {

  /**
   * 获取小说实例
   *
   * @param url 一般是章节列表URL
   */
  public static Novel getNovelInstance(String url) {
    String lowerCaseUrl = url.toLowerCase();
    try {
      URL u = new URL(lowerCaseUrl);
      switch (u.getHost()) {
        case "www.booktxt.net":
          return new BookTxtNovel(url);
        default:
          throw new NovelExeception("无法识别的URL:" + url);
      }
    } catch (MalformedURLException e) {
      throw new NovelExeception("不合法的URL:" + url, e);
    }
  }

  /**
   * 获取章节正文实例
   *
   * @param url 章节正文URL
   * @see #getChapterBodyInstance(String, ChapterBody)
   */
  public static ChapterBody getChapterBodyInstance(String url) {
    String lowerCaseUrl = url.toLowerCase();
    try {
      URL u = new URL(lowerCaseUrl);
      switch (u.getHost()) {
        case "www.booktxt.net":
          return new BookTxtChapterBody(url);
        default:
          throw new NovelExeception("无法识别的URL:" + url);
      }
    } catch (MalformedURLException e) {
      throw new NovelExeception("不合法的URL:" + url, e);
    }
  }

  /**
   * 获取章节正文实例
   *
   * @param url 章节正文实例
   * @param instance 实例类型
   */
  public static ChapterBody getChapterBodyInstance(String url, ChapterBody instance) {
    if (instance instanceof BookTxtChapterBody) {
      return new BookTxtChapterBody(url);
    } else {
      throw new NovelExeception("无法识别的instance:" + instance.getClass());
    }
  }
}
