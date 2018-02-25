package com.ifengxue.novel.chapter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BookTxtChapterBodyTest {

  @Test
  public void testChapterBody() {
    String chapterUrl = "http://www.booktxt.net/5_5212/1946401.html";
    String nextChapterUrl = "http://www.booktxt.net/5_5212/1946402.html";
    BookTxtChapterBody body = new BookTxtChapterBody(chapterUrl);
    assertTrue(body.getPrevChapterUrl() == null);
    assertEquals(nextChapterUrl, body.getNextChapterUrl());
    assertTrue(body.getChapter() != null);
    System.out.println(body);
  }
}