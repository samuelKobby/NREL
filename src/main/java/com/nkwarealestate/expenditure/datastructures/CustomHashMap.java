package com.nkwarealestate.expenditure.datastructures;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Custom HashMap implementation using arrays and linked lists for collision
 * handling
 * This implementation is built from scratch to meet the project requirements
 * Supports iteration over entries, keys, and values
 */
public class CustomHashMap<K, V> {

    private static final int DEFAULT_CAPACITY = 16;
    private static final double LOAD_FACTOR = 0.75;

    private Node<K, V>[] buckets;
    private int size;
    private int capacity;

    /**
     * index[2]
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

    /**
     * Entry class for exposing key-value pairs in the map
     */
    public static class Entry<K, V> {
        private final K key;
        private V value;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return key + "=" + value;
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
        for (Entry<K, V> entry : entries()) {
            keys.add(entry.getKey());
        }
        return keys;
    }

    /**
     * Get all keys as an iterable collection
     */
    public Iterable<K> keys() {
        return () -> new KeyIterator();
    }

    /**
     * Clear all entries from the map
     */
    @SuppressWarnings("unchecked")
    public void clear() {
        buckets = new Node[capacity];
        size = 0;
    }

    /**
     * Get all entries in the map as an iterable collection
     */
    public Iterable<Entry<K, V>> entries() {
        return () -> new EntryIterator();
    }

    /**
     * Get all values in the map as an iterable collection
     */
    public Iterable<V> values() {
        return () -> new ValueIterator();
    }

    /**
     * Custom iterator for entries
     */
    private class EntryIterator implements Iterator<Entry<K, V>> {
        private int bucketIndex;
        private Node<K, V> current;
        private Node<K, V> next;
        private int entryCount;

        public EntryIterator() {
            bucketIndex = 0;
            current = null;
            next = null;
            entryCount = 0;
            advanceToNextEntry();
        }

        private void advanceToNextEntry() {
            if (next != null) {
                // Move to next node in the current bucket
                next = next.next;
            }

            // If we need to move to a new bucket
            while (next == null && bucketIndex < capacity) {
                next = buckets[bucketIndex++];
            }
        }

        @Override
        public boolean hasNext() {
            return next != null && entryCount < size;
        }

        @Override
        public Entry<K, V> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            current = next;
            entryCount++;
            advanceToNextEntry();
            return new Entry<>(current.key, current.value);
        }
    }

    /**
     * Custom iterator for values
     */
    private class ValueIterator implements Iterator<V> {
        private EntryIterator entryIterator;

        public ValueIterator() {
            this.entryIterator = new EntryIterator();
        }

        @Override
        public boolean hasNext() {
            return entryIterator.hasNext();
        }

        @Override
        public V next() {
            return entryIterator.next().getValue();
        }
    }

    /**
     * Custom iterator for keys
     */
    private class KeyIterator implements Iterator<K> {
        private EntryIterator entryIterator;

        public KeyIterator() {
            this.entryIterator = new EntryIterator();
        }

        @Override
        public boolean hasNext() {
            return entryIterator.hasNext();
        }

        @Override
        public K next() {
            return entryIterator.next().getKey();
        }
    }

    /**
     * Get all entries as a CustomLinkedList
     */
    public CustomLinkedList<Entry<K, V>> entryList() {
        CustomLinkedList<Entry<K, V>> entries = new CustomLinkedList<>();
        for (Entry<K, V> entry : entries()) {
            entries.add(entry);
        }
        return entries;
    }

    /**
     * Get all values as a CustomLinkedList
     */
    public CustomLinkedList<V> valueList() {
        CustomLinkedList<V> values = new CustomLinkedList<>();
        for (V value : values()) {
            values.add(value);
        }
        return values;
    }

    /**
     * Returns a string representation of the HashMap
     */
    @Override
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;

        for (Entry<K, V> entry : entries()) {
            if (!first) {
                sb.append(", ");
            }
            first = false;
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }

        sb.append("}");
        return sb.toString();
    }
}
