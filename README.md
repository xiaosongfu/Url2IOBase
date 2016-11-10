昨天刷微博的时候看到了[url2io](http://www.url2io.com/)，恰巧最近有爬网页的需求，便着手对[url2io](http://www.url2io.com/)官方的api进行二次封装。封装得不好，见谅！

封装到何种程度是让人比较纠结的，如果封装的不够灵活，可能就不能拿来直接用了，可是官方的api十分的简单，封装的太灵活可能反而不如不封装，所以大家如果觉得可以直接用的，到[release下载jar包](https://github.com/xiaosongfu/Url2IOBase/releases/download/v1.0.0/Url2IOBase.jar)即可，如果不能直接用的可以下载源码自己修改，代码渣，见谅。

代码使用建造者模式，使用十分简单，到[测试文件：Url2IOBaseTest](https://github.com/xiaosongfu/Url2IOBase/blob/master/src/com/fuxiaosong/url2iobase/Url2IOBaseTest.java)里一看便懂。有几点需要着重说明：
1. index: 仅表示索引，该属性的默认值为1，仅仅是标识当前爬取到第多少页，在控制台打印信息的时候显示用，并没有其他什么用
2. total: 指定需要爬取多少页的数据
3. what: 爬取的是什么东西，如小说的书名，仅用于输出文件的文件名
4. token: token必须要设置，token必须要设置，重要的事情说2遍(到官网[url2io](http://www.url2io.com/)注册帐号就可以拿到token了，莫急莫急)
5. beginUrl: 爬取的第一个网页的url，必须要设置，必须要设置，必须要设置，重要的事情说3遍
6. sleep: 我也不知道[url2io](http://www.url2io.com/)的抗压能力怎么样，不过爬一页休息2秒也没什么关系吧
7. process: 把标题和正文内容爬出来了，但是可能包含一些我们不想要的东西，这时候就得自己写代码把那些渣渣去掉了，具体请到[测试文件：Url2IOBaseTest](https://github.com/xiaosongfu/Url2IOBase/blob/master/src/com/fuxiaosong/url2iobase/Url2IOBaseTest.java)里一看便知，其实就是把标题或正文处理了一下，去掉几个字符什么的，相信你懂的 ^-^

代码码好了，跑完了，是该收货的时候了。在源代码目录下会生成多个文件：
> 所有的标题都存放在一个文件里面，文件名为:what-title.txt，一个标题占一行，空几格是该页的url，形如：第一章 公子，公子（1）    http://www.ybdu.com/xiaoshuo/4/4398/556977.html
> 
> 正文内容会以10个网页内容为基础，分开保存在各个文件里面，如1-10保存在：what-1-10-content.txt 文件里。

不仅人会蒙逼，服务器也会，服务器好好的跟你说话是这样的：

![你好啊](http://xiaosongfu-github.nos-eastchina1.126.net/success.png)


你把他惹生气了是这样的：

![我生气了](http://xiaosongfu-github.nos-eastchina1.126.net/error.png)


很不幸，我的token让我弄生气了，然后罢工了，然后又哄好了，然后就可以愉快的玩耍了，大家折腾去吧...

欢迎提issue，提PR。
