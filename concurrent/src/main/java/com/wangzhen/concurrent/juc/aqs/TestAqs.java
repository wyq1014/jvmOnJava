package com.wangzhen.concurrent.juc.aqs;

import com.wangzhen.concurrent.util.SleepUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TestAqs {
    static Logger logger = LoggerFactory.getLogger(TestAqs.class);

    public static void main(String[] args) {
        MyLock lock = new MyLock();
        new Thread(()->{
            lock.lock();
            try {
                logger.debug("线程1加锁成功");
                SleepUtils.second(1);
            }finally {
                lock.unlock();
                logger.debug("线程1释放锁成功");
            }
        },"t1").start();

        new Thread(()->{
            lock.lock();
            try {
                logger.debug("线程2加锁成功");
            }finally {
                lock.unlock();
                logger.debug("线程2释放锁成功");
            }
        },"t2").start();

    }
}

class MyLock implements Lock{
    // 独占锁 同步器类
    class MySync extends AbstractQueuedSynchronizer{
        @Override
        protected boolean tryAcquire(int arg) {
            if(compareAndSetState(0,1)){
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }else {
                return false;
            }
        }

        @Override
        protected boolean tryRelease(int arg) {
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        // 是否处于占用状态
        @Override
        protected boolean isHeldExclusively() {
            return getState()==1;
        }

        public Condition newCondition(){
            return new ConditionObject();
        }
    }

    private MySync mySync = new MySync();


    // 加锁，不成功会进入等待队列
    @Override
    public void lock() {
        mySync.acquire(1);
    }

    // 可打断的加锁
    @Override
    public void lockInterruptibly() throws InterruptedException {
        mySync.acquireInterruptibly(1);
    }

    // 尝试加锁（一次）
    @Override
    public boolean tryLock() {

        return mySync.tryAcquire(1);
    }

    // 待超时时间的加锁
    @Override
    public boolean tryLock(long time, @NotNull TimeUnit unit) throws InterruptedException {
        return mySync.tryAcquireNanos(1,unit.toNanos(time));
    }

    // 解锁
    @Override
    public void unlock() {
        mySync.release(1);
    }

    // 创建条件变量
    @NotNull
    @Override
    public Condition newCondition() {
        return mySync.newCondition();
    }
}
