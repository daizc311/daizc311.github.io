---
title: '[Csharp]循环链表封装' 
date: 2023-12-05 23:12:05

categories:

- [我永远爱学习]

tags:

- 'CSharp'
- '.NET'
---
# [Csharp]循环链表封装

## 要求

- 需要具有线程安全特性
- 可以直接找出上一个、当前值和下一个值

## 思路

一开始是打算直接拿LinkedList改改的，打算直接把LinkedListNode首尾连起来，然后直接用就是了。
但随即发现一个问题：要是谁谁谁不小心for了一下这list，那还得了？而且线程安全也没法保证啊。   
设计一个`CreateCircularList`，通过`CreateCircularIterator`方法生产一个Iterator,再去迭代这个Iterator就没问题了。
但随后也发现一个问题，这不够通用，我必须new一个`CreateCircularList`，

<!--more-->


```c#
using System.Collections.Generic;

#pragma warning disable CS0219 // 变量已被赋值，但从未使用过它的值

namespace Wpf481.Utils;

public class CircularLinkedIterator<TData> where TData : struct {
    private readonly CircularLinkedList<TData> _list;

    public CircularLinkedIterator(ICollection<TData> collection) {
        _list = new CircularLinkedList<TData>(collection);
    }

    public int CurrentIndex { get; private set; } = -1;

    public void Next() {
        lock (this) {
            if (_list.Count == 0) {
                CurrentIndex = -1;
                return;
            }
            var index = CurrentIndex + 1;
            CurrentIndex = index == _list.Count ? 0 : index;
        }
    }

    public TData? NextItem { get { return _list.GetNodeByIndex(CurrentIndex + 1)?.Value; } }

    public TData? PreviousItem => _list.GetNodeByIndex(CurrentIndex - 1)?.Value;

    public TData? CurrentItem => _list.GetNodeByIndex(CurrentIndex)?.Value;


    private class CircularLinkedList<T> : LinkedList<T> where T : struct {
        internal CircularLinkedList(IEnumerable<T> collection) : base(collection) { }

        internal LinkedListNode<T>? GetNodeByIndex(int index) {
            if (Count == 0) {
                return null;
            }

            int realIndex = (Count + index) % Count;
            var current = First;
            for (int i = 0; i < realIndex; i++) {
                current = current?.Next ?? First;
            }
            return current;
        }
    }
}
```


## 单元测试

```c#
public class CircularLinkedListTest {
        public static void test() {
            LoggerFactory.Create(builder => { }).CreateLogger<CircularLinkedListTest>();

            var list = new List<int>();

            var iterator = new CircularLinkedIterator<int>(list);

            for (int i = 0; i < 3; i++) {
                Console.WriteLine(
                    $"[{iterator.CurrentIndex}]\tlast: {iterator.PreviousItem} \t current: {iterator.CurrentItem}\t next: {iterator.NextItem}");
                iterator.Next();
            }

            for (int i = 1; i <= 2; i++) {
                list.Add(i);
            }

            iterator = new CircularLinkedIterator<int>(list);
            for (int i = 0; i < 10; i++) {
                Console.WriteLine(
                    $"[{iterator.CurrentIndex}]\tlast: {iterator.PreviousItem} \t current: {iterator.CurrentItem}\t next: {iterator.NextItem}");
                iterator.Next();
            }

            list.Clear();

            iterator = new CircularLinkedIterator<int>(list);
            for (int i = 0; i < 3; i++) {
                Console.WriteLine(
                    $"[{iterator.CurrentIndex}]\tlast: {iterator.PreviousItem} \t current: {iterator.CurrentItem}\t next: {iterator.NextItem}");
                iterator.Next();
            }
        }
    }
}
```