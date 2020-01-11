package com.ifengxue.novel.download;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MultiThreadDownloaderTest {

  private static ExecutorService threadPool = Executors.newCachedThreadPool();
  private int fixedThreadCount = 5;
  private int fixedTaskCount = 100;

  @BeforeClass
  public static void init() throws IOException {
    SingleThreadDownloaderTest.init();
  }

  @Test
  public void download() throws InterruptedException {
    Downloader downloader = MultiThreadDownloader.newFixedThreadCountDownloader(threadPool, fixedThreadCount);
    String path = "out/天行-失落叶.txt";
    downloader.download("http://www.xhytd.com/0/727/", path);
    waitFinish(path);
  }

  @Test
  public void downloadSkipPrevChapters() throws InterruptedException {
    Downloader downloader = MultiThreadDownloader.newFixedTaskCountDownloader(threadPool, fixedTaskCount);
    String path = "out/牧神记.txt";
    downloader.downloadSkipPrevChapters("http://www.booktxt.net/5_5189/2366095.html", path);
    waitFinish(path);
  }

  private void waitFinish(String path) throws InterruptedException {
    while (Files.notExists(Paths.get(path))) {
      Thread.sleep(1_000);
    }
    Thread.sleep(5_000);
  }

  @AfterClass
  public static void destroy() {
    threadPool.shutdown();
  }
}