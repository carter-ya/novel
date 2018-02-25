package com.ifengxue.novel;

import com.ifengxue.novel.chapter.Chapter;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 小说详情接口
 */
public interface Novel {

  /**
   * 小说名称
   */
  String getTitle();

  /**
   * 作者名
   *
   * @return 如果小说作者未知则返回null
   */
  String getAuthor();

  /**
   * 小说简介
   *
   * @return 如果小说简介未知则返回null
   */
  String getSummary();

  /**
   * 获取小说最后更新时间
   *
   * @return 如果最后更新时间未知则返回null
   */
  LocalDateTime getLastModifyTime();

  /**
   * 获取封皮图像地址
   *
   * @return 如果封皮图像地址未知则返回null
   */
  String getCoverImageUrl();

  /**
   * 获取总字数
   *
   * @return 如果字数未知则返回-1
   */
  int getWordCount();

  /**
   * 获取总的章节数
   *
   * @return 如果章节数未知则返回-1
   */
  int getChapterCount();

  /**
   * 获取章节列表
   */
  List<Chapter> getChapters();
}
