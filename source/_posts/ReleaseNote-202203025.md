---
title: ReleaseNote-202203025
date: 2022-03-25 03:16:10
comments: true
categories:
- [也许算是笔记]

tags:
- Blog
- Hexo

cover: https://oss.note.dreamccc.cn/note/images/posts/ReleaseNote/202203025-title.jpg?x-oss-process=style/blog_title

---

- 将流水线由`Travis CI`切换为`Github Action`
- 域名由`note.dreamccc.club`切换至`note.bequick.run`
- 将主题 [Nexmoe](https://github.com/theme-nexmoe/hexo-theme-nexmoe) 的部署方式由Git-Submodel迁移至NPM
- 文章和标题图片使用OSS回源叠加CDN，提高大陆地区可用性
- 基于 [hexo-action](https://github.com/daizc311/hexo-action) 定制了打包镜像用以解决境外CDN可用性不高的问题
- 添加SiteMap并提交至 [Google Search Console](https://search.google.com/search-console)

<!--more-->
