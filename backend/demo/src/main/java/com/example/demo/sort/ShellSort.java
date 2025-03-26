package com.example.demo.sort;

public class ShellSort extends Sorter {
    @Override
    public void sort(int[] arr) {
        int n = arr.length;
        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                int key = arr[i];
                int j = i - gap;

                while (j >= 0) {
                    if (visualizer != null) {
                        visualizeCompare(j + gap, j);
                    }
                    if (arr[j] > key) {
                        arr[j + gap] = arr[j];
                        if (visualizer != null) {
                            visualizeWrite(j + gap, arr[j]);
                        }
                        j = j - gap;
                    } else {
                        break;
                    }
                }
                arr[j + gap] = key;
                if (visualizer != null) {
                    visualizeWrite(j + gap, key);
                }
            }
        }
    }
} 