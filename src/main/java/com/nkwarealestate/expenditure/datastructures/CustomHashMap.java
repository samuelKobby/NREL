package com.nkwarealestate.expenditure.datastructures;

/**
 * Custom HashMap implementation using arrays and linked lists for collision handling
 * This implementation is built from scratch to meet the project requirements
 */
public class CustomHashMap<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;
    
    private Node<K, V>[] buckets;
    private int size;
    private int capacity;
    
    /**index[2]
     * Node class for storing key-value pairs in linked list
     */
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> next;
        
        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public CustomHashMap() {
        this.capacity = DEFAULT_CAPACITY;
        this.buckets = new Node[capacity];
        this.size = 0;
    }
    
    @SuppressWarnings("unchecked")
    public CustomHashMap(int initialCapacity) {
        this.capacity = initialCapacity;
        this.buckets = new Node[capacity];
        this.size = 0;
    }
    
    /**
     * Hash function to determine bucket index
     */
    private int hash(K key) {
        if (key == null) {
            return 0;
        }
        return Math.abs(key.hashCode()) % capacity;
    }
    
    /**
     * Put a key-value pair into the map
     */
    public V put(K key, V value) {
        if (size >= capacity * LOAD_FACTOR) {
            resize();
        }
        
        int index = hash(key);
        Node<K, V> head = buckets[index];
        
        // If bucket is empty
        if (head == null) {
            buckets[index] = new Node<>(key, value);
            size++;
            return null;
        }
        
        // Search for existing key or find end of chain
        Node<K, V> current = head;
        while (current != null) {
            if (current.key != null && current.key.equals(key)) {
                // Key exists, update value
                V oldValue = current.value;
                current.value = value;
                return oldValue;
            }
            if (current.next == null) {
                break;
            }
            current = current.next;
        }
        
        // Add new node at end of chain
        current.next = new Node<>(key, value);
        size++;
        return null;
    }
    
    /**
     * Get value by key
     */
    public V get(K key) {
        int index = hash(key);
        Node<K, V> current = buckets[index];
        
        while (current != null) {
            if (current.key != null && current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
        
        return null; // Key not found
    }
    
    /**
     * Remove a key-value pair
     */
    public V remove(K key) {
        int index = hash(key);
        Node<K, V> head = buckets[index];
        
        if (head == null) {
            return null;
        }
        
        // If head node is the one to remove
        if (head.key != null && head.key.equals(key)) {
            buckets[index] = head.next;
            size--;
            return head.value;
        }
        
        // Search in the chain
        Node<K, V> current = head;
        while (current.next != null) {
            if (current.next.key != null && current.next.key.equals(key)) {
                V value = current.next.value;
                current.next = current.next.next;
                size--;
                return value;
            }
            current = current.next;
        }
        
        return null; // Key not found
    }
    
    /**
     * Check if key exists in the map
     */
    public boolean containsKey(K key) {
        return get(key) != null;
    }
    
    /**
     * Get the size of the map
     */
    public int size() {
        return size;
    }
    
    /**
     * Check if map is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * Resize the hash table when load factor is exceeded
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        Node<K, V>[] oldBuckets = buckets;
        capacity *= 2;
        buckets = new Node[capacity];
        size = 0;
        
        // Rehash all existing entries
        for (Node<K, V> head : oldBuckets) {
            Node<K, V> current = head;
            while (current != null) {
                put(current.key, current.value);
                current = current.next;
            }
        }
    }
    
    /**
     * Get all keys in the map
     */
    public CustomLinkedList<K> keySet() {
        CustomLinkedList<K> keys = new CustomLinkedList<>();
        
        for (Node<K, V> head : buckets) {
            Node<K, V> current = head;
            while (current != null) {
                keys.add(current.key);
                current = current.next;
            }
        }
        
        return keys;
    }
    
    /**
     * Clear all entries from the map
     */
    @SuppressWarnings("unchecked")
    public void clear() {
        buckets = new Node[capacity];
        size = 0;
    }
}
