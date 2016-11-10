package com.fuxiaosong.url2iobase;

import com.google.gson.Gson;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Url2IOBase 类
 *
 * @author fuxiaosong
 * @version 1.0.0 2016年11月10日 14:10:32
 */
public final class Url2IOBase {
    //索引，标识当前爬取到了第几页
    private int mIndex = 1;
    //总共爬取多少页数据
    private int mTotal = 10;
    //文件名的前缀
    private String mFileName = "";
    //基础url
    private String mBaseUrl = "";
    //起始的url
    private String mNextUrl = "";
    //爬取线程休眠的时间，单位毫秒
    private Long mSleepTime = 2000L;

    //文件名的前缀
    private int mFileNamePre = 1;
    //文件名中的章节范围
    private String mMid = "1-10";

    //标题和正文内容的二次处理对象
    private BaseProcess mBaseProcess = null;

    //保存各种错误信息
    private static HashMap<String , String> mErrorInfoMap = null;

    /*
     * 初始化各种错误信息
     */
    static {
        mErrorInfoMap = new HashMap<>();
        mErrorInfoMap.put("PermissionError", "token认证错误；已超出使用配额");
        mErrorInfoMap.put("HTTPError", "抓取需要提取正文的网页时发生HTTP请求错误，如：404 Not Found");
        mErrorInfoMap.put("URLError", "抓取需要提取正文的网页时发生网址错误，如：Name or service not known");
        mErrorInfoMap.put("TypeError", "请求的资源不是html文档或xhtml文档，无法提取正文");
        mErrorInfoMap.put("UnknowError", "未知错误，很可能是服务器内部错误。具体看错误消息");
    }

    /**
     * 构造方法
     *
     * @param index 索引，标识当前爬取到了第几页
     * @param total 总共爬取多少页数据
     * @param what 爬取的是什么东西，如小说的书名，用于输出文件的文件名
     * @param beginUrl 基础url
     * @param sleepTime 爬取线程休眠的时间，单位毫秒
     */
    public Url2IOBase(int index, int total , String what, String baseUrl , String beginUrl, Long sleepTime , BaseProcess baseProcess) {
        this.mIndex = index;
        this.mTotal = total;
        this.mFileName = what + "-";
        this.mBaseUrl = "http://api.url2io.com/article?token="+baseUrl+"&fields=next,text&url=";
        this.mNextUrl = beginUrl;
        this.mSleepTime = sleepTime;
        this.mBaseProcess = baseProcess;
    }

    /**
     * 核心方法，发起网络请求，处理服务器返回的结果，以及将结果写入文件
     */
    public void process() {
        //StringBuffer，用来承接服务器返回值
        StringBuffer sb = null;
        for (int i = 0; i < mTotal; i++) {
            /*
             * 如果下一页的url为空，则停止爬取动作
             */
            if(null == mNextUrl || "".equals(mNextUrl)){
                System.out.println("-------------------------------------");
                System.out.println("---- 要爬取的url为空,爬取动作停止 ----");
                System.out.println("-------------------------------------");
                return;
            }
            //实例化StringBuffer
            sb = new StringBuffer();
            /*
             * 发起网络请求
             */
            try {
                URL url = new URL(mBaseUrl + mNextUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String data;
                while ((data = br.readLine()) != null) {
                    sb.append(data);
                }
                br.close();
                connection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*
             * 使用Gson解析服务器返回的数据为Response对象
             */
            Response response = new Gson().fromJson(sb.toString(), Response.class);

            /*
             * 看看是不是出问题了
             */
            if(null != response.getMsg() && null != response.getError()){
                System.out.println("--------------------------------------");
                System.out.println("---------- duang 出问题了 ------------");
                System.out.println("---> msg：" + response.getMsg());
                System.out.println("---> error：" + mErrorInfoMap.get(response.getError()));
                System.out.println("--------------------------------------");

                return;
            }

            /*
             * 启动线程写入文件
             */
            new WriteToFileThread(response.getTitle(), response.getText(), mNextUrl).start();

            /*
             * 保存下一页的url
             */
            mNextUrl = response.getNext();

            /*
             * 打印信息
             */
            System.out.println("--------------------------------------");
            System.out.println("---> 爬取第 " + mIndex + " 页");
            System.out.println("---> 标题： " + response.getTitle());
            System.out.println("---> 开头几个字：" + response.getText().substring(0, 20));
            System.out.println("--------------------------------------");
            System.out.println("");

            /*
             * 不可用高频的请求服务
             */
            try {
                Thread.sleep(mSleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 文件写入线程
     */
    private class WriteToFileThread extends Thread {
        private String mTitle = "";
        private String mContent = "";
        private String mCurrentUrl = "";
        private WriterContentToFile mWriterContentToFile = null;

        /**
         * 构造方法
         * @param title 标题
         * @param content 正文内容
         * @param currentUrl 当前页的url
         */
        public WriteToFileThread(String title, String content, String currentUrl) {
            this.mTitle = title;
            this.mContent = content;
            this.mCurrentUrl = currentUrl;
            mWriterContentToFile = new WriterContentToFile();
        }

        @Override
        public void run() {
            /*
             * 如果没有自定义处理类，就不用再处理了
             */
            if(! ("BaseProcess".equals(mBaseProcess.getClass().getSimpleName()))) {
                mTitle = mBaseProcess.processTitle(mTitle);
                mContent = mBaseProcess.processText(mContent);
            }
            /*
             * 写标题
             */
            mWriterContentToFile.write(mFileName + "title.txt", mTitle + "    " + mCurrentUrl + "\n");

            /*
             * 写内容
             */
            if (mIndex % 10 == 0) {
                mFileNamePre++;
                mMid = ((mFileNamePre - 1) * 10) + "" + (mFileNamePre + 10);
            }
            mWriterContentToFile.write(mFileName + mMid + "-content.txt", "\n\r\n" + mTitle + "\n" + mContent);

            /*
             * mIndex自增
             */
            mIndex++;
        }
    }

    /**
     * 文件写入器
     */
    private class WriterContentToFile {
        //文件输出流
        private FileOutputStream mFileOutputStream = null;
        //带缓冲的输出流
        private BufferedOutputStream mBufferedOutputStream =null;

        /**
         * 将内容写入到指定的文件
         *
         * @param fileName 文件名
         * @param content 要写入的内容
         */
        public void write(String fileName , String content){
            try {
                //构造输出流
                mFileOutputStream = new FileOutputStream(new File(fileName) ,true);
                mBufferedOutputStream =new BufferedOutputStream(mFileOutputStream);
                //开始写入文件
                mBufferedOutputStream.write(content.getBytes());
                //刷新输出流并关闭输出流
                mBufferedOutputStream.flush();
                mBufferedOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 建造者类
     */
    public static final class Builder {
        //索引，标识当前爬取到了第几页
        private int mIndex = 1;
        //总共爬取多少页数据
        private int mTotal = 10;
        //文件名的前缀
        private String mFileName = "qiushibaike";
        //mToken
        private String mToken = "111222333";
        //起始的url
        private String mNextUrl = "http://www.qiushibaike.com/";
        //爬取线程休眠的时间，单位毫秒
        private Long mSleepTime = 2000L;
        //标题和正文内容的二次处理对象
        private BaseProcess mBaseProcess = null;

        /**
         * 构造方法
         * 需要在构造方法内实例化 mBaseProcess 对象
         */
        public Builder(){
            mBaseProcess = new BaseProcess();
        }

        /**
         * 索引，标识当前爬取到了第几页
         * 该属性的默认值为1，仅仅是标识当前爬取到了多少页，在控制台打印信息的时候显示用，其他并没有什么用
         *
         * @param index
         * @return Builder实例
         */
        public Builder index(int index){
            this.mIndex = index;
            return this;
        }

        /**
         * 总共需要爬取多少页
         *
         * @param total 总共需要爬取多少页
         * @return Builder实例
         */
        public Builder total(int total){
            this.mTotal = total;
            return this;
        }

        /**
         * 爬取的是什么东西，如小说的书名，用于输出文件的文件名
         *
         * @param what 爬取的是什么东西
         * @return Builder实例
         */
        public Builder what(String what){
            this.mFileName = what;
            return this;
        }

        /**
         * 设置token，该函数必须调用
         *
         * @param token token
         * @return Builder实例
         */
        public Builder token(String token){
            this.mToken = token;
            return this;
        }

        /**
         * 爬取动作开始的第一页的url
         *
         * @param beginUrl
         * @return Builder实例
         */
        public Builder beginUrl(String beginUrl){
            this.mNextUrl = beginUrl;
            return this;
        }

        /**
         * 爬取线程爬取动作不能太高频，每爬取一次，休息 mSleepTime 毫秒
         *
         * @param sleepTime 爬取线程休眠的时间，单位毫秒
         * @return Builder实例
         */
        public Builder sleepTime(Long sleepTime){
            this.mSleepTime = sleepTime;
            return this;
        }

        /**
         * 如果需要对标题、正文内容进行二次处理则需要继承该类，
         * 并按需重写 BaseProcess 的 processTitle(...) 或 processText(...) 方法
         *
         * @param process 标题和正文内容的二次处理对象
         * @return Builder实例
         */
        public Builder process(BaseProcess process){
            this.mBaseProcess = process;
            return this;
        }

        /**
         * build 方法，构造出 Url2IOBase 实例
         *
         * @return Url2IOBase 实例
         */
        public Url2IOBase build(){
            return new Url2IOBase(mIndex, mTotal, mFileName, mToken, mNextUrl, mSleepTime, mBaseProcess);
        }
    }
}