package com.ifengxue.novel;

import java.nio.charset.StandardCharsets;
import lombok.Data;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * 配置
 */
@Data
public class NovelConfiguration {

  private static final NovelConfiguration INSTANCE = new NovelConfiguration();

  /**
   * http client
   */
  private CloseableHttpClient httpClient = HttpClients.createDefault();
  /**
   * 文件编码
   */
  private String encode = StandardCharsets.UTF_8.name();
  /**
   * 换行符
   */
  private String lineSeparator = "\r\n";

  public static NovelConfiguration getInstance() {
    return INSTANCE;
  }
}
