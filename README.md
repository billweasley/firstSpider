# 简述 Brief Introduction:
最新版本 The lastest version：0.0.0.3
这是一个爆肝5天干出来的爬虫，也是我写得第一个爬虫。  
This is a spider coding for 5 days and my first one.  
然而对大多数人来说，它只是批量备份ICE课件并分类的小工具。  
For most of peoplem, it is just a tool for backup lesson meterials form [XJTLU JCE](https://ice.xjtlu.edu.cn).  
初次写这种东西，当然有诸多不足，对自己的要求就是能用就好（人生苦短，我要学python）。  
Many shortages since it is my first spider but enough to use, which is the basic require for myself.  

<S>界面是随手画的，本来不打算做图形界面的，然后又花了一整天（手动再见</S>

# 请注意 Notice:

+ 登陆[地址](https://ice.xjtlu.edu.cn)登陆名为“姓名+入学年”，没有邮箱后缀

+ 总体下载时间为大约10到15分钟，文件所需硬盘空间为587 MB（以信息与计算机科学大二为例）。不同专业，不同年级和不同网速应该差别较大（没测试过）。

+ 目前仅支持幻灯片，word文档和pdf文档的下载；
 其它类型文件取决于老师放置的姿势，如果你发现有个没有后缀名的文件请尝试手动添加后缀名。
 如果有同学表示本专业某种奇怪的格式很多，请联系我，或者你可以自己改一下代码。

+ 幻灯片和word文档打开如果提示格式不匹配，请去掉后缀名最后的x,比如"pptx"->"ppt","docx"->"doc"

---

 **(以下内容和使用者无关，请忽略。 开发者留步)**

---
**这是一个Netbean工程**

## Known issues for v 0.0.0.3：

> + <S>由于设计的问题，一个文件一个线程，导致难以确定整个过程的结束—。(会导致按钮恢复时间不准以及右上角关闭按钮判断状态失准)。</S> （已于0.0.0.2版本修复）

> + <S>偶尔会出现未知原因的 java.net.SocketTimeoutException: Read timed out 错误</S>（已于0.0.0.3版本修复）

> + <S>偶尔会出现未知原因的 java.net.SocketException: Reset 错误</S>（已于0.0.0.3版本修复）

> + ICE上的文件两种php混杂(真是宇宙最好的语言，呵呵哒)，一种链接后为直接取得文件地址；另一种为得到后跳转得到一个新的php页面，上面有文件的地址与文件名。
对于前者，只能程序命名，但是现在只能通过文件类型判定（但是分不清ppt还是pptx）

> + 不支持文件夹嵌套，也就是说，文件夹只支持一级目录，下面目录的文件会被直接爬到一级目录下面。


## To do:

> + 整体算法优化

> + 文件夹嵌套

> + <S>缩减下载线程数量及下载线程状态确定 </S>

> + 课程选择功能，即选择要下载的课程

> + 作业爬取功能

> + 链接爬取功能

> + 更多文件类型的支持，更准确的抓取文件类型
