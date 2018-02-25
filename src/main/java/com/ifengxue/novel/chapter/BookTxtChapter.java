package com.ifengxue.novel.chapter;

/**
 * 顶点小说章节
 */
public class BookTxtChapter extends AbstractChapter {

  private ChapterBody chapterBody;

  @Override
  public synchronized ChapterBody openChapter() {
    if (chapterBody == null) {
      chapterBody = new BookTxtChapterBody(url);
    }
    return chapterBody;
  }
}
