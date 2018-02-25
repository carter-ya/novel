package com.ifengxue.novel.download;

/**
 * 小说下载器
 */
public interface Downloader {

  /**
   * 下载小说并保存到指定文件中
   *
   * @param chapterListUrl 章节列表地址
   * @param filename 要保存的文件
   */
  void download(String chapterListUrl, String filename);

  /**
   * 下载从指定章节开始的小说并保存到指定文件中
   *
   * @param chapterUrl 章节正文地址
   * @param filename 要保存的文件
   */
  void downloadSkipPrevChapters(String chapterUrl, String filename);
}
