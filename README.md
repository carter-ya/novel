### 如何下载小说？
#### 单线程下载
```java
Downloader downloder = new SingleThreadDownloader();
String path = "out/万古神帝.txt";// 保存路径
String chapterListUrl = "http://www.booktxt.net/3_3571/";// 章节地址
downloader.download(chapterListUrl, path);// 整本书下载
String chapterUrl = "http://www.booktxt.net/3_3571/1292242.html";// 指定的章节
downloader.downloadSkipPrevChapters(chapterUrl, path);// 从指定的章节开始下载所有章节
```
#### 多线程下载
```java
int fixedThreadCount = 5;// 固定每次下载只用5个线程
Downloader downloder = MultiThreadDownloader.newFixedThreadCountDownloader(Executors.newCachedThreadPool(), fixedThreadCount);
int fixedTaskCount = 100;// 固定每个线程最多下载100章
downloader = MultiThreadDownloader.newFixedTaskCountDownloader(Executors.newCachedThreadPool(), fixedTaskCount);
```
### 支持哪些小说网站？
- [顶点小说](http://www.booktxt.net)
- [笔趣阁](http://www.biquge.com.tw)
### 如何支持更多的小说网站？
1. 实现[Novel](src/main/java/com/ifengxue/novel/Novel.java)接口
> 可参考[顶点小说](src/main/java/com/ifengxue/novel/BookTxtNovel.java)实现

2. 实现[Chapter](src/main/java/com/ifengxue/novel/chapter/Chapter.java)接口
> 可参考[顶点小说](src/main/java/com/ifengxue/novel/chapter/BookTxtChapter.java)实现

3. 实现[ChapterBody](src/main/java/com/ifengxue/novel/chapter/ChapterBody.java)接口
> 可参考[顶点小说](src/main/java/com/ifengxue/novel/chapter/BookTxtChapterBody.java)实现

4. 注册到工厂[NovelFactory](src/main/java/com/ifengxue/novel/NovelFactory.java)
```java
NovelFactory.register("www.booktxt.net", BookTxtNovel.class, BookTxtChapterBody.class);
NovelFactory.register("www.example.com", ExampleNovel.class, ExampleChapterBody.class);
```