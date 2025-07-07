package com.nkwarealestate.expenditure.datastructures;

/**
 * Custom Set implementation using HashMap for O(1) operations
 * Used for unique category management and ensuring no duplicates
 */
public class CustomSet<T> {

    private CustomHashMap<T, Boolean> map;

    /**
     * Constructor
     */
    public CustomSet() {
        this.map = new CustomHashMap<>();
    }

    /**
     * Add an element to the set
     * 
     * @param element The element to add
     * @return true if element was added (wasn't already present), false otherwise
     */
    public boolean add(T element) {
        if (element == null) {
            throw new IllegalArgumentException("Set does not allow null elements");
        }

        boolean wasPresent = map.containsKey(element);
        map.put(element, true);
        return !wasPresent;
    }

    /**
     * Remove an element from the set
     * 
     * @param element The element to remove
     * @return true if element was removed (was present), false otherwise
     */
    public boolean remove(T element) {
        if (element == null) {
            return false;
        }

        boolean wasPresent = map.containsKey(element);
        map.remove(element);
        return wasPresent;
    }

    /**
     * Check if set contains an element
     * 
     * @param element The element to check
     * @return true if element is in the set, false otherwise
     */
    public boolean contains(T element) {
        if (element == null) {
            return false;
        }
        return map.containsKey(element);
    }

    /**
     * Get the size of the set
     * 
     * @return Number of elements in the set
     */
    public int size() {
        return map.size();
    }

    /**
     * Check if set is empty
     * 
     * @return true if set is empty, false otherwise
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Clear all elements from the set
     */
    public void clear() {
        map.clear();
    }

    /**
     * Get all elements as an array
     * 
     * @return Array containing all elements in the set
     */
    @SuppressWarnings("unchecked")
    public T[] toArray() {
        CustomLinkedList<T> keysList = map.keySet();
        T[] keys = (T[]) new Object[size()];

        for (int i = 0; i < keysList.size(); i++) {
            keys[i] = keysList.get(i);
        }

        return keys;
    }

    /**
     * Create union of this set with another set
     * 
     * @param other The other set
     * @return New set containing elements from both sets
     */
    public CustomSet<T> union(CustomSet<T> other) {
        CustomSet<T> result = new CustomSet<>();

        // Add all elements from this set
        for (T element : this.toArray()) {
            result.add(element);
        }

        // Add all elements from other set
        for (T element : other.toArray()) {
            result.add(element);
        }

        return result;
    }

    /**
     * Create intersection of this set with another set
     * 
     * @param other The other set
     * @return New set containing elements present in both sets
     */
    public CustomSet<T> intersection(CustomSet<T> other) {
        CustomSet<T> result = new CustomSet<>();

        // Add elements that are in both sets
        for (T element : this.toArray()) {
            if (other.contains(element)) {
                result.add(element);
            }
        }

        return result;
    }

    /**
     * Create difference of this set with another set
     * 
     * @param other The other set
     * @return New set containing elements in this set but not in other
     */
    public CustomSet<T> difference(CustomSet<T> other) {
        CustomSet<T> result = new CustomSet<>();

        // Add elements that are in this set but not in other
        for (T element : this.toArray()) {
            if (!other.contains(element)) {
                result.add(element);
            }
        }

        return result;
    }

    /**
     * Check if this set is a subset of another set
     * 
     * @param other The other set
     * @return true if all elements in this set are also in other set
     */
    public boolean isSubsetOf(CustomSet<T> other) {
        for (T element : this.toArray()) {
            if (!other.contains(element)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }

        StringBuilder sb = new StringBuilder("{");
        T[] elements = toArray();
        for (int i = 0; i < elements.length; i++) {
            sb.append(elements[i]);
            if (i < elements.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
