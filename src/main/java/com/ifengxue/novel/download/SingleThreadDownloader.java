package com.ifengxue.novel.download;

import com.ifengxue.novel.Novel;
import com.ifengxue.novel.NovelConfiguration;
import com.ifengxue.novel.NovelExeception;
import com.ifengxue.novel.NovelFactory;
import com.ifengxue.novel.chapter.Chapter;
import com.ifengxue.novel.chapter.ChapterBody;
import java.io.IOException;
import java.io.PrintWriter;
import lombok.extern.slf4j.Slf4j;

/**
 * 单线程小说下载器
 */
@Slf4j
public class SingleThreadDownloader implements Downloader {

  @Override
  public void download(String chapterListUrl, String filename) {
    Novel novel = NovelFactory.getNovelInstance(chapterListUrl);
    if (novel.getChapterCount() == 0) {// 没有任何有效章节
      try (PrintWriter writer = new PrintWriter(filename, NovelConfiguration.getInstance().getEncode())) {
        writer.write(NovelConfiguration.getInstance().getLineSeparator());
      } catch (IOException e) {
        throw new NovelExeception("写出文件失败", e);
      }
    } else {
      downloadSkipPrevChapters(novel.getChapters().get(0).getUrl(), filename);
    }
  }

  @Override
  public void downloadSkipPrevChapters(String chapterUrl, String filename) {
    ChapterBody body = NovelFactory.getChapterBodyInstance(chapterUrl);
    String lineSeparator = NovelConfiguration.getInstance().getLineSeparator();
    try (PrintWriter writer = new PrintWriter(filename, NovelConfiguration.getInstance().getEncode())) {
      while (body != null) {
        // 写章节标题
        Chapter chapter = body.getChapter();
        log.debug("章节名称[{}]，章节地址[{}]", chapter.getTitle(), chapter.getUrl());
        writer.write(chapter.getTitle());
        writer.write(lineSeparator);
        // 写章节正文
        writer.write(body.getChapterBodyText());
        writer.write(lineSeparator);
        if (body.getNextChapterUrl() != null) {
          log.debug("下一章节地址[{}]", body.getNextChapterUrl());
          body = NovelFactory.getChapterBodyInstance(body.getNextChapterUrl(), body);
        } else {
          body = null;
        }
      }
    } catch (IOException e) {
      throw new NovelExeception("写出文件失败", e);
    }
  }
}
