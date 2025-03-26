package com.example.demo.sorter;

import java.io.*;
import java.util.*;

public class ExternalSort1 extends Sorter1 {
    private static final int CHUNK_SIZE = 1000;
    private File tempDir;
    
    public ExternalSort1() {
        tempDir = new File("temp_sort");
        tempDir.mkdir();
    }
    
    @Override
    public void sort1(int[] arr) {
        try {
            File inputFile = new File(tempDir, "input.txt");
            writeArrayToFile(arr, inputFile);
            
            List<File> sortedChunks = splitAndSort(inputFile);
            File outputFile = new File(tempDir, "output.txt");
            mergeFiles(sortedChunks, outputFile);
            
            readFileToArray(outputFile, arr);
            
            cleanup();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void writeArrayToFile(int[] arr, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int value : arr) {
                writer.write(String.valueOf(value));
                writer.newLine();
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
            
            while ((line = reader.readLine()) != null) {
                int value = Integer.parseInt(line);
                chunk.add(value);
                count++;
                
                if (count == CHUNK_SIZE) {
                    File tempFile = writeSortedChunk(chunk, fileIndex++);
                    tempFiles.add(tempFile);
                    chunk.clear();
                    count = 0;
                }
            }
            
            if (!chunk.isEmpty()) {
                File tempFile = writeSortedChunk(chunk, fileIndex);
                tempFiles.add(tempFile);
            }
        }
        
        return tempFiles;
    }
    
    private File writeSortedChunk(List<Integer> chunk, int index) throws IOException {
        int[] chunkArray = chunk.stream().mapToInt(i -> i).toArray();
        Arrays.sort(chunkArray);
        
        File tempFile = new File(tempDir, "chunk_" + index + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            for (int value : chunkArray) {
                writer.write(String.valueOf(value));
                writer.newLine();
            }
        }
        return tempFile;
    }
    
    private void mergeFiles(List<File> files, File outputFile) throws IOException {
        List<BufferedReader> readers = new ArrayList<>();
        int[] currentValues = new int[files.size()];
        boolean[] finished = new boolean[files.size()];
        
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