package com.ifengxue.novel.chapter;

public class BiqugeChapter extends AbstractChapter {

  private ChapterBody chapterBody;

  public BiqugeChapter(String url, String title) {
    this.url = url;
    this.title = title;
  }

  @Override
  public ChapterBody openChapter() {
    return null;
  }
}
