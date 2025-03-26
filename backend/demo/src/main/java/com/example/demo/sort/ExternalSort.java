package com.example.demo.sort;

import java.io.*;
import java.util.*;

public class ExternalSort extends Sorter {
    private static final int CHUNK_SIZE = 1000;
    private File tempDir;
    
    public ExternalSort() {
        tempDir = new File("temp_sort");
        tempDir.mkdir();
    }
    
    @Override
    public void sort(int[] arr) {
        try {
            // 将数组写入临时文件，同时可视化写入操作
            File inputFile = new File(tempDir, "input.txt");
            writeArrayToFile(arr, inputFile);
            
            List<File> sortedChunks = splitAndSort(inputFile);
            File outputFile = new File(tempDir, "output.txt");
            mergeFiles(sortedChunks, outputFile);
            
            // 读回结果到原数组时进行可视化
            readFileToArray(outputFile, arr);
            
            cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void writeArrayToFile(int[] arr, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < arr.length; i++) {
                writer.write(String.valueOf(arr[i]));
                writer.newLine();
                if (visualizer != null) {
                    visualizer.onWrite(i, arr[i]);
                }
            }
        }
    }
    
    private List<File> splitAndSort(File inputFile) throws IOException {
        List<File> tempFiles = new ArrayList<>();
        List<Integer> chunk = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
            String line;
            int count = 0;
            int fileIndex = 0;
            int globalIndex = 0;  // 用于跟踪全局位置
            
            while ((line = reader.readLine()) != null) {
                int value = Integer.parseInt(line);
                chunk.add(value);
                if (visualizer != null) {
                    visualizer.onWrite(globalIndex++, value);
                }
                count++;
                
                if (count == CHUNK_SIZE) {
                    File tempFile = writeSortedChunk(chunk, fileIndex++, globalIndex - chunk.size());
                    tempFiles.add(tempFile);
                    chunk.clear();
                    count = 0;
                }
            }
            
            if (!chunk.isEmpty()) {
                File tempFile = writeSortedChunk(chunk, fileIndex, globalIndex - chunk.size());
                tempFiles.add(tempFile);
            }
        }
        
        return tempFiles;
    }
    
    private File writeSortedChunk(List<Integer> chunk, int index, int startIndex) throws IOException {
        // 将List转换为数组并排序
        int[] chunkArray = chunk.stream().mapToInt(i -> i).toArray();
        
        // 使用QuickSort并传递可视化器
        QuickSort quickSort = new QuickSort();
        quickSort.setVisualizer(new OffsetVisualizer(visualizer, startIndex));
        quickSort.sort(chunkArray);
        
        // 写入排序后的块到文件
        File tempFile = new File(tempDir, "chunk_" + index + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            for (int i = 0; i < chunkArray.length; i++) {
                writer.write(String.valueOf(chunkArray[i]));
                writer.newLine();
                if (visualizer != null) {
                    visualizer.onWrite(startIndex + i, chunkArray[i]);
                }
            }
        }
        return tempFile;
    }
    
    private void mergeFiles(List<File> files, File outputFile) throws IOException {
        List<BufferedReader> readers = new ArrayList<>();
        int[] currentValues = new int[files.size()];
        boolean[] finished = new boolean[files.size()];
        int outputIndex = 0;
        
        // 使用 try-with-resources 确保所有资源被正确关闭
        try {
            // 初始化所有读取器并读取第一个元素
            for (int i = 0; i < files.size(); i++) {
                BufferedReader reader = new BufferedReader(new FileReader(files.get(i)));
                readers.add(reader);
                String line = reader.readLine();
                if (line != null) {
                    currentValues[i] = Integer.parseInt(line);
                } else {
                    finished[i] = true;
                }
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
                while (true) {
                    int minIndex = getMinIndex(currentValues, finished);
                    if (minIndex == -1) break;
                    
                    writer.write(String.valueOf(currentValues[minIndex]));
                    writer.newLine();
                    
                    if (visualizer != null) {
                        visualizer.onWrite(outputIndex++, currentValues[minIndex]);
                    }
                    
                    String line = readers.get(minIndex).readLine();
                    if (line != null) {
                        currentValues[minIndex] = Integer.parseInt(line);
                    } else {
                        finished[minIndex] = true;
                    }
                }
            }
        } finally {
            // 关闭所有读取器
            for (BufferedReader reader : readers) {
                if (reader != null) {
                    reader.close();
                }
            }
        }
    }
    
    private int getMinIndex(int[] values, boolean[] finished) {
        int minIndex = -1;
        int minValue = Integer.MAX_VALUE;
        
        for (int i = 0; i < values.length; i++) {
            if (!finished[i]) {
                if (values[i] < minValue) {
                    minValue = values[i];
                    minIndex = i;
                }
            }
        }
        
        return minIndex;
    }
    
    private void readFileToArray(File file, int[] arr) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int index = 0;
            while ((line = reader.readLine()) != null && index < arr.length) {
                arr[index] = Integer.parseInt(line);
                if (visualizer != null) {
                    visualizer.onWrite(index, arr[index]);
                }
                index++;
            }
        }
    }
    
    private void cleanup() {
        File[] files = tempDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.delete()) {
                    System.err.println("Failed to delete file: " + file.getAbsolutePath());
                }
            }
        }
        if (!tempDir.delete()) {
            System.err.println("Failed to delete directory: " + tempDir.getAbsolutePath());
        }
    }
}

// 辅助类：用于处理块内排序时的索引偏移
class OffsetVisualizer implements SortingVisualizer {
    private final SortingVisualizer baseVisualizer;
    private final int offset;
    
    public OffsetVisualizer(SortingVisualizer baseVisualizer, int offset) {
        this.baseVisualizer = baseVisualizer;
        this.offset = offset;
    }
    
    @Override
    public void onSwap(int i, int j) {
        if (baseVisualizer != null) {
            baseVisualizer.onSwap(i + offset, j + offset);
        }
    }
    
    @Override
    public void onCompare(int i, int j) {
        if (baseVisualizer != null) {
            baseVisualizer.onCompare(i + offset, j + offset);
        }
    }
    
    @Override
    public void onWrite(int index, int value) {
        if (baseVisualizer != null) {
            baseVisualizer.onWrite(index + offset, value);
        }
    }
} 