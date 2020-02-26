package com.ifengxue.novel.chapter;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.experimental.Delegate;

public class HytdChapterBody implements ChapterBody {

  @Delegate(excludes = ExcludeDelegateMethods.class)
  private BookTxtChapterBody bookTxtChapterBody;
  private HytdChapter chapter;
  private String chapterBodyText;

  public HytdChapterBody(String chapterUrl) {
    bookTxtChapterBody = new BookTxtChapterBody(chapterUrl, getCharset());
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

  @Override
  public Charset getCharset() {
    return StandardCharsets.UTF_8;
  }

  private interface ExcludeDelegateMethods {

    @SuppressWarnings("unused")
    String getChapterBodyText();

    @SuppressWarnings("unused")
    Chapter getChapter();

    @SuppressWarnings("unused")
    Charset getCharset();
  }
}
