---
title: Bash脚本经验之谈
date: 2023-06-19 15:50:00
cover: https://oss.note.dreamccc.cn/note/images/posts/Bash脚本经验之谈/title.png
categories:
- [我永远爱学习]

tags:
- 乱七八糟

---

最近将CI/CD从Gitlab迁移到我自己写的CI/CD平台(自豪),在切换期间,需要使用脚本打通两个系统,于是乎这几天都在写bash脚本,真的快写吐了,各种命令语法花里胡哨,超简单的操作都需要绕半天才能成功.  
总的来说就是'使用各种不规范的写法调用各种东拼西凑来的工具来处理各种稀奇古怪的数据'.   
写的过程中也积累了不少经验教训,做个笔记分享一下（虽然也没人看）.

<!--more-->

### 总结的一些教训

1. 赋值语句等号后别带空格
    ```bash
    root@7f1ca1fb0b2e:/# A = 666
    bash: A: command not found
    root@7f1ca1fb0b2e:/# A=666
    root@7f1ca1fb0b2e:/# echo $A
    666
    root@7f1ca1fb0b2e:/# 
    ```
    - 注: 写快了很容易犯低超低级错误

2. 使用`$`符号取值时,单引号中的值不会计算:
    ```bash
    root@7f1ca1fb0b2e:/# A=666
    root@7f1ca1fb0b2e:/# echo $A
    666
    root@7f1ca1fb0b2e:/# echo "$A"
    666
    root@7f1ca1fb0b2e:/# echo '$A'
    $A
    ```
    - 注: 超长字符串时注意需要分辨
  

3. 多行语句时,切记检查`转义符\`后有无空格
    ```bash
    root@7f1ca1fb0b2e:/# echo \
    > $A
    666
    root@7f1ca1fb0b2e:/# echo \ 

    root@7f1ca1fb0b2e:/# $A
    bash: 666: command not found
    ```
    - 注: 答应我在vscode里写好了再粘贴过去好不?

4. 别在单引号字符串里使用转义符
    ```bash
    root@7f1ca1fb0b2e:/# echo asd\
    > dsa
    asddsa
    root@7f1ca1fb0b2e:/# echo "asd\
    > dsa"
    asddsa
    root@7f1ca1fb0b2e:/# echo 'asd\
    dsa'
    asd\
    dsa
    ```
    - 注: 眼睛都看直了没看出来问题,最后发现是个单引号
  
5. 使用if块时,注意前后留空格
    ```bash
    root@7f1ca1fb0b2e:/# if [[-z "$UNIT_TEST" ]] ;then echo "true";else echo "false";fi;
    bash: [[-z: command not found
    false
    root@7f1ca1fb0b2e:/# if [[ -z "$UNIT_TEST" ]] ;then echo "true";else echo "false";fi;
    true    
    ```

6. jq取值时,直接取出的值是jsonFormat的,需要加`-r`参数取原始值
    ```bash
    root@7f1ca1fb0b2e:/# echo '{"key":"value"}'|jq .key
    "value"
    root@7f1ca1fb0b2e:/# echo '{"key":"value"}'|jq -r .key
    value
    ```
    - 注: 用之前先看看`--help`吧,求你了

7. 使用jq拼接json最好的办法是用模板字符串(参数能够确定时)
    ```bash
    root@7f1ca1fb0b2e:/# PARAM=$( jq -n  \
    --arg beam_projectKey "$beam_projectKey"\
    --arg beam_version "$beam_version"\
    --arg beam_branch "$beam_branch"\
    --arg beam_description "$beam_description"\
    --arg beam_commitHash "$beam_commitHash"\
    --arg beam_imageTag "$beam_imageTag" \
     '{projectKey:$beam_projectKey,version: $beam_version,branch: $beam_branch,description: $beam_description,commitHash: $beam_commitHash,imageTag: $beam_imageTag,createUserId: "USR-000000",createUserName: "GitLab-CI"}'\
    )
    ```
   - 注: 千万别想着直接用jq动态插入/替换值,真的很累

### 吸取的一些经验

1. 多使用转义换行,活用括号分组,空格多加几个,以增加可读性(可读性仍然很低)
    ```bash
    echo '推送到Beam[本地]' && \
    RESULT=$(curl -s -XPOST "http://192.167.20.38:8888/artifact/save" \
      -H "Accept: application/json" \
      -H "Content-Type: application/json" \
      -H "Auth: ******" \
      --data-raw "$PARAM"  --compressed) && \
    echo $RESULT|jq . || \
    echo '[本地]Beam服务未启动'
    ```
    - 上面这行指令由3个指令连接而成,关键地方做了换行并添加空格,看起来更有层次感.
    - 使用`&&`进行指令连接,最后使用`||`兜底,相当于做了个`try-catch`.使得这行指令的返回值始终为'0'
2. 写流水线时不要头铁直接启动流水线进行调试,使用shell反弹将流水线中的bash反弹到本机进行调试,最后一次性将命令粘进脚本中,这样更省时间.
3. 能写python不写bash,如果流水线由自己控制,那尽量保证镜像中带一个python环境,不要在乎那点大小,能快速出活才是最关键的.
4. 如果bash脚本需要定义在yaml文件中,遇到长指令的时候最好使用`|`文本块放置
    ```yaml
    aaa:
        bbb:
        - echo start
        - |
          echo '推送到Beam[本地]' && \
          RESULT=$(curl -s -XPOST "http://192.167.20.38:8888/artifact/save" \
            -H "Accept: application/json" \
            -H "Content-Type: application/json" \
            -H "Auth: ******" \
            --data-raw "$PARAM"  --compressed) && \
          echo $RESULT|jq . || \
          echo '[本地]Beam服务未启动'
        - echo end
    ```