package com.nkwarealestate.expenditure.datastructures;

/**
 * Custom Stack implementation using arrays
 * Used for receipt/invoice processing queues and validation workflows
 */
public class CustomStack<T> {

    private static final int DEFAULT_CAPACITY = 10;
    private T[] stack;
    private int top;
    private int capacity;

    /**
     * Constructor with default capacity
     */
    @SuppressWarnings("unchecked")
    public CustomStack() {
        this.capacity = DEFAULT_CAPACITY;
        this.stack = (T[]) new Object[capacity];
        this.top = -1;
    }

    /**
     * Constructor with specified capacity
     */
    @SuppressWarnings("unchecked")
    public CustomStack(int capacity) {
        this.capacity = capacity;
        this.stack = (T[]) new Object[capacity];
        this.top = -1;
    }

    /**
     * Push an element onto the stack
     */
    public void push(T item) {
        if (isFull()) {
            resize();
        }
        stack[++top] = item;
    }

    /**
     * Pop an element from the stack
     */
    public T pop() {
        if (isEmpty()) {
            throw new RuntimeException("Stack is empty");
        }
        T item = stack[top];
        stack[top--] = null; // Help GC
        return item;
    }

    /**
     * Peek at the top element without removing it
     */
    public T peek() {
        if (isEmpty()) {
            throw new RuntimeException("Stack is empty");
        }
        return stack[top];
    }

    /**
     * Check if stack is empty
     */
    public boolean isEmpty() {
        return top == -1;
    }

    /**
     * Check if stack is full
     */
    public boolean isFull() {
        return top == capacity - 1;
    }

    /**
     * Get current size of stack
     */
    public int size() {
        return top + 1;
    }

    /**
     * Get capacity of stack
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Resize the stack when it becomes full
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = capacity * 2;
        T[] newStack = (T[]) new Object[newCapacity];

        for (int i = 0; i <= top; i++) {
            newStack[i] = stack[i];
        }

        stack = newStack;
        capacity = newCapacity;
    }

    /**
     * Clear all elements from the stack
     */
    public void clear() {
        while (!isEmpty()) {
            pop();
        }
    }

    /**
     * Convert stack to array (bottom to top order)
     */
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        T[] result = (T[]) new Object[size()];
        for (int i = 0; i <= top; i++) {
            result[i] = stack[i];
        }
        return result;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i <= top; i++) {
            sb.append(stack[i]);
            if (i < top) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
