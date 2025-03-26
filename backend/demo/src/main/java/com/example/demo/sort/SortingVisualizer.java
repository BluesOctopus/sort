package com.example.demo.sort;

public interface SortingVisualizer {
    void onSwap(int i, int j);
    void onCompare(int i, int j);
    void onWrite(int index, int value);
}