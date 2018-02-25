package com.ifengxue.novel;

import com.ifengxue.novel.chapter.BookTxtChapter;
import com.ifengxue.novel.chapter.Chapter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 顶点小说实现<a href="http://www.booktxt.net">顶点小说</a>
 */
@Slf4j
@Data
public class BookTxtNovel implements Novel {

  /**
   * 编码
   */
  public static final String HTML_ENCODING = "GBK";
  private final String chapterListUrl;
  private String title;
  private String author;
  private String summary;
  private LocalDateTime lastModifyTime;
  private String coverImageUrl;
  private int chapterCount;
  private List<Chapter> chapters;

  /**
   * @param chapterListUrl 章节列表链接
   */
  public BookTxtNovel(String chapterListUrl) {
    this.chapterListUrl = chapterListUrl;
    openBookTxt();
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

  private void openBookTxt() {
    CloseableHttpClient httpClient = NovelConfiguration.getInstance().getHttpClient();
    try (CloseableHttpResponse response = httpClient.execute(new HttpGet(chapterListUrl))) {
      Document doc = Jsoup.parse(EntityUtils.toString(response.getEntity(), HTML_ENCODING), chapterListUrl);
      Element listEle = Optional.ofNullable(doc.getElementById("list"))
          .orElseThrow(() -> new NovelExeception("抓取" + chapterListUrl + "失败，没有匹配#list的节点"));

      title = doc.select("#info h1").text();// 标题
      Elements pEles = doc.select("#info p");
      String authorText = pEles.first().text();
      author = authorText.substring(authorText.indexOf("：") + 1);// 作者
      String lastModifyTimeText = pEles.get(2).text();
      lastModifyTimeText = lastModifyTimeText.substring(lastModifyTimeText.indexOf("：") + 1);// 最后修改时间
      lastModifyTime = parseDateTime(lastModifyTimeText);
      summary = doc.select("#intro p").first().text();
      //TODO 封面图片由脚本加载
//      coverImageUrl = Optional.ofNullable(doc.select("#fmimg img").first()).map(img -> img.absUrl("src"))
//          .orElse(null);// 封面图片URL

      Elements children = listEle.getElementsByTag("dl").first().children();
      chapters = new LinkedList<>();
      boolean isBodyChapter = false;// 是否是正文章节标志
      int dtIndex = 0;
      for (Element child : children) {
        // 跳过小说最新章节
        if (!isBodyChapter && child.tagName().equalsIgnoreCase("dt") && ++dtIndex == 2) {
          isBodyChapter = true;
        }
        if (!isBodyChapter || child.tagName().equalsIgnoreCase("dt")) {
          continue;
        }
        Element aEle = child.getElementsByTag("a").first();
        chapters.add(parseChapter(aEle));
        chapterCount++;
      }
    } catch (IOException e) {
      throw new NovelExeception("读取" + chapterListUrl + "失败", e);
    }
  }

  private Chapter parseChapter(Element aEle) {
    BookTxtChapter chapter = new BookTxtChapter();
    chapter.setUrl(aEle.absUrl("href"));
    chapter.setTitle(aEle.text());
    return chapter;
  }

  private LocalDateTime parseDateTime(String text) {
    try {
      return LocalDateTime.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    } catch (Exception e) {
      log.debug("解析时间字符串失败[{}]", text);
      return null;
    }
  }
}
