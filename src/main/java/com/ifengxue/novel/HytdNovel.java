package com.ifengxue.novel;

import lombok.experimental.Delegate;

/**
 * 黄易天地实现<a href="https://www.hytd.com">黄易天地</a>
 */
public class HytdNovel implements Novel {

  @Delegate
  private BookTxtNovel bookTxtNovel;

  public HytdNovel(String chapterListUr) {
    bookTxtNovel = new BookTxtNovel(chapterListUr);
  }
}
