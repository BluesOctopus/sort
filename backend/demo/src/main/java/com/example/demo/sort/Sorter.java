package com.example.demo.sort;

public abstract class Sorter {
    protected SortingVisualizer visualizer;
    public void setVisualizer(SortingVisualizer visualizer) {
        this.visualizer = visualizer;
    }
    public abstract void sort(int[] arr);

    protected void visualizeSwap(int i, int j) {
        if (visualizer != null) {
            visualizer.onSwap(i, j);
        }
    }

    protected void visualizeCompare(int i, int j) {
        if (visualizer != null) {
            visualizer.onCompare(i, j);
        }
    }

    protected void visualizeWrite(int index, int value) {
        if (visualizer != null) {
            visualizer.onWrite(index, value);
        }
    }
} 