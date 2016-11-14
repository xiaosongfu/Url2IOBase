> [Url2IOBase库](https://github.com/xiaosongfu/Url2IOBase) 仅支持将爬取到的标题和内容写入文件，且文件的目录就是当前目录，它不支持其他任何操作，如保存到数据库等。

> [Url2IOElastic库](https://github.com/xiaosongfu/Url2IOElastic) 则是采用了插件机制，可以自定义类对结果进行自定义处理，如保存到数据库、上传到服务器等。

---
> 以下内容适用于 v2.0.0 以上版本，v1.x.x版本请参考[README-v1.md](https://github.com/xiaosongfu/Url2IOBase/blob/master/README-v1.md)

## #1
昨天刷微博的时候看到了[url2io](http://www.url2io.com/)，恰巧最近有爬网页的需求，便着手对[url2io](http://www.url2io.com/)官方的api进行二次封装。封装得不好，见谅！

封装到何种程度是让人比较纠结的，如果封装的不够灵活，可能就不能拿来直接用了，可是官方的api十分的简单，封装的太灵活可能反而不如不封装，所以大家如果觉得可以直接用的，到[release下载jar包](https://github.com/xiaosongfu/Url2IOBase/releases/download/v1.0.0/Url2IOBase.jar)即可，如果不能直接用的可以下载源码自己修改，代码渣，见谅。

## #2
代码使用建造者模式，使用十分简单，到[测试文件：Url2IOBaseTest](https://github.com/xiaosongfu/Url2IOBase/blob/master/src/com/fuxiaosong/url2iobase/Url2IOBaseTest.java)里一看便懂。现对所有的参数做着重说明：

> 1. index: 仅表示索引，该属性的默认值为1，仅仅是标识当前爬取到第多少页，在控制台打印信息的时候显示用，并没有其他什么用
2. total: 指定需要爬取多少页的数据
3. together: 聚合大小，用于指定将多少个网页的正文内容保存在同一个txt文件中
4. what: 爬取的是什么东西，如小说的书名，仅用于输出文件的文件名
5. token: token必须要设置，token必须要设置，重要的事情说2遍(到官网[url2io](http://www.url2io.com/)注册帐号就可以拿到token了，莫急莫急)
7. beginUrl: 爬取的第一个网页的url，必须要设置，必须要设置，必须要设置，重要的事情说3遍
8. sleep: 我也不知道[url2io](http://www.url2io.com/)的抗压能力怎么样，不过爬一页休息2秒也没什么关系吧
9. process: 把标题和正文内容爬出来了，但是可能包含一些我们不想要的东西，这时候就得自己写代码把那些渣渣去掉了，具体请到[测试文件：Url2IOBaseTest](https://github.com/xiaosongfu/Url2IOBase/blob/master/src/com/fuxiaosong/url2iobase/Url2IOBaseTest.java)里一看便知，其实就是把标题和正文处理了一下，去掉几个字符什么的，相信你懂的 ^-^

## #3
使用方法
> 1. 引入jar包
2. 创建Url2IOBase对象，并执行article()方法，以爬取《极品家丁》小说为例：

---  

    Url2IOBase url2IOBase = new Url2IOBase.Builder()
                .token("你的token")
                .index(1)
                .total(100)
                .together(5)
                .what("极品家丁")
                .beginUrl("http://www.ybdu.com/xiaoshuo/4/4398/556977.html")
                .sleepTime(5000L)
                .build();
    url2IOBase.article();
    //解释：爬取极品家丁小说，index从1开始，共爬取100页，每5页的正文内容保存在一个txt文件里，每爬一页休息的时间为5秒

## #4
代码码好了，跑完了，是该收货的时候了。在源代码目录下会生成多个文件：
> 所有的标题都存放在一个文件里面，文件名为:what-title.txt，一个标题占一行，空几格是该页的url，形如：第一章 公子，公子（1）    http://www.ybdu.com/xiaoshuo/4/4398/556977.html
>
> 正文内容会以together个网页内容为基础，分开保存在各个文件里面，如1-together保存在：what-1-together-content.txt 文件里。

## #5
不仅人会蒙逼，服务器也会：

> 服务器跟你好好的说话是这样的：

![你好啊](http://xiaosongfu-github.nos-eastchina1.126.net/success.png)


> 你把他惹生气了是这样的：

![我生气了](http://xiaosongfu-github.nos-eastchina1.126.net/error.png)

## #6
欢迎提issue，提PR。
