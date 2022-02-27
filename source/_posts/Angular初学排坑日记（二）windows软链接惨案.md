---
title: Angular初学排坑日记（二） windows软链接惨案
date: 2022-01-03 02:32:10
cover: https://dreamccc-note-ia.oss-cn-chengdu.aliyuncs.com/images/posts/Angular初学排坑日记（二）windows软链接惨案/title.png

categories:
- [我永远爱学习]

tags:
- 前端
- Angular
---

# Angular初学排坑日记（二） windows软链接惨案

上文讲到已经成功创建了Angular项目，现在总得跑下DEMO来试试水吧，于是打开webstorm导入项目，单击播放键。好家伙，新错误来了。
```shell
D:\Users\Link\WebstormProjects\angularDemo\src\polyfills.ts - Error: Module build failed (from D:\Users\Link\WebstormPro
                                                                                                                       ojects\angularDemo\node_modules\@ngtools\webpack\src\ivy\index.js):
Error: D:\Users\Link\WebstormProjects\angularDemo\src\polyfills.ts is missing from the TypeScript compilation. Please ma
                                                                                                                       ake sure it is in your tsconfig via the 'files' or 'include' property.
    at D:\Users\Link\WebstormProjects\angularDemo\node_modules\@ngtools\webpack\src\ivy\loader.js:60:26



** Angular Live Development Server is listening on localhost:4200, open your browser on http://localhost:4200/ **       


× Failed to compile.
√ Browser application bundle generation complete.
```
编译失败，但浏览器启动成功。弹出一个明晃晃的`Cannot GET /`。
<!--more-->
仔细看了下报错，但完全没明白这个提示的意思，感觉就是说`src\polyfills.ts`没找到，所以ts编译失败啥的，让我检查tsconfig里是否有配置`files`或者`include`属性。
```json
{
  "extends": "./tsconfig.json",
  "compilerOptions": {
    "outDir": "./out-tsc/app",
    "types": []
  },
  "files": [
    "src/main.ts",
    "src/polyfills.ts"
  ],
  "include": [
    "src/**/*.d.ts"
  ]
}
```
但是很遗憾的是，配置里都有配，但这个问题还是出现了，于是开始google。
- [Angular5 :polyfills.ts & \main.ts is missing from the TypeScript compilation](https://stackoverflow.com/questions/49091956/angular5-polyfills-ts-main-ts-is-missing-from-the-typescript-compilation)
感觉和我的症状很相似。这位朋友告诉我们修改`angular.json`添加一个属性就可以了。
```json lines
{ "projects": {
    "anglarDemo2": {
      "architect": {
        "build": {
          "options": {
            "preserveSymlinks": true  //  《==========加的这行
```
再次启动项目，已经可以正常启动了。随后根据上面的属性搜索了一下相关的资料，找到一个issues
- [main.ts is missing from de TypeScript compilation](https://github.com/angular/angular-cli/issues/9909)

总而言之就是windows软链接惹的祸，angular需要在配置里设置保留逻辑链接才能正常的读到文件。
起因是我在装系统时为了节约C盘的空间，将C:\User\Link这个目录通过Symlink的方式挂载到了D盘导致触发了这个报错。
