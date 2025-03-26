package com.example.demo.sorter;

public class ShellSort1 extends Sorter1 {
    @Override
    public void sort1(int[] arr) {
        int n = arr.length;
        for (int gap = n / 2; gap > 0; gap /= 2) {
            for (int i = gap; i < n; i++) {
                int key = arr[i];
                int j = i - gap;

                while (j >= 0 && arr[j] > key) {
                    arr[j + gap] = arr[j];
                    j = j - gap;
                }
                arr[j + gap] = key;
            }
        }
    }
}
