---
title: '计划任务的Spring实现与手动实现'
date: 2019/06/04 10:23:06
updated: 2020/2/20 15:41:36
cover: https://dreamccc-note-ia.oss-cn-chengdu.aliyuncs.com/images/posts/计划任务的Spring实现与手动实现/title.jpg
comments: true
categories: 
    - [我永远爱学习]
tags: 
    - java
    - spring
---

手写计划任务当然比不过简单又好用的SpringScheduled
<!--more-->
## 使用Spring Scheduled的计划任务 

### 关键方法

- 计算下一次匹配CRON表达式的时间
- `new CronSequenceGenerator("1 1 0 1 * ?").next(new Date());`
- 该方法用于计算下一次运行的到现在时间的时间差
- `org.springframework.scheduling.concurrent.ReschedulingRunnable#schedule();`
    
### 实现代码

```java
/**
 * <h2>ScheduleConfig</h2>
 * <p>定时任务配置</p>
 *
 * @author Daizc
 * @date 2019/12/10
 */
@Component
@EnableScheduling
public class ScheduleConfig {

    // 每月第一天的0分0秒执行
    public static final String CRON_EXPRESSION = "0 0 0 1 * ?";

    @Scheduled(cron = CRON_EXPRESSION, zone = "Asia/Shanghai")
    public void generateFormJob() {
        System.out.println(new Date().toString() + " &gt;&gt;计划任务执行....");
    }
}
```
## 自己写的计划任务

### 思路

- 使用一个阻塞队列
- 使用一个线程去消费队列
- 使用一个线程在计算`现在到下次执行时间的时间差`并睡到下次执行时间将Runable放入队列中消费

### 实现代码

```java

    /**
     * 自己写的计划任务
     */
    private LinkedBlockingQueue<runnable> queue = new LinkedBlockingQueue&lt;&gt;();

    private Runnable task = () -&gt; {
        System.out.println(new Date().toString() + " &gt;&gt;计划任务执行....");
    };

    @SuppressWarnings({"all", "AlibabaAvoidManuallyCreateThread"})
    public ScheduleConfig() {
        Thread take = new Thread(() -&gt; {
            try {
                while (true) {
                    Runnable runnable = runnable = queue.take();
                    runnable.run();
                }
            } catch (InterruptedException ignored) {}
        });
        take.setDaemon(true);
        take.setName("schedule-take");

        Thread put = new Thread(() -&gt; {
            Runnable runnable = null;
            try {
                while (true) {
                    // 计算下次执行时间
                    Date next = new CronSequenceGenerator(CRON_EXPRESSION).next(new Date());
                    Thread.sleep(next.getTime() - System.currentTimeMillis());
                    queue.put(task);
                }
            } catch (InterruptedException ignored) {}
        });
        put.setDaemon(true);
        put.setName("schedule-put");

        take.start();
        put.start();
    }
```
