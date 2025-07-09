package com.nkwarealestate.expenditure.datastructures;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom Tree implementation
 * Used for hierarchical data organization
 */
public class CustomTree<T> {

    private TreeNode<T> root;
    private int size;
    
    /**
     * Inner class for tree nodes
     */
    public static class TreeNode<T> {
        T value;
        TreeNode<T> parent;
        List<TreeNode<T>> children;
        
        TreeNode(T value) {
            this.value = value;
            this.parent = null;
            this.children = new ArrayList<>();
        }
        
        /**
         * Add a child node to this node
         */
        void addChild(TreeNode<T> child) {
            child.parent = this;
            this.children.add(child);
        }
        
        /**
         * Check if this node is a leaf (has no children)
         */
        boolean isLeaf() {
            return children.isEmpty();
        }
        
        /**
         * Get the number of children
         */
        int childCount() {
            return children.size();
        }
    }
    
    /**
     * Default constructor
     */
    public CustomTree() {
        this.root = null;
        this.size = 0;
    }
    
    /**
     * Constructor with root value
     */
    public CustomTree(T rootValue) {
        this.root = new TreeNode<>(rootValue);
        this.size = 1;
    }
    
    /**
     * Add a child node to a parent node identified by its value
     * 
     * @param parentValue The value of the parent node
     * @param childValue The value for the new child node
     * @return True if parent found and child added, false otherwise
     */
    public boolean addChild(T parentValue, T childValue) {
        if (root == null) {
            // If tree is empty and trying to add a child, create root first
            root = new TreeNode<>(parentValue);
            size++;
        }
        
        // Find the parent node
        TreeNode<T> parentNode = findNode(root, parentValue);
        
        if (parentNode != null) {
            // Add child to the parent
            TreeNode<T> childNode = new TreeNode<>(childValue);
            parentNode.addChild(childNode);
            size++;
            return true;
        }
        
        return false;
    }
    
    /**
     * Find a node by value using DFS
     */
    private TreeNode<T> findNode(TreeNode<T> current, T value) {
        if (current == null) {
            return null;
        }
        
        if (current.value.equals(value)) {
            return current;
        }
        
        // Search in children
        for (TreeNode<T> child : current.children) {
            TreeNode<T> found = findNode(child, value);
            if (found != null) {
                return found;
            }
        }
        
        return null;
    }
    
    /**
     * Get the size of the tree (number of nodes)
     */
    public int size() {
        return size;
    }
    
    /**
     * Check if the tree is empty
     */
    public boolean isEmpty() {
        return root == null;
    }
    
    /**
     * Check if a value exists in the tree
     */
    public boolean contains(T value) {
        return findNode(root, value) != null;
    }
    
    /**
     * Get the depth (level) of a node with the given value
     * Root is at depth 0
     */
    public int getDepth(T value) {
        TreeNode<T> node = findNode(root, value);
        
        if (node == null) {
            return -1; // Node not found
        }
        
        int depth = 0;
        TreeNode<T> current = node;
        
        while (current.parent != null) {
            depth++;
            current = current.parent;
        }
        
        return depth;
    }
    
    /**
     * Get the height of the tree (longest path from root to a leaf)
     */
    public int getHeight() {
        if (root == null) {
            return 0;
        }
        
        return getNodeHeight(root);
    }
    
    /**
     * Get the height of a subtree rooted at the given node
     */
    private int getNodeHeight(TreeNode<T> node) {
        if (node.isLeaf()) {
            return 0;
        }
        
        int maxChildHeight = -1;
        
        for (TreeNode<T> child : node.children) {
            int childHeight = getNodeHeight(child);
            if (childHeight > maxChildHeight) {
                maxChildHeight = childHeight;
            }
        }
        
        return maxChildHeight + 1;
    }
    
    /**
     * Print the tree structure in a hierarchical format
     */
    public void printTree() {
        if (root == null) {
            System.out.println("Empty tree");
            return;
        }
        
        printNodeHierarchy(root, 0);
    }
    
    /**
     * Helper method to print node hierarchy with indentation
     */
    private void printNodeHierarchy(TreeNode<T> node, int depth) {
        if (node == null) {
            return;
        }
        
        // Print indentation based on depth
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            indent.append("  ");
        }
        
        // Print current node with appropriate branch symbol
        if (depth > 0) {
            System.out.println(indent.toString() + "└─ " + node.value);
        } else {
            System.out.println(node.value); // Root node
        }
        
        // Print children with increased indentation
        for (TreeNode<T> child : node.children) {
            printNodeHierarchy(child, depth + 1);
        }
    }
    
    /**
     * Clear the tree
     */
    public void clear() {
        root = null;
        size = 0;
    }
}
