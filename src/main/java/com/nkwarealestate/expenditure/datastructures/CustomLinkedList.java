package com.nkwarealestate.expenditure.datastructures;

/**
 * Custom LinkedList implementation built from scratch
 */
public class CustomLinkedList<T> {
    
    private Node<T> head;
    private Node<T> tail;
    private int size;
    
    /**
     * Node class for the linked list
     */
    private static class Node<T> {
        T data;
        Node<T> next;
        
        Node(T data) {
            this.data = data;
            this.next = null;
        }
    }
    
    public CustomLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }
    
    /**
     * Add element to the end of the list
     */
    public void add(T data) {
        Node<T> newNode = new Node<>(data);
        
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            tail = newNode;
        }
        
        size++;
    }
    
    /**
     * Add element at specific index
     */
    public void add(int index, T data) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        if (index == size) {
            add(data);
            return;
        }
        
        Node<T> newNode = new Node<>(data);
        
        if (index == 0) {
            newNode.next = head;
            head = newNode;
            if (tail == null) {
                tail = newNode;
            }
        } else {
            Node<T> current = head;
            for (int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            newNode.next = current.next;
            current.next = newNode;
        }
        
        size++;
    }
    
    /**
     * Get element at specific index
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        Node<T> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        
        return current.data;
    }
    
    /**
     * Remove element at specific index
     */
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        
        T data;
        
        if (index == 0) {
            data = head.data;
            head = head.next;
            if (head == null) {
                tail = null;
            }
        } else {
            Node<T> current = head;
            for (int i = 0; i < index - 1; i++) {
                current = current.next;
            }
            data = current.next.data;
            current.next = current.next.next;
            
            if (current.next == null) {
                tail = current;
            }
        }
        
        size--;
        return data;
    }
    
    /**
     * Remove first occurrence of element
     */
    public boolean remove(T data) {
        if (head == null) {
            return false;
        }
        
        if (head.data.equals(data)) {
            head = head.next;
            if (head == null) {
                tail = null;
            }
            size--;
            return true;
        }
        
        Node<T> current = head;
        while (current.next != null) {
            if (current.next.data.equals(data)) {
                current.next = current.next.next;
                if (current.next == null) {
                    tail = current;
                }
                size--;
                return true;
            }
            current = current.next;
        }
        
        return false;
    }
    
    /**
     * Check if list contains element
     */
    public boolean contains(T data) {
        Node<T> current = head;
        while (current != null) {
            if (current.data.equals(data)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }
    
    /**
     * Get the size of the list
     */
    public int size() {
        return size;
    }
    
    /**
     * Check if list is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Clear all elements from the list
     */
    public void clear() {
        head = null;
        tail = null;
        size = 0;
    }
    
    /**
     * Convert list to array
     */
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        if (size == 0) {
            return (T[]) new Object[0];
        }
        
        T[] array = (T[]) new Object[size];
        Node<T> current = head;
        int index = 0;
        
        while (current != null) {
            array[index++] = current.data;
            current = current.next;
        }
        
        return array;
    }
    
    @Override
    public String toString() {
        if (head == null) {
            return "[]";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        
        Node<T> current = head;
        while (current != null) {
            sb.append(current.data);
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next;
        }
        
        sb.append("]");
        return sb.toString();
    }
}
