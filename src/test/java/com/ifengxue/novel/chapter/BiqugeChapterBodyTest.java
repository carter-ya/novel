package com.ifengxue.novel.chapter;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BiqugeChapterBodyTest {

  @Test
  public void openChapterBody() {
    String url = "http://www.biquge.com.tw/11_11850/7636128.html";
    String novel = "圣墟";
    String title = "外篇二 绝世";
    ChapterBody chapterBody = new BiqugeChapterBody(url);
    assertEquals(novel, chapterBody.getNovel().getTitle());
    assertEquals(title, chapterBody.getChapter().getTitle());
    System.out.println(chapterBody);
  }

}