package com.ifengxue.novel;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.experimental.Delegate;

/**
 * 黄易天地实现<a href="https://www.xhytd.com">黄易天地</a>
 */
public class HytdNovel implements Novel {

  @Delegate(excludes = Exclude.class)
  private BookTxtNovel bookTxtNovel;

  public HytdNovel(String chapterListUr) {
    bookTxtNovel = new BookTxtNovel(chapterListUr, getCharset());
  }

  @Override
  public Charset getCharset() {
    return StandardCharsets.UTF_8;
  }

  interface Exclude {

    Charset getCharset();
  }
}
