---
title: '[Csharp]Task相关'
date: 2023-12-14 11:20:22

categories:

  - 我永远爱学习

tags:

  - 'CSharp'
  - '.NET'
---

<!--more-->

## async与await

**示例代码**

```c#
public async static void test() {
    XmlConfigurator.Configure(new System.IO.FileInfo("log4net.config"));
     TaskCompletionSource<string> source = new TaskCompletionSource<string>();
    new Thread(o => {
        log.Info("Thread Sleep");
        Thread.Sleep(1000);
        log.Info("Thread TrySetResult");
        // source.TrySetResult("6666666666");
        // source.TrySetCanceled();
        source.TrySetException(new Exception("666"));
    }).Start();
      log.Info("Start await");
    try {
        string waitAsync = await source.Task.WaitAsync(new TimeSpan(0, 0, 5));
        log.Info("End await => " + waitAsync);
        // string s = await test2(source);
    } catch (TimeoutException e) {
        log.Info("TimeoutException", e);
    } catch (TaskCanceledException e) {
        log.Info("TaskCanceledException", e);
    }
    log.Info("End");
}
```

## Task.Wait

```c#
public static void testWait() {
    XmlConfigurator.Configure(new System.IO.FileInfo("log4net.config"));
    TaskCompletionSource<string> source = new TaskCompletionSource<string>();

    new Thread(o => {
        log.Info("Thread Sleep");
        Thread.Sleep(3000);
        log.Info("Thread TrySetResult");
        // source.TrySetResult("6666666666");
        source.TrySetCanceled();
        // source.TrySetException(new Exception());
    }).Start();

    log.InfoFormat("Start Wait");
    try {
        bool wait = source.Task.Wait(5000);
        log.InfoFormat("Wait:{0}", wait);
        if (wait) {
            if (source.Task.Status == TaskStatus.RanToCompletion) {
                log.InfoFormat("Result:{0}", source.Task.Result);
            } else if (source.Task.Status == TaskStatus.Faulted) {
                log.InfoFormat("Exception:{0}", source.Task.Exception);
            }
        } else {
            log.InfoFormat("Timeout");
        }
    } catch (Exception e) {
        log.InfoFormat("Exception: {0}=>{1}", e.GetType(),e.InnerException.GetType());
    }
}
```