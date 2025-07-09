package com.nkwarealestate.expenditure.datastructures;

/**
 * Custom MinHeap implementation using arrays
 * Specialized for bank account balance monitoring
 * Uses account balances as keys and account IDs as values
 */
public class MinHeap {

    private static final int DEFAULT_CAPACITY = 10;
    private int[] balances; // Keys (balances)
    private String[] accountIds; // Values (account IDs)
    private int size;
    private int capacity;

    /**
     * Constructor with default capacity
     */
    public MinHeap() {
        this.capacity = DEFAULT_CAPACITY;
        this.balances = new int[capacity];
        this.accountIds = new String[capacity];
        this.size = 0;
    }

    /**
     * Constructor with specified capacity
     */
    public MinHeap(int capacity) {
        this.capacity = capacity;
        this.balances = new int[capacity];
        this.accountIds = new String[capacity];
        this.size = 0;
    }

    /**
     * Insert a bank account into the heap based on balance
     */
    public void insert(int balance, String accountId) {
        if (accountId == null) {
            throw new IllegalArgumentException("Account ID cannot be null");
        }

        if (size >= capacity) {
            resize();
        }

        balances[size] = balance;
        accountIds[size] = accountId;
        heapifyUp(size);
        size++;
    }

    /**
     * Extract the account with minimum balance from the heap
     */
    public String extractMin() {
        if (isEmpty()) {
            throw new RuntimeException("Heap is empty");
        }

        String minAccountId = accountIds[0];

        // Move the last element to the root
        balances[0] = balances[size - 1];
        accountIds[0] = accountIds[size - 1];

        // Clear the last element
        accountIds[size - 1] = null;
        size--;

        if (size > 0) {
            heapifyDown(0);
        }

        return minAccountId;
    }

    /**
     * Peek at the account with minimum balance without removing it
     */
    public String peekAccount() {
        if (isEmpty()) {
            throw new RuntimeException("Heap is empty");
        }
        return accountIds[0];
    }

    /**
     * Get the minimum balance
     */
    public int peekBalance() {
        if (isEmpty()) {
            throw new RuntimeException("Heap is empty");
        }
        return balances[0];
    }

    /**
     * Get accounts with balances below the threshold
     */
    public String[] getAccountsBelowThreshold(int threshold) {
        // Count accounts below threshold
        int count = 0;
        for (int i = 0; i < size; i++) {
            if (balances[i] < threshold) {
                count++;
            }
        }

        // Create result array
        String[] result = new String[count];
        int index = 0;

        for (int i = 0; i < size; i++) {
            if (balances[i] < threshold) {
                result[index++] = accountIds[i];
            }
        }

        return result;
    }

    /**
     * Update the balance for a specific account
     */
    public void updateBalance(String accountId, int newBalance) {
        for (int i = 0; i < size; i++) {
            if (accountIds[i] != null && accountIds[i].equals(accountId)) {
                int oldBalance = balances[i];
                balances[i] = newBalance;

                if (newBalance < oldBalance) {
                    heapifyUp(i);
                } else {
                    heapifyDown(i);
                }
                return;
            }
        }

        // Account not found, add it
        insert(newBalance, accountId);
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
        int parentIndex = (index - 1) / 2;

        if (index > 0 && balances[index] < balances[parentIndex]) {
            swap(index, parentIndex);
            heapifyUp(parentIndex);
        }
    }

    /**
     * Heapify down operation to maintain heap property after extraction
     */
    private void heapifyDown(int index) {
        int leftChild = 2 * index + 1;
        int rightChild = 2 * index + 2;
        int smallest = index;

        if (leftChild < size && balances[leftChild] < balances[smallest]) {
            smallest = leftChild;
        }

        if (rightChild < size && balances[rightChild] < balances[smallest]) {
            smallest = rightChild;
        }

        if (smallest != index) {
            swap(index, smallest);
            heapifyDown(smallest);
        }
    }

    /**
     * Swap elements at two indices
     */
    private void swap(int i, int j) {
        // Swap balances
        int tempBalance = balances[i];
        balances[i] = balances[j];
        balances[j] = tempBalance;

        // Swap account IDs
        String tempId = accountIds[i];
        accountIds[i] = accountIds[j];
        accountIds[j] = tempId;
    }

    /**
     * Resize the heap when it becomes full
     */
    private void resize() {
        int newCapacity = capacity * 2;

        int[] newBalances = new int[newCapacity];
        String[] newAccountIds = new String[newCapacity];

        // Copy elements
        System.arraycopy(balances, 0, newBalances, 0, size);
        System.arraycopy(accountIds, 0, newAccountIds, 0, size);

        balances = newBalances;
        accountIds = newAccountIds;
        capacity = newCapacity;
    }

    /**
     * Clear all elements from the heap
     */
    public void clear() {
        for (int i = 0; i < size; i++) {
            accountIds[i] = null;
        }
        size = 0;
    }

    /**
     * Get all account IDs in the heap (not in sorted order)
     */
    public String[] getAccountIds() {
        String[] result = new String[size];
        for (int i = 0; i < size; i++) {
            result[i] = accountIds[i];
        }
        return result;
    }

    /**
     * Get all balances in the heap (not in sorted order)
     */
    public int[] getBalances() {
        int[] result = new int[size];
        System.arraycopy(balances, 0, result, 0, size);
        return result;
    }

    /**
     * Build a heap from existing account data
     */
    public static MinHeap buildHeap(int[] balances, String[] accountIds) {
        if (balances.length != accountIds.length) {
            throw new IllegalArgumentException("Balance and account ID arrays must be the same length");
        }

        MinHeap heap = new MinHeap(balances.length * 2);

        for (int i = 0; i < balances.length; i++) {
            if (accountIds[i] != null) {
                heap.insert(balances[i], accountIds[i]);
            }
        }

        return heap;
    }

    /**
     * Print the heap for debugging
     */
    public void printHeap() {
        System.out.println("\nHeap Contents (Balance : AccountID):");
        for (int i = 0; i < size; i++) {
            System.out.println(balances[i] + " : " + accountIds[i]);
        }
        System.out.println();
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(balances[i]).append(":").append(accountIds[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
