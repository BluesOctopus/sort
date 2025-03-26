package com.example.demo.sort;

public class SelectSort extends Sorter {
    @Override
    public void sort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            int min1 = i;
            for (int j = i + 1; j < n; j++) {
                if (visualizer != null) {
                    visualizeCompare(j, min1);
                }
                if (arr[j] < arr[min1]) {
                    min1 = j;
                }
            }
            if (min1 != i) {
                int swap = arr[i];
                arr[i] = arr[min1];
                arr[min1] = swap;

                if (visualizer != null) {
                    visualizeSwap(i, min1);
                }
            }
        }
    }
} 