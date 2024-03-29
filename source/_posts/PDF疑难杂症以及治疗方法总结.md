---
title: PDF疑难杂症
date: 2021-08-26 17:50:10
comments: true
categories:
- 我永远爱学习
tags:
- PDF

cover: https://dreamccc-note.oss-cn-chengdu.aliyuncs.com/note/images/posts/PDF疑难杂症以及治疗方法总结/DC-about-pdf-new-720x474.png.img.png?x-oss-process=style/blog_title

---

PDF是一个由Adobe编写现由国际标准化组织 (ISO) 维护的一个开放式标准，PDF 文档可以包含链接和按钮、表单域、音频、视频和业务逻辑。这种文件可进行电子签名，因此政府机构大量在用，但是仍然是个煞笔格式。

<!--more-->
## 编辑PDF相关的建议
- IText有一整套pdf模板生成-填充-签名的工具链，如果公司愿意可以直接买服务，如果不愿意使用，那至少在pdf模板制作时需要统一PDF编辑工具。
- IText工具集中包含一个`itext-rups`，该工具能展示pdf的结构树，方便debug
- 推荐使用的pdf编辑工具为`PDF-XChange Editor`和`Adobe Acrobat DC`

## 填充PDF容易遇到的问题
### 文本域无法填充中文
    检查文本框中使用的字体是否内嵌在Pdf中,如果没有内嵌，那么随便找一个文字，将该文字的字体设置为需要内嵌的字体并保存即可
### 减小pdf的体积
    可以使用编辑工具优化pdf，将未使用的字体都清理出pdf，pdf将只保留使用过的字形，这可能会影响不带字体的文本域填充
### 文本域填充图片
    在pdf标准中未定义图片文本域，当下的图片文本域都是基于ButtonField变形的，所以只需要给这个Button设置背景图片或者直接设置值为Base64即可
### 按钮填充样式异常
    pdf并未通过类型区分单选/复选框，而是为按钮定义了一系列的属性来决定其行为和样式
    其中有一个样式列表，当按钮的value命中样式列表的key时，就会应用样式列表预定义的样式
    因此直接检查为按钮填充的值是否与pdf工具中定义`按钮值`一致即可
    *需要注意样式列表的key不能是中文，在`Acrobat`中`按钮值`被设置为中文时，会将`按钮值`定义在该按钮的opt属性中，在样式列表中定义key`"0"`，再将opt的属性映射过去，这种情况并不受大部分PDF的操作工具的支持，但是可以直接将按钮值填充为`"0"`来解决这个问题
