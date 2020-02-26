package com.ifengxue.novel;

import com.ifengxue.novel.chapter.BiqugeChapter;
import com.ifengxue.novel.chapter.Chapter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 笔趣阁<a href="http://www.biquge.com.tw">笔趣阁</a>实现
 */
@Slf4j
@Data
public class BiqugeNovel implements Novel {

  private final String chapterListUrl;
  private String title;
  private String author;
  private LocalDateTime lastModifyTime;
  private String summary;
  private String coverImageUrl;
  private int chapterCount;
  private LinkedList<Chapter> chapters;

  public BiqugeNovel(String chapterListUrl) {
    this.chapterListUrl = chapterListUrl;
    openBiquge();
  }

  @Override
  public String getTitle() {
    return title;
  }

  @Override
  public String getAuthor() {
    return author;
  }

  @Override
  public String getSummary() {
    return summary;
  }

  @Override
  public LocalDateTime getLastModifyTime() {
    return lastModifyTime;
  }

  @Override
  public String getCoverImageUrl() {
    return coverImageUrl;
  }

  @Override
  public int getWordCount() {
    return -1;
  }

  @Override
  public int getChapterCount() {
    return chapterCount;
  }

  @Override
  public List<Chapter> getChapters() {
    return chapters;
  }

  @Override
  public Charset getCharset() {
    return StandardCharsets.UTF_8;
  }

  private void openBiquge() {
    try (CloseableHttpResponse response = NovelConfiguration.getInstance().getHttpClient()
        .execute(new HttpGet(chapterListUrl))) {
      Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity(), getCharset()), chapterListUrl);

      Element listEle = Optional.ofNullable(doc.getElementById("list"))
          .orElseThrow(() -> new NovelExeception("抓取" + chapterListUrl + "失败，没有匹配#list的节点"));
      title = doc.select("#info h1").first().text();// 标题
      Elements pEles = doc.select("#info p");
      String authorText = pEles.first().text();
      author = authorText.substring(authorText.indexOf("：") + 1);// 作者
      String lastModifyTimeText = pEles.get(2).text();
      lastModifyTimeText = lastModifyTimeText.substring(lastModifyTimeText.indexOf("：") + 1);
      lastModifyTime = parseDateTime(lastModifyTimeText);// 最后更新时间
      summary = doc.selectFirst("#intro p").text();// 简介
      coverImageUrl = Optional.ofNullable(doc.selectFirst("#fmimg img")).map(e -> e.absUrl("src"))
          .orElse(null);// 需要检查图片是否真实存在
      // 章节列表
      chapters = new LinkedList<>();
      for (Element chapterEle : listEle.select("dl dd")) {
        Element aEle = chapterEle.selectFirst("a");
        chapters.add(new BiqugeChapter(aEle.absUrl("href"), aEle.text()));
        chapterCount++;
      }
    } catch (IOException e) {
      throw new NovelExeception("读取" + chapterListUrl + "失败", e);
    }
    // 检查封面图片是否真实存在
    try (CloseableHttpResponse response = NovelConfiguration.getInstance().getHttpClient()
        .execute(new HttpGet(coverImageUrl))) {
      if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
        coverImageUrl = null;
      }
    } catch (IOException e) {
      log.warn("检查封面图片是否存在失败，默认无封面图片", e);
      coverImageUrl = null;
    }
  }

  private LocalDateTime parseDateTime(String text) {
    try {
      return LocalDateTime.of(LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd")), LocalTime.MIN);
    } catch (Exception e) {
      log.debug("解析时间字符串失败[{}]", text);
      return null;
    }
  }
}
