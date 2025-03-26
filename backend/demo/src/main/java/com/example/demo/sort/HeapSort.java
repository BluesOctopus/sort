package com.example.demo.sort;

public class HeapSort extends Sorter {
    private void heap(int[] arr, int n, int i) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;
        
        if (left < n) {
            if (visualizer != null) {
                visualizeCompare(largest, left);

            }
            if (arr[left] > arr[largest])
                largest = left;
        }
        
        if (right < n) {
            if (visualizer != null) {
                visualizeCompare(largest, right);

            }
            if (arr[right] > arr[largest])
                largest = right;
        }
        
        if (largest != i) {
            int swap = arr[i];
            arr[i] = arr[largest];
            arr[largest] = swap;
            
            if (visualizer != null) {
                visualizeSwap(i, largest);

            }
            
            heap(arr, n, largest);
        }
    }
    
    @Override
    public void sort(int[] arr) {
        int n = arr.length;
        for (int i = n / 2 - 1; i >= 0; i--) {
            heap(arr, n, i);
        }
        for (int i = n - 1; i > 0; i--) {
            int swap = arr[0];
            arr[0] = arr[i];
            arr[i] = swap;
            
            if (visualizer != null) {
                visualizeSwap(0, i);

            }
            
            heap(arr, i, 0);
        }
    }
    

} 