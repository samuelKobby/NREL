package com.nkwarealestate.expenditure.datastructures;

/**
 * Custom Queue implementation using arrays with circular buffer approach
 * Used for transaction processing and workflow management
 */
public class CustomQueue<T> {

    private static final int DEFAULT_CAPACITY = 10;
    private T[] queue;
    private int front;
    private int rear;
    private int size;
    private int capacity;

    /**
     * Constructor with default capacity
     */
    @SuppressWarnings("unchecked")
    public CustomQueue() {
        this.capacity = DEFAULT_CAPACITY;
        this.queue = (T[]) new Object[capacity];
        this.front = 0;
        this.rear = -1;
        this.size = 0;
    }

    /**
     * Constructor with specified capacity
     */
    @SuppressWarnings("unchecked")
    public CustomQueue(int capacity) {
        this.capacity = capacity;
        this.queue = (T[]) new Object[capacity];
        this.front = 0;
        this.rear = -1;
        this.size = 0;
    }

    /**
     * Add an element to the rear of the queue
     */
    public void enqueue(T item) {
        if (isFull()) {
            resize();
        }
        rear = (rear + 1) % capacity;
        queue[rear] = item;
        size++;
    }

    /**
     * Remove and return the front element of the queue
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new RuntimeException("Queue is empty");
        }
        T item = queue[front];
        queue[front] = null; // Help GC
        front = (front + 1) % capacity;
        size--;
        return item;
    }

    /**
     * Peek at the front element without removing it
     */
    public T peek() {
        if (isEmpty()) {
            throw new RuntimeException("Queue is empty");
        }
        return queue[front];
    }

    /**
     * Check if queue is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Check if queue is full
     */
    public boolean isFull() {
        return size == capacity;
    }

    /**
     * Get current size of queue
     */
    public int size() {
        return size;
    }

    /**
     * Get capacity of queue
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Resize the queue when it becomes full
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = capacity * 2;
        T[] newQueue = (T[]) new Object[newCapacity];

        // Copy elements from front to rear
        for (int i = 0; i < size; i++) {
            newQueue[i] = queue[(front + i) % capacity];
        }

        queue = newQueue;
        front = 0;
        rear = size - 1;
        capacity = newCapacity;
    }

    /**
     * Clear all elements from the queue
     */
    public void clear() {
        while (!isEmpty()) {
            dequeue();
        }
    }

    /**
     * Convert queue to array (front to rear order)
     */
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        T[] result = (T[]) new Object[size];
        for (int i = 0; i < size; i++) {
            result[i] = queue[(front + i) % capacity];
        }
        return result;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(queue[(front + i) % capacity]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
