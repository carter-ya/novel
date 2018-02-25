package com.ifengxue.novel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BookTxtNovelTest {

  @Test
  public void testBookTxt() {
    String url = "http://www.booktxt.net/2_2096/";
    String title = "我是至尊";
    String author = "风凌天下";
    String firstChapterTitle = "楔子";

    Novel novel = new BookTxtNovel(url);
    assertEquals(title, novel.getTitle());
    assertEquals(author, novel.getAuthor());
    assertEquals(firstChapterTitle, novel.getChapters().get(0).getTitle());

    System.out.println(novel);
  }
}