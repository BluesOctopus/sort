package com.example.demo.sort;

public class InsertionSort extends Sorter {
    @Override
    public void sort(int[] arr) {
        int n = arr.length;
        for (int i = 1; i < n; i++) {
            int key = arr[i];
            int j = i - 1;
            
            while (j >= 0) {
                if (visualizer != null) {
                    visualizeCompare(j + 1, j);
                }
                if (arr[j] > key) {
                    arr[j + 1] = arr[j];
                    if (visualizer != null) {
                        visualizeWrite(j + 1, arr[j]);
                    }
                    j = j - 1;
                } else {
                    break;
                }
            }
            arr[j + 1] = key;
            if (visualizer != null) {
                visualizeWrite(j + 1, key);
            }
        }
    }
} 