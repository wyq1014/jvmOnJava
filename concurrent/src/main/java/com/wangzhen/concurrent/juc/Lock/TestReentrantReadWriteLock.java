package com.wangzhen.concurrent.juc.Lock;

import jdk.nashorn.internal.ir.CallNode;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Description: 测试 可重入读写锁
 * Datetime:    2021/2/1   16:15
 * Author:   王震
 */
@Slf4j
public class TestReentrantReadWriteLock {

    class ReadWirteArrayList extends ArrayList {
        private ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();
        private ReentrantReadWriteLock.ReadLock readLock = reentrantReadWriteLock.readLock();
        private ReentrantReadWriteLock.WriteLock writeLock = reentrantReadWriteLock.writeLock();
        @Override
        public Object get(int index) {
            readLock.lock();
            try {
                return super.get(index);
            }finally {
                readLock.unlock();
            }

        }

        @Override
        public boolean add(Object o) {
            writeLock.lock();
            try {
                return super.add(o);
            }finally {
                writeLock.unlock();
            }
        }
    }

    /**
     * 测试读写锁的一些特性
     */
    @Test
    public void  test01(){
        ReadWirteArrayList list = new ReadWirteArrayList();

    }

    /**
     * 说明: 测试1000个线程同时 put 1000 个数据耗费的时间
     * 1.vector  并发写速度比较慢。
     * 2.ArrayList 速度快，但是得到的结果错误。
     * @throws InterruptedException
     */
    @Test
    public void test02() throws InterruptedException {
        long start = System.currentTimeMillis();
       List<Integer> integers = new Vector<>(100000);

//        List<Integer> integers = new ArrayList<>(1000000);
        integers.add(0);
        Thread[] readThread = new Thread[1000];
        Thread[] writeThreads = new Thread[1000];
        for (int i = 0; i < readThread.length; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    integers.get(0);
                }
            });
            readThread[i] = thread;
        }
        for (int i = 0; i < writeThreads.length; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    integers.add(j);
                }
            });
            writeThreads[i] = thread;
        }

        for (int i = 0; i < readThread.length; i++) {
            readThread[i].start();
            writeThreads[i].start();
        }
        for (int i = 0; i < readThread.length; i++) {
            readThread[i].join();
            writeThreads[i].join();
        }

        long end = System.currentTimeMillis();

        System.out.println(integers.size());
//        for (int i = 0; i < integers.size(); i++) {
//            System.out.println(integers.get(i));
//        }
        System.out.println(end-start);


    }
}