package com.example.demo.sort;

public class BubbleSort extends Sorter {
    @Override
    public void sort(int[] array) {
            for (int i = 0; i < array.length - 1; i++) {
                for (int j = 0; j < array.length - i - 1; j++) {
                    visualizeCompare(j, j + 1);


                    if (array[j] > array[j + 1]) {
                        visualizeSwap(j, j + 1);
                        int temp = array[j];
                        array[j] = array[j + 1];
                        array[j + 1] = temp;
                    }
                }
            }

    }
} 