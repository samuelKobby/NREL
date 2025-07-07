package com.nkwarealestate.expenditure.datastructures;

/**
 * Custom MinHeap implementation using arrays
 * Used for bank account balance monitoring and priority operations
 */
public class MinHeap<T extends Comparable<T>> {

    private static final int DEFAULT_CAPACITY = 10;
    private T[] heap;
    private int size;
    private int capacity;

    /**
     * Constructor with default capacity
     */
    @SuppressWarnings("unchecked")
    public MinHeap() {
        this.capacity = DEFAULT_CAPACITY;
        this.heap = (T[]) new Comparable[capacity];
        this.size = 0;
    }

    /**
     * Constructor with specified capacity
     */
    @SuppressWarnings("unchecked")
    public MinHeap(int capacity) {
        this.capacity = capacity;
        this.heap = (T[]) new Comparable[capacity];
        this.size = 0;
    }

    /**
     * Insert an element into the heap
     */
    public void insert(T element) {
        if (element == null) {
            throw new IllegalArgumentException("Cannot insert null element");
        }

        if (size >= capacity) {
            resize();
        }

        heap[size] = element;
        heapifyUp(size);
        size++;
    }

    /**
     * Extract the minimum element (root) from the heap
     */
    public T extractMin() {
        if (isEmpty()) {
            throw new RuntimeException("Heap is empty");
        }

        T min = heap[0];
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;

        if (size > 0) {
            heapifyDown(0);
        }

        return min;
    }

    /**
     * Peek at the minimum element without removing it
     */
    public T peek() {
        if (isEmpty()) {
            throw new RuntimeException("Heap is empty");
        }
        return heap[0];
    }

    /**
     * Check if heap is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Get current size of heap
     */
    public int size() {
        return size;
    }

    /**
     * Get capacity of heap
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Heapify up operation to maintain heap property after insertion
     */
    private void heapifyUp(int index) {
        int parentIndex = getParentIndex(index);

        if (parentIndex >= 0 && heap[index].compareTo(heap[parentIndex]) < 0) {
            swap(index, parentIndex);
            heapifyUp(parentIndex);
        }
    }

    /**
     * Heapify down operation to maintain heap property after extraction
     */
    private void heapifyDown(int index) {
        int leftChild = getLeftChildIndex(index);
        int rightChild = getRightChildIndex(index);
        int smallest = index;

        if (leftChild < size && heap[leftChild].compareTo(heap[smallest]) < 0) {
            smallest = leftChild;
        }

        if (rightChild < size && heap[rightChild].compareTo(heap[smallest]) < 0) {
            smallest = rightChild;
        }

        if (smallest != index) {
            swap(index, smallest);
            heapifyDown(smallest);
        }
    }

    /**
     * Get parent index of given index
     */
    private int getParentIndex(int index) {
        return (index - 1) / 2;
    }

    /**
     * Get left child index of given index
     */
    private int getLeftChildIndex(int index) {
        return 2 * index + 1;
    }

    /**
     * Get right child index of given index
     */
    private int getRightChildIndex(int index) {
        return 2 * index + 2;
    }

    /**
     * Swap elements at two indices
     */
    private void swap(int i, int j) {
        T temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    /**
     * Resize the heap when it becomes full
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = capacity * 2;
        T[] newHeap = (T[]) new Comparable[newCapacity];

        for (int i = 0; i < size; i++) {
            newHeap[i] = heap[i];
        }

        heap = newHeap;
        capacity = newCapacity;
    }

    /**
     * Clear all elements from the heap
     */
    public void clear() {
        for (int i = 0; i < size; i++) {
            heap[i] = null;
        }
        size = 0;
    }

    /**
     * Convert heap to array (not in sorted order)
     */
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        T[] result = (T[]) new Comparable[size];
        for (int i = 0; i < size; i++) {
            result[i] = heap[i];
        }
        return result;
    }

    /**
     * Build a heap from an existing array
     */
    @SuppressWarnings("unchecked")
    public static <T extends Comparable<T>> MinHeap<T> buildHeap(T[] array) {
        MinHeap<T> heap = new MinHeap<>(array.length * 2);

        for (T element : array) {
            if (element != null) {
                heap.insert(element);
            }
        }

        return heap;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(heap[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
