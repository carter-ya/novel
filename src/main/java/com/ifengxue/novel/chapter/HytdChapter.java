package com.ifengxue.novel.chapter;

public class HytdChapter extends AbstractChapter {

  private volatile ChapterBody chapterBody;

  @Override
  public ChapterBody openChapter() {
    if (chapterBody == null) {
      synchronized (this) {
        if (chapterBody == null) {
          chapterBody = new HytdChapterBody(url);
        }
      }
    }
    return chapterBody;
  }
}
