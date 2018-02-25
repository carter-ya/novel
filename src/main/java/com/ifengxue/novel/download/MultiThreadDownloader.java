package com.ifengxue.novel.download;

import com.ifengxue.novel.Novel;
import com.ifengxue.novel.NovelConfiguration;
import com.ifengxue.novel.NovelExeception;
import com.ifengxue.novel.NovelFactory;
import com.ifengxue.novel.chapter.Chapter;
import com.ifengxue.novel.chapter.ChapterBody;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

/**
 * 多线程下载器
 */
@Slf4j
public class MultiThreadDownloader implements Downloader {

  private final ExecutorService threadPool;
  private final boolean useFixedThread;
  private final int fixedThreadCount;
  private final int fixedTaskCount;

  private MultiThreadDownloader(ExecutorService threadPool, boolean useFixedThread, int fixedThreadCount,
      int fixedTaskCount) {
    this.threadPool = threadPool;
    this.useFixedThread = useFixedThread;
    this.fixedThreadCount = fixedThreadCount;
    this.fixedTaskCount = fixedTaskCount;
  }

  /**
   * 固定线程数量的多线程下载器
   *
   * @param threadPool 线程池
   * @param fixedThreadCount 固定的线程数量
   */
  public static MultiThreadDownloader newFixedThreadCountDownloader(ExecutorService threadPool, int fixedThreadCount) {
    return new MultiThreadDownloader(threadPool, true, fixedThreadCount, 0);
  }

  /**
   * 固定每个线程下载数量的多线程下载器
   *
   * @param threadPool 线程池
   * @param fixedTaskCount 固定的每个线程下载数量
   */
  public static MultiThreadDownloader newFixedTaskCountDownloader(ExecutorService threadPool, int fixedTaskCount) {
    return new MultiThreadDownloader(threadPool, false, 0, fixedTaskCount);
  }

  @Override
  public void download(String chapterListUrl, String filename) {
    Novel novel = NovelFactory.getNovelInstance(chapterListUrl);
    try {
      download(novel.getChapters(), novel.getChapterCount(), filename);
    } catch (Exception e) {
      throw new NovelExeception("下载失败", e);
    }
  }

  @Override
  public void downloadSkipPrevChapters(String chapterUrl, String filename) {
    ChapterBody body = NovelFactory.getChapterBodyInstance(chapterUrl);
    Novel novel = NovelFactory.getNovelInstance(body.getChapterListUrl());
    List<Chapter> chapters = new LinkedList<>();
    int chapterCount = 0;
    boolean isSkipPrevChapter = true;
    for (Chapter chapter : novel.getChapters()) {
      // 跳过前面不需要的章节
      if (isSkipPrevChapter && !chapter.getUrl().equals(chapterUrl)) {
        continue;
      }
      isSkipPrevChapter = false;
      chapters.add(chapter);
      chapterCount++;
    }
    try {
      download(chapters, chapterCount, filename);
    } catch (Exception e) {
      throw new NovelExeception("下载失败", e);
    }
  }

  private void download(List<Chapter> chapters, int chapterCount, String filename)
      throws IOException, ExecutionException, InterruptedException {
    int taskCount;
    int threadCount;
    if (useFixedThread) {
      taskCount = (int) Math.ceil(chapterCount * 1.0 / fixedThreadCount);
      threadCount = fixedThreadCount;
    } else {
      threadCount = (int) Math.ceil(chapterCount * 1.0 / fixedTaskCount);
      taskCount = fixedTaskCount;
    }
    // 无任何章节可以下载
    if (taskCount == 0 || threadCount == 0) {
      try (PrintWriter writer = new PrintWriter(filename, NovelConfiguration.getInstance().getEncode())) {
        writer.write(NovelConfiguration.getInstance().getLineSeparator());
      } catch (IOException e) {
        throw new NovelExeception("写出文件失败", e);
      }
      return;
    }
    // 切分任务
    Path novelDir = Files.createTempDirectory("novel");// 创建临时目录
    List<List<Chapter>> subChapters = subChapters(chapters, taskCount, threadCount);
    String lineSeparator = NovelConfiguration.getInstance().getLineSeparator();
    List<Future<String>> futures = new LinkedList<>();
    // 开始下载
    for (List<Chapter> subChapter : subChapters) {
      futures.add(threadPool.submit(() -> {
        String subFilename = novelDir.resolve(Paths.get(System.nanoTime() + ".txt")).toString();
        Iterator<Chapter> itr = subChapter.iterator();
        Chapter chapter = itr.next();
        ChapterBody body = NovelFactory.getChapterBodyInstance(chapter.getUrl());
        try (PrintWriter writer = new PrintWriter(subFilename, NovelConfiguration.getInstance().getEncode())) {
          while (body != null) {
            // 写章节标题
            chapter = body.getChapter();
            log.debug("章节名称[{}]，章节地址[{}]", chapter.getTitle(), chapter.getUrl());
            writer.write(chapter.getTitle());
            writer.write(lineSeparator);
            // 写章节正文
            writer.write(body.getChapterBodyText());
            writer.write(lineSeparator);
            if (itr.hasNext()) {
              chapter = itr.next();
              log.debug("下一章节地址[{}]", chapter.getUrl());
              body = NovelFactory.getChapterBodyInstance(chapter.getUrl());
            } else {
              body = null;
            }
          }
        }
        return subFilename;
      }));
    }
    // 拷贝文件
    List<String> filenames = new LinkedList<>();
    for (Future<String> future : futures) {
      filenames.add(future.get());
    }
    PrintWriter writer = null;
    BufferedReader reader = null;
    try {
      String encode = NovelConfiguration.getInstance().getEncode();
      writer = new PrintWriter(filename, encode);
      for (String f : filenames) {
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(f), encode));
        String line;
        while ((line = reader.readLine()) != null) {
          writer.write(line);
          writer.write(lineSeparator);
        }
        reader.close();
        Files.delete(Paths.get(f));// 删除临时文件
      }
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (Exception e) {
          // ignore
        }
      }
      if (reader != null) {
        try {
          reader.close();
        } catch (IOException e) {
          // ignore
        }
      }
    }
  }

  private List<List<Chapter>> subChapters(List<Chapter> chapters, int taskCount, int threadCount) {
    List<List<Chapter>> subChapters = new ArrayList<>(threadCount);
    for (int i = 0; i < threadCount; i++) {
      if (i == threadCount - 1) {
        subChapters.add(chapters.subList(i * taskCount, chapters.size()));
      } else {
        subChapters.add(chapters.subList(i * taskCount, (i + 1) * taskCount));
      }
    }
    return subChapters;
  }
}
