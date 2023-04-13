package com.sunzy.vulfocus.model;

import org.apache.tomcat.util.threads.TaskQueue;

public class Timer {
    //根据时间进行优先排序的队列
    private final TaskQueue queue = new TaskQueue();
/*
    //消费线程，对queue中的定时任务进行编排和执行
    private final TimerThread thread = new TimerThread(queue);

    //构造函数
    public Timer(String name) {
        thread.setName(name);
        thread.start();
    }*/

}
