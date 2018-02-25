package com.ifengxue.novel.chapter;

/**
 * 小说章节接口
 */
public interface Chapter {

  /**
   * 获取章节标题
   */
  String getTitle();

  /**
   * 获取章节链接地址
   */
  String getUrl();

  /**
   * 打开章节地址并获取章节信息
   */
  ChapterBody openChapter();
}
