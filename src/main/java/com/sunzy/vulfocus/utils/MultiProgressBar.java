package com.sunzy.vulfocus.utils;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class MultiProgressBar {
    private static final int PROGRESS_BAR_WIDTH = 40;
    private static final int UPDATE_INTERVAL = 100; // 进度条更新时间间隔，单位为毫秒

    private static ExecutorService executor = Executors.newFixedThreadPool(2);

    public static void main(String[] args) {
        ProgressTask taskA = new ProgressTask("Task A", 10);
        ProgressTask taskB = new ProgressTask("Task B", 20);

        executor.submit(taskA);
        executor.submit(taskB);

        // 启动定时器，定期更新进度条
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            clearConsole();
            System.out.println("Task A: " + taskA.getProgressBar());
            System.out.print("Task B: " + taskB.getProgressBar());
        }, 0, UPDATE_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static class ProgressTask implements Runnable {
        private String name;
        private int total;
        private int progress;

        public ProgressTask(String name, int total) {
            this.name = name;
            this.total = total;
        }

        public String getProgressBar() {
            int completed = progress * PROGRESS_BAR_WIDTH / total;
            StringBuilder sb = new StringBuilder("[");
            for (int i = 0; i < completed; i++) {
                sb.append('=');
            }
            for (int i = completed; i < PROGRESS_BAR_WIDTH; i++) {
                sb.append(' ');
            }
            sb.append(']');
            sb.append(String.format(" %d%%", progress * 100 / total));
            sb.append("\r");
            return sb.toString();
        }

        @Override
        public void run() {
            for (int i = 0; i < total; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progress++;
            }
        }
    }
}

