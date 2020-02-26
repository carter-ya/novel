package com.ifengxue.novel.chapter;

import com.ifengxue.novel.BookTxtNovel;
import com.ifengxue.novel.Novel;
import com.ifengxue.novel.NovelConfiguration;
import com.ifengxue.novel.NovelExeception;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Optional;
import lombok.Setter;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

/**
 * 顶点小说章节正文
 */
public class BookTxtChapterBody implements ChapterBody {

  private final String chapterUrl;
  /**
   * 章节列表地址URL
   */
  private String chapterListUrl;
  private Novel novel;
  private String chapterBody;
  private BookTxtChapter chapter;
  private String prevChapterUrl;
  private String nextChapterUrl;
  @Setter
  private Charset charset = Charset.forName("GBK");

  /**
   * @param chapterUrl 顶点小说章节正文地址
   */
  public BookTxtChapterBody(String chapterUrl) {
    this.chapterUrl = chapterUrl;
    openChapterBody();
  }

  /**
   * @param chapterUrl 顶点小说章节正文地址
   */
  public BookTxtChapterBody(String chapterUrl, Charset charset) {
    this.chapterUrl = chapterUrl;
    this.charset = charset;
    openChapterBody();
  }

  @Override
  public synchronized Novel getNovel() {
    if (novel == null) {
      novel = new BookTxtNovel(chapterListUrl);
    }
    return novel;
  }

  @Override
  public Chapter getChapter() {
    return chapter;
  }

  @Override
  public String getPrevChapterUrl() {
    return prevChapterUrl;
  }

  @Override
  public String getNextChapterUrl() {
    return nextChapterUrl;
  }

  @Override
  public String getChapterListUrl() {
    return chapterListUrl;
  }

  @Override
  public String getChapterBodyText() {
    return chapterBody;
  }

  @Override
  public Charset getCharset() {
    return charset;
  }

  public void setCharset(Charset charset) {
    this.charset = charset;
  }

  @Override
  public String toString() {
    return "BookTxtChapterBody{" +
        "chapterUrl='" + chapterUrl + '\'' +
        ", chapterListUrl='" + chapterListUrl + '\'' +
        ", prevChapterUrl='" + prevChapterUrl + '\'' +
        ", nextChapterUrl='" + nextChapterUrl + '\'' +
        ", chapterBodyText='" + getChapterBodyText() + '\'' +
        '}';
  }

  /**
   * 打开章节正文
   */
  private void openChapterBody() {
    CloseableHttpClient httpClient = NovelConfiguration.getInstance().getHttpClient();
    try (CloseableHttpResponse response = httpClient.execute(new HttpGet(chapterUrl))) {
      String html = EntityUtils.toString(response.getEntity(), getCharset());
      html = html.replace("&nbsp;&nbsp;", SPACE_TAG)// 替换为空格
          .replace("<br />", LINE_BREAK_TAG);// 替换为换行
      Document doc = Jsoup.parse(html, chapterUrl);
      chapterBody = Optional.ofNullable(doc.getElementById("content"))
          .orElseThrow(() -> new NovelExeception("抓取" + chapterUrl + "失败，没有匹配#content的节点"))
          .text()
          .replace(SPACE_TAG, " ")
          .replace(LINE_BREAK_TAG, NovelConfiguration.getInstance().getLineSeparator());// 章节正文节点
      Elements conTopAElements = doc.select(".con_top a");
      chapterListUrl = conTopAElements.get(conTopAElements.size() - 1).absUrl("href");// 章节地址列表URL

      chapter = new BookTxtChapter();
      chapter.setUrl(chapterUrl);
      chapter.setTitle(doc.selectFirst(".bookname h1").text());// 章节名称

      Elements aEles = doc.select(".bottem1 a");
      prevChapterUrl = aEles.get(1).absUrl("href");// 前一章节地址
      nextChapterUrl = aEles.get(3).absUrl("href");// 后一章节地址
      if (prevChapterUrl.equals(chapterListUrl)) {
        prevChapterUrl = null;// 已经是第一章，则没有前一章地址
      }
      if (nextChapterUrl.equals(chapterListUrl)) {
        nextChapterUrl = null;// 已经是最后一章，则没有后一章地址
      }
    } catch (IOException e) {
      throw new NovelExeception("读取" + chapterUrl + "失败", e);
    }
  }
}
