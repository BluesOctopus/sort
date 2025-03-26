// 排序服务，负责排序和可视化
package com.example.demo.controller;

import org.springframework.stereotype.Service;

import com.example.demo.sort.*;
import com.example.demo.sorter.*;

@Service
public class SortingService {
    private final java.util.Random random = new java.util.Random();
    private final WebSocketVisualizer webSocketVisualizer;

    public SortingService(WebSocketVisualizer webSocketVisualizer) {
        this.webSocketVisualizer = webSocketVisualizer;
    }

    public Long startSorting(SortingRequest request) {
        if (request.getDisplayMode().equals("Sorter1")) {
            int[] array = generateRandomArray(request.getArraySize()); 
            Sorter1 sorter1 = createSorter1(request.getAlgorithm());  
            long startTime = System.nanoTime();
            sorter1.sort1(array);
            long endTime = System.nanoTime();
            long durationInMillis = (endTime - startTime) / 1_000_000;
            webSocketVisualizer.sendMessage("Sorting Time: " + durationInMillis + " ms");
            return durationInMillis;
        } else {
            int[] array = generateRandomArray(request.getArraySize());   
            webSocketVisualizer.sendMessage("Array:" + java.util.Arrays.toString(array)); // 发送数组
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); 
            } // 防止卡bug，有可能出现array比action慢
            
            Sorter sorter = createSorter(request.getAlgorithm());
            sorter.setVisualizer(webSocketVisualizer);
            sorter.sort(array);
            return null; 
        }
    }

    private int[] generateRandomArray(int size) {
        int[] array = new int[size];
        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt(size) + 1;
        }
        return array;
    }

    private Sorter createSorter(String algorithm) {
        switch (algorithm) {
            case "HeapSort":
                return new HeapSort();
            case "SelectSort":
                return new SelectSort();
            case "QuickSort":
                return new QuickSort();
            case "BubbleSort":
                return new BubbleSort();
            case "ShellSort":
                return new ShellSort();
            case "InsertionSort":
                return new InsertionSort();
            case "ExternalSort":
                return new ExternalSort();
            default:
                throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }
    }

    private Sorter1 createSorter1(String algorithm) {
        switch (algorithm) {
            case "HeapSort":
                return new HeapSort1();
            case "SelectSort":
                return new SelectSort1();
            case "QuickSort":
                return new QuickSort1();
            case "BubbleSort":
                return new BubbleSort1();
            case "ShellSort":
                return new ShellSort1();
            case "InsertionSort":
                return new InsertionSort1();
            case "ExternalSort":
                return new ExternalSort1();
            default:
                throw new IllegalArgumentException("Unsupported algorithm: " + algorithm);
        }
    }
} 