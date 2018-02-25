package com.ifengxue.novel;

public class NovelExeception extends RuntimeException {

  public NovelExeception() {
    super();
  }

  public NovelExeception(String message) {
    super(message);
  }

  public NovelExeception(String message, Throwable cause) {
    super(message, cause);
  }

  public NovelExeception(Throwable cause) {
    super(cause);
  }

  protected NovelExeception(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
