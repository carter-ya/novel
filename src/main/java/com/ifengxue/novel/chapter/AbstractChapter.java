package com.ifengxue.novel.chapter;

import lombok.Data;

@Data
public abstract class AbstractChapter implements Chapter {

  protected String title;
  protected String url;

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public String getUrl() {
    return url;
  }
}
