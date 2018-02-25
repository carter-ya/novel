package com.ifengxue.novel.chapter;

import com.ifengxue.novel.Novel;

/**
 * 章节正文
 */
public interface ChapterBody {

  String SPACE_TAG = "#space";
  String LINE_BREAK_TAG = "#lineBreak";

  /**
   * 获取小说信息
   *
   * @return 如果不能获取小说信息则返回null
   */
  Novel getNovel();

  /**
   * 获取章节信息
   *
   * @return 如果不能获取章节信息则返回null
   */
  Chapter getChapter();

  /**
   * 获取前一章节URL
   *
   * @return 如果没有前一章地址则返回null
   */
  String getPrevChapterUrl();

  /**
   * 获取后一章节URL
   *
   * @return 如果没有后一章节地址则返回null
   */
  String getNextChapterUrl();

  /**
   * 获取章节目录地址
   */
  String getChapterListUrl();

  /**
   * 获取章节正文
   */
  String getChapterBodyText();
}
