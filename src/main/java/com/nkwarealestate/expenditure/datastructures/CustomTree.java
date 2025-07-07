package com.nkwarealestate.expenditure.datastructures;

/**
 * Custom Binary Search Tree implementation
 * Used for hierarchical data organization and efficient searching/sorting
 */
public class CustomTree<T extends Comparable<T>> {

    private TreeNode<T> root;
    private int size;

    /**
     * TreeNode class for BST implementation
     */
    private static class TreeNode<T> {
        T data;
        TreeNode<T> left;
        TreeNode<T> right;

        TreeNode(T data) {
            this.data = data;
            this.left = null;
            this.right = null;
        }
    }

    /**
     * Constructor
     */
    public CustomTree() {
        this.root = null;
        this.size = 0;
    }

    /**
     * Insert an element into the tree
     */
    public void insert(T data) {
        if (data == null) {
            throw new IllegalArgumentException("Cannot insert null data");
        }

        root = insertRecursive(root, data);
    }

    /**
     * Recursive helper method for insertion
     */
    private TreeNode<T> insertRecursive(TreeNode<T> node, T data) {
        if (node == null) {
            size++;
            return new TreeNode<>(data);
        }

        int comparison = data.compareTo(node.data);

        if (comparison < 0) {
            node.left = insertRecursive(node.left, data);
        } else if (comparison > 0) {
            node.right = insertRecursive(node.right, data);
        }
        // If comparison == 0, we don't insert duplicates

        return node;
    }

    /**
     * Search for an element in the tree
     */
    public boolean search(T data) {
        if (data == null) {
            return false;
        }
        return searchRecursive(root, data);
    }

    /**
     * Recursive helper method for searching
     */
    private boolean searchRecursive(TreeNode<T> node, T data) {
        if (node == null) {
            return false;
        }

        int comparison = data.compareTo(node.data);

        if (comparison == 0) {
            return true;
        } else if (comparison < 0) {
            return searchRecursive(node.left, data);
        } else {
            return searchRecursive(node.right, data);
        }
    }

    /**
     * Delete an element from the tree
     */
    public boolean delete(T data) {
        if (data == null || !search(data)) {
            return false;
        }

        root = deleteRecursive(root, data);
        size--;
        return true;
    }

    /**
     * Recursive helper method for deletion
     */
    private TreeNode<T> deleteRecursive(TreeNode<T> node, T data) {
        if (node == null) {
            return null;
        }

        int comparison = data.compareTo(node.data);

        if (comparison < 0) {
            node.left = deleteRecursive(node.left, data);
        } else if (comparison > 0) {
            node.right = deleteRecursive(node.right, data);
        } else {
            // Node to be deleted found

            // Case 1: No children (leaf node)
            if (node.left == null && node.right == null) {
                return null;
            }

            // Case 2: One child
            if (node.left == null) {
                return node.right;
            }
            if (node.right == null) {
                return node.left;
            }

            // Case 3: Two children
            // Find inorder successor (smallest in right subtree)
            TreeNode<T> successor = findMin(node.right);
            node.data = successor.data;
            node.right = deleteRecursive(node.right, successor.data);
        }

        return node;
    }

    /**
     * Find minimum element in subtree
     */
    private TreeNode<T> findMin(TreeNode<T> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    /**
     * Find maximum element in subtree
     */
    private TreeNode<T> findMax(TreeNode<T> node) {
        while (node.right != null) {
            node = node.right;
        }
        return node;
    }

    /**
     * Get minimum element in the tree
     */
    public T getMin() {
        if (root == null) {
            throw new RuntimeException("Tree is empty");
        }
        return findMin(root).data;
    }

    /**
     * Get maximum element in the tree
     */
    public T getMax() {
        if (root == null) {
            throw new RuntimeException("Tree is empty");
        }
        return findMax(root).data;
    }

    /**
     * Get size of the tree
     */
    public int size() {
        return size;
    }

    /**
     * Check if tree is empty
     */
    public boolean isEmpty() {
        return root == null;
    }

    /**
     * Get height of the tree
     */
    public int height() {
        return heightRecursive(root);
    }

    /**
     * Recursive helper method for height calculation
     */
    private int heightRecursive(TreeNode<T> node) {
        if (node == null) {
            return -1;
        }
        return 1 + Math.max(heightRecursive(node.left), heightRecursive(node.right));
    }

    /**
     * Clear all elements from the tree
     */
    public void clear() {
        root = null;
        size = 0;
    }

    /**
     * Get inorder traversal as array
     */
    public CustomLinkedList<T> inOrderTraversal() {
        CustomLinkedList<T> result = new CustomLinkedList<>();
        inOrderRecursive(root, result);
        return result;
    }

    /**
     * Recursive helper for inorder traversal
     */
    private void inOrderRecursive(TreeNode<T> node, CustomLinkedList<T> result) {
        if (node != null) {
            inOrderRecursive(node.left, result);
            result.add(node.data);
            inOrderRecursive(node.right, result);
        }
    }

    /**
     * Get preorder traversal as array
     */
    public CustomLinkedList<T> preOrderTraversal() {
        CustomLinkedList<T> result = new CustomLinkedList<>();
        preOrderRecursive(root, result);
        return result;
    }

    /**
     * Recursive helper for preorder traversal
     */
    private void preOrderRecursive(TreeNode<T> node, CustomLinkedList<T> result) {
        if (node != null) {
            result.add(node.data);
            preOrderRecursive(node.left, result);
            preOrderRecursive(node.right, result);
        }
    }

    /**
     * Get postorder traversal as array
     */
    public CustomLinkedList<T> postOrderTraversal() {
        CustomLinkedList<T> result = new CustomLinkedList<>();
        postOrderRecursive(root, result);
        return result;
    }

    /**
     * Recursive helper for postorder traversal
     */
    private void postOrderRecursive(TreeNode<T> node, CustomLinkedList<T> result) {
        if (node != null) {
            postOrderRecursive(node.left, result);
            postOrderRecursive(node.right, result);
            result.add(node.data);
        }
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "[]";
        }

        CustomLinkedList<T> inOrder = inOrderTraversal();
        StringBuilder sb = new StringBuilder("[");

        for (int i = 0; i < inOrder.size(); i++) {
            sb.append(inOrder.get(i));
            if (i < inOrder.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append("]");
        return sb.toString();
    }
}
