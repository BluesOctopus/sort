package com.example.demo.sort;

public class QuickSort extends Sorter {
    @Override
    public void sort(int[] array) {
        try {
            quickSort(array, 0, array.length - 1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // 恢复中断状态
        }
    }

    private void quickSort(int[] array, int low, int high) throws InterruptedException {
        if (low < high) {
            int pi = partition(array, low, high);
            quickSort(array, low, pi - 1);
            quickSort(array, pi + 1, high);
        }
    }

    private int partition(int[] array, int low, int high) {
        int pivot = array[high];
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            visualizeCompare(j, high); // 发送比较消息

            if (array[j] < pivot) {
                i++;
                visualizeSwap(i, j); // 发送交换消息
                int temp = array[i];
                array[i] = array[j];
                array[j] = temp;

                visualizeWrite(i, array[i]); // 发送写入消息
                visualizeWrite(j, array[j]); // 发送写入消息
            }
        }
        visualizeSwap(i + 1, high); // 发送交换消息
        int temp = array[i + 1];
        array[i + 1] = array[high];
        array[high] = temp;

        visualizeWrite(i + 1, array[i + 1]); // 发送写入消息
        visualizeWrite(high, array[high]); // 发送写入消息

        return i + 1;
    }
} 