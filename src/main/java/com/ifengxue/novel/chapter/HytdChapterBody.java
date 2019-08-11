package com.ifengxue.novel.chapter;

import lombok.experimental.Delegate;

public class HytdChapterBody implements ChapterBody {

  @Delegate(excludes = ExcludeDelegateMethods.class)
  private BookTxtChapterBody bookTxtChapterBody;
  private HytdChapter chapter;
  private String chapterBodyText;

  public HytdChapterBody(String chapterUrl) {
    bookTxtChapterBody = new BookTxtChapterBody(chapterUrl);
    chapterBodyText = bookTxtChapterBody.getChapterBodyText().replaceAll("\\s*.*记住本站地址：\\[.*]\\s*.*无广告！", "");
    chapter = new HytdChapter();
    chapter.setUrl(chapterUrl);
    chapter.setTitle(bookTxtChapterBody.getChapter().getTitle());
  }

  @Override
  public Chapter getChapter() {
    return chapter;
  }

  @Override
  public String getChapterBodyText() {
    return chapterBodyText;
  }

  private interface ExcludeDelegateMethods {

    @SuppressWarnings("unused")
    String getChapterBodyText();

    @SuppressWarnings("unused")
    Chapter getChapter();
  }
}
