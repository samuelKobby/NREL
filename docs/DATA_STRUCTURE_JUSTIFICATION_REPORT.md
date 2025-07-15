# NREL System - Data Structure Justification Report

## Executive Summary

This report provides detailed justification for the choice of each data structure implemented in the NREL Expenditure Management System. Each selection is analyzed based on performance requirements, use case scenarios, and algorithmic efficiency.

---

## 1. CustomHashMap<K, V>

### Primary Use Cases
- **Expenditure Storage**: Main repository for all expenditure records
- **Category Management**: Fast lookup of expenditure categories
- **Bank Account Storage**: Quick access to account information
- **User Session Management**: Efficient data retrieval during operations

### Justification Analysis

#### Performance Requirements
- **Requirement**: Sub-millisecond access to expenditure records by ID
- **Solution**: HashMap provides O(1) average-case lookup time
- **Alternative Considered**: Array/List with O(n) linear search - rejected due to poor scalability

#### Memory Efficiency
- **Space Complexity**: O(n) where n = number of key-value pairs
- **Memory Overhead**: ~32 bytes per entry (16-byte object header + 8-byte key reference + 8-byte value reference)
- **Load Factor**: Maintained at 0.75 to balance memory usage vs. performance

#### Collision Handling Strategy
```
Hash Collision Resolution: Separate Chaining
├── Advantage: Simple implementation, handles any load factor
├── Trade-off: Extra memory for linked list nodes
└── Performance: O(1) average, O(n) worst case (rare with good hash function)
```

#### Why Not Other Structures?
- **Array**: O(n) search time unacceptable for large datasets
- **Binary Search Tree**: O(log n) search slower than hash table
- **Linked List**: O(n) search time inadequate for frequent lookups

### Implementation Decision Matrix
| Criterion | HashMap Score | Array Score | BST Score | List Score |
|-----------|---------------|-------------|-----------|------------|
| Lookup Speed | 10/10 | 3/10 | 7/10 | 3/10 |
| Insert Speed | 10/10 | 5/10 | 7/10 | 9/10 |
| Memory Usage | 7/10 | 10/10 | 6/10 | 8/10 |
| Complexity | 6/10 | 10/10 | 5/10 | 9/10 |
| **Total** | **33/40** | **28/40** | **25/40** | **29/40** |

---

## 2. CustomLinkedList<T>

### Primary Use Cases
- **Search Results**: Dynamic collections of filtered expenditures
- **Category Lists**: Variable-length category collections
- **Report Generation**: Building reports with unknown result size
- **Transaction Chains**: Linked transaction processing

### Justification Analysis

#### Dynamic Size Management
- **Requirement**: Handle collections of unknown size efficiently
- **Solution**: LinkedList grows/shrinks dynamically without pre-allocation
- **Memory**: Allocates exactly what's needed, no wasted space

#### Insertion/Deletion Performance
```
Operation Performance Analysis:
├── Insert at Head: O(1) - Constant time, ideal for stack-like operations
├── Insert at Tail: O(1) - With tail pointer, efficient queue operations
├── Insert at Index: O(n) - Acceptable for infrequent indexed operations
└── Delete: O(n) - Linear search required, acceptable for small lists
```

#### Why Not Other Structures?
- **ArrayList**: Requires pre-allocation, wastes memory for small collections
- **Fixed Array**: Cannot handle variable-size collections
- **HashMap**: Unnecessary overhead when order matters

### Memory Layout Analysis
```
Node Structure (24 bytes per node):
├── Object Header: 8 bytes
├── Data Reference: 8 bytes
├── Next Pointer: 8 bytes
└── Total Overhead: 24 bytes + data size
```

---

## 3. CustomSet<T>

### Primary Use Cases
- **Category Uniqueness**: Ensure no duplicate category names
- **User Role Management**: Unique user permissions
- **Validation Lists**: Unique constraint enforcement
- **Deduplication**: Remove duplicates from search results

### Justification Analysis

#### Uniqueness Guarantee
- **Requirement**: Mathematical set properties (no duplicates)
- **Solution**: Set automatically prevents duplicate insertions
- **Implementation**: Built on CustomHashMap for O(1) contains() checks

#### Performance Characteristics
```
Set Operations Performance:
├── Add: O(1) average - HashMap-backed insertion
├── Contains: O(1) average - Direct hash lookup
├── Remove: O(1) average - HashMap deletion
└── Set Operations: O(n+m) - Union, intersection, difference
```

#### Alternative Analysis
- **Array with Manual Checking**: O(n) contains() operation too slow
- **Sorted Array**: O(log n) search better but O(n) insertion
- **Boolean Array**: Only works for small, known universe of elements

---

## 4. CustomStack<T>

### Primary Use Cases
- **Receipt Processing**: LIFO receipt validation workflow
- **Undo Operations**: Reverse recent user actions
- **Expression Evaluation**: Parse financial formulas
- **Method Call Stack**: Track nested operations

### Justification Analysis

#### LIFO Requirement
- **Business Logic**: Receipt processing follows Last-In-First-Out pattern
- **Natural Fit**: Stack operations match business workflow exactly
- **Efficiency**: O(1) push/pop operations ideal for frequent access

#### Implementation Strategy
```
Array-Based Stack Design:
├── Advantages: Cache-friendly, minimal memory overhead
├── Dynamic Resizing: Doubles capacity when full
├── Memory: Contiguous allocation improves performance
└── Trade-off: Occasional O(n) resize cost amortized to O(1)
```

#### Why Not LinkedList?
- **Memory Overhead**: Each node requires 24 bytes vs. 8 bytes array element
- **Cache Performance**: Array provides better locality of reference
- **Simplicity**: Array implementation simpler and faster

---

## 5. CustomQueue<T>

### Primary Use Cases
- **Transaction Processing**: FIFO transaction workflow
- **Batch Operations**: Process expenditures in submission order
- **Event Processing**: Handle user actions sequentially
- **Print Queue**: Manage report generation requests

### Justification Analysis

#### FIFO Requirement
- **Business Logic**: Transactions must be processed in chronological order
- **Fairness**: First-submitted expenditures processed first
- **Audit Trail**: Maintains temporal ordering for compliance

#### Circular Array Implementation
```
Circular Buffer Design:
├── Space Efficiency: Fixed-size array, no wasted space
├── Performance: O(1) enqueue/dequeue operations
├── Memory: Contiguous allocation, cache-friendly
└── Wraparound: Head/tail pointers enable circular usage
```

---

## 6. CustomTree<T>

### Primary Use Cases
- **Hierarchical Categories**: Parent-child category relationships
- **Organizational Structure**: Department/project hierarchies
- **Decision Trees**: Financial analysis workflows
- **Data Visualization**: Tree-based report structures

### Justification Analysis

#### Hierarchical Data Modeling
- **Natural Representation**: Tree structure matches real-world hierarchies
- **Navigation**: Parent-child relationships easy to traverse
- **Scalability**: Supports arbitrary depth and branching

#### N-ary Tree Design
```
Tree Node Structure:
├── Data: T - Generic data storage
├── Parent: TreeNode<T> - Upward navigation
├── Children: CustomLinkedList<TreeNode<T>> - Dynamic child list
└── Operations: O(h) where h = height, O(1) for direct child access
```

#### Alternative Considerations
- **Flat Structure with Parent IDs**: Requires expensive queries to build hierarchies
- **Adjacency Matrix**: Wastes space for sparse hierarchies
- **Binary Tree**: Insufficient for multi-child categories

---

## 7. MinHeap

### Primary Use Cases
- **Bank Account Monitoring**: Track accounts with lowest balances
- **Priority Operations**: Process high-priority expenditures first
- **Resource Allocation**: Assign limited resources optimally
- **Alert System**: Monitor financial thresholds

### Justification Analysis

#### Priority Queue Requirements
- **Requirement**: Always access minimum element efficiently
- **Solution**: MinHeap provides O(log n) insertion, O(1) minimum access
- **Use Case**: Monitor bank accounts with critically low balances

#### Binary Heap Implementation
```
Array-Based Heap Properties:
├── Parent-Child Relationship: parent(i) = (i-1)/2, children(i) = 2i+1, 2i+2
├── Complete Binary Tree: Fills levels left-to-right
├── Heap Property: parent ≤ children (min-heap)
└── Space Efficiency: No pointer overhead, cache-friendly
```

#### Performance Analysis
| Operation | Time Complexity | Justification |
|-----------|----------------|---------------|
| Insert | O(log n) | Bubble up through log n levels |
| Extract Min | O(log n) | Bubble down after removal |
| Peek Min | O(1) | Root element always minimum |
| Heapify | O(n) | Build heap from array |

---

## 8. Graph

### Primary Use Cases
- **Account Relationships**: Model connections between bank accounts
- **Transaction Flow**: Track money movement between accounts
- **Dependency Analysis**: Expenditure dependencies and prerequisites
- **Network Analysis**: Financial relationship mapping

### Justification Analysis

#### Graph Representation Choice
```
Adjacency List Implementation:
├── Vertices: CustomHashMap<String, CustomLinkedList<Edge>>
├── Edges: Weighted connections with metadata
├── Memory: O(V + E) space complexity
└── Performance: O(V + E) for most graph algorithms
```

#### Why Adjacency List vs. Matrix?
| Aspect | Adjacency List | Adjacency Matrix |
|--------|----------------|------------------|
| Space | O(V + E) | O(V²) |
| Add Vertex | O(1) | O(V²) |
| Add Edge | O(1) | O(1) |
| Find Edge | O(degree) | O(1) |
| **Best For** | **Sparse Graphs** | **Dense Graphs** |

#### Business Case Alignment
- **Financial Networks**: Typically sparse (accounts don't connect to all others)
- **Scalability**: Can handle thousands of accounts efficiently
- **Flexibility**: Easy to add new account types and relationships

---

## Data Structure Selection Summary

### Performance-Critical Selections
1. **HashMap for Expenditures**: O(1) lookup essential for user experience
2. **MinHeap for Account Monitoring**: O(1) minimum access for alerts
3. **Set for Categories**: O(1) uniqueness checking prevents errors

### Memory-Efficient Selections
1. **LinkedList for Dynamic Collections**: No pre-allocation waste
2. **Stack/Queue Arrays**: Minimal overhead for fixed-size operations
3. **Tree for Hierarchies**: Natural representation, no artificial constraints

### Business Logic Aligned Selections
1. **Stack for Receipts**: LIFO matches processing workflow
2. **Queue for Transactions**: FIFO ensures chronological processing
3. **Graph for Relationships**: Models real-world account connections

---

## Conclusion

Each data structure was selected based on rigorous analysis of:
1. **Performance Requirements**: Time/space complexity matching use case needs
2. **Business Logic Alignment**: Structure matches real-world workflows
3. **Scalability Considerations**: Handles growth from small to large datasets
4. **Implementation Simplicity**: Balance between efficiency and maintainability

The combination provides a robust foundation for the NREL system with optimal performance characteristics for each specific use case.
