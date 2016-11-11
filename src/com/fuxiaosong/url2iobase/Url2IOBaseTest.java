package com.fuxiaosong.url2iobase;

/**
 * Url2IOBase 的测试类 Url2IOBaseTest
 *
 * @author fuxiaosong
 * @version 1.0.0 2016年11月10日 14:10:32
 */
public class Url2IOBaseTest {
    public static void main(String[] args){
        /*
         * 爬取默认的网址：糗事百科 http://www.qiushibaike.com/
         */
        new Url2IOBase.Builder().token("你的token").build().process();

        /*
         * 爬取 《极品家丁》 小说
         */
//        Url2IOBase url2IOBase = new Url2IOBase.Builder()
//                .token("你的token")
//                .index(1)
//                .together(2)
//                .total(4)
//                .what("极品家丁")
//                .beginUrl("http://www.ybdu.com/xiaoshuo/4/4398/556977.html")
//                .process(new JPJDProcess())
//                .sleepTime(5000L)
//                .build();
//        url2IOBase.process();
    }

    /**
     * 自定义的标题 正文内容处理类
     * 实现将标题中的 "_极品家丁最新章节_一本读全本小说网" 去掉，将正文中的...去掉
     */
    private static class JPJDProcess extends BaseProcess {
        @Override
        public String processTitle(String title) {
            return title.replace("_极品家丁最新章节_一本读全本小说网","");
        }

        @Override
        public String processText(String text) {
            String text1 = text.replace("\n\r\n", "\r\n");
            String text2 = text1.replace("\n", "");
            String text3 = text2.replace("上一页        返回目录        下一页", "");
            return text3;
        }
    }
}
