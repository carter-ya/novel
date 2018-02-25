package com.ifengxue.novel.download;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

public class SingleThreadDownloaderTest {

  private SingleThreadDownloader downloader = new SingleThreadDownloader();

  @BeforeClass
  public static void init() throws IOException {
    PropertyConfigurator
        .configure(SingleThreadDownloaderTest.class.getClassLoader().getResourceAsStream("log4j.properties"));
    if (!Files.exists(Paths.get("out"))) {
      Files.createDirectory(Paths.get("out"));
    }
  }

  @Test
  public void download() {
    downloader.download("http://www.booktxt.net/4_4496/", "out/超级卡牌系统.txt");
  }

  @Test
  public void downloadSkipPrevChapters() {
    downloader.downloadSkipPrevChapters("http://www.booktxt.net/3_3571/1455492.html", "out/万古神帝.txt");
  }
}