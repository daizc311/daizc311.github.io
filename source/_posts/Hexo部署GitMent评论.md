---
title: 'Hexo部署GitMent评论'
date: 2018/12/23 11:02:53
updated: 2020/2/20 15:41:36
comments: true
categories: 
    - [<del>搞点骚操作</del>]
tags: 
    - blog
---

之前还没换主题的时候就是这样手动配置的，现在gitment都没人用了git-talk比较方便
<!--more-->

# GitMent简介 #
  Gitment是一个基于GitHub Issues的评论系统，他使用一个Github Repository的Issues区作为评论的存储区。支持在前端直接引入，不需要任何后端代码。可以在页面进行登录、查看、评论、点赞等操作，同时有完整的 Markdown / GFM 和代码高亮支持。尤为适合各种基于 GitHub Pages 的静态博客或项目页面。<!-- more -->

### Initialize Comments 时提示 Error: Validation Failed ### 


###### 出现问题 ###### 

  部署完成后点击初始化按钮后alert:"Error: Validation Failed" 
![图1.Error: Validation Failed](https://www.dreamccc.club/hexo/images/pasted-3.png)
<center><span style="border-bottom: 1px solid #d9d9d9;color: #aaa;">图1.Error: Validation Failed</span></center>

###### 查询资料 ######

  首先查阅了一下资料,找到了 [GitMent的issues](https://github.com/imsun/gitment/issues/118) ：
 &gt; 地址 https://github.com/imsun/gitment/issues/118

###### 原因分析 ######

  GitHub Issues中新建label时LabelName长度不能超过50，但是在GitMent中是以页面url(window.location.pathname)作为唯一标识来创建Label的，当我们以中文作为文章标题时，中文将被转义，转义后的url超过了50字符长度的限制，引起报错。

![图2.LabelName长度过长](https://www.dreamccc.club/hexo/images/pasted-7.png)
<center><span style="border-bottom: 1px solid #d9d9d9;color: #aaa;">图2.LabelName长度过长</span></center>

解决方式：
 1. 缩短标题长度，并尽量少用中文标题
 2. 寻找较短的具有唯一性的参数作为Issues的LabelName
 
 
![图3.参数替换前后对比](https://www.dreamccc.club/hexo/images/pasted-4.png)
<center><span style="border-bottom: 1px solid #d9d9d9;color: #aaa;">图3.参数替换前后对比</span></center>
 
很明显只能寻找其他参数来代替URL作为ID了，在这里我们使用网页标题（document.title）作为ID。


###### 错误修正&nbsp;#######

** hexo-theme-yilia主题 ** <br/>
  该主题的gitment配置文件位于${hexo_root}/themes/hexo-theme-yilia/layout/_partial/post下

修改gitment.ejs:
![图4.gitment.ejs](https://www.dreamccc.club/hexo/images/pasted-9.png)
<center><span style="border-bottom: 1px solid #d9d9d9;color: #aaa;">图4.gitment.ejs</span></center>

** hexo-theme-next主题 ** <br/>
  该主题的gitment配置文件位于${hexo_root}/themes/hexo-theme-next/layout/_third-party/comments下

修改gitment.swig:
![图5.gitment.swig](https://www.dreamccc.club/hexo/images/pasted-8.png)
<center><span style="border-bottom: 1px solid #d9d9d9;color: #aaa;">图5.gitment.swig</span></center>

###### 效果一览 ######

  修改后成功初始化，Issus中出现了对应文章的记录，blog中可以正常的发布评论了。

![图6.GitMent效果](https://www.dreamccc.club/hexo/images/pasted-10.png)
<center><span style="border-bottom: 1px solid #d9d9d9;color: #aaa;">图6.GitMent效果</span></center>


![图7.GitHub效果](https://www.dreamccc.club/hexo/images/pasted-11.png)
<center><span style="border-bottom: 1px solid #d9d9d9;color: #aaa;">图7.GitHub效果</span></center></pre></en-note>
