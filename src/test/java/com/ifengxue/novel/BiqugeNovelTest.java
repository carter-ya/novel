package com.ifengxue.novel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BiqugeNovelTest {

  @Test
  public void openBiquge() {
    String url = "http://www.biquge.com.tw/11_11850/";
    String author = "辰东";
    String summary = "在破败中崛起，在寂灭中复苏。沧海成尘，雷电枯竭……";
    Novel novel = new BiqugeNovel(url);
    assertEquals(author, novel.getAuthor());
    assertEquals(summary, novel.getSummary());
    assertTrue(novel.getChapterCount() > 0);
    System.out.println(novel);
  }
}