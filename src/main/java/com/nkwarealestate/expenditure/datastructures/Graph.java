package com.nkwarealestate.expenditure.datastructures;

/**
 * Custom Graph implementation using adjacency list representation
 * Used for account relationship mapping and financial flow analysis
 */
public class Graph<T> {

    private CustomHashMap<T, CustomLinkedList<Edge<T>>> adjacencyList;
    private boolean isDirected;
    private int vertexCount;
    private int edgeCount;

    /**
     * Edge class to represent weighted edges
     */
    public static class Edge<T> {
        T destination;
        double weight;

        public Edge(T destination, double weight) {
            this.destination = destination;
            this.weight = weight;
        }

        public Edge(T destination) {
            this.destination = destination;
            this.weight = 1.0; // Default weight
        }

        public T getDestination() {
            return destination;
        }

        public double getWeight() {
            return weight;
        }

        @Override
        public String toString() {
            return destination + "(" + weight + ")";
        }
    }

    /**
     * Constructor for directed/undirected graph
     */
    public Graph(boolean isDirected) {
        this.adjacencyList = new CustomHashMap<>();
        this.isDirected = isDirected;
        this.vertexCount = 0;
        this.edgeCount = 0;
    }

    /**
     * Constructor for undirected graph (default)
     */
    public Graph() {
        this(false);
    }

    /**
     * Add a vertex to the graph
     */
    public void addVertex(T vertex) {
        if (vertex == null) {
            throw new IllegalArgumentException("Vertex cannot be null");
        }

        if (!adjacencyList.containsKey(vertex)) {
            adjacencyList.put(vertex, new CustomLinkedList<>());
            vertexCount++;
        }
    }

    /**
     * Add an edge between two vertices with weight
     */
    public void addEdge(T source, T destination, double weight) {
        if (source == null || destination == null) {
            throw new IllegalArgumentException("Vertices cannot be null");
        }

        // Add vertices if they don't exist
        addVertex(source);
        addVertex(destination);

        // Add edge from source to destination
        adjacencyList.get(source).add(new Edge<>(destination, weight));

        // If undirected, add edge from destination to source
        if (!isDirected) {
            adjacencyList.get(destination).add(new Edge<>(source, weight));
        }

        edgeCount++;
    }

    /**
     * Add an unweighted edge (default weight = 1.0)
     */
    public void addEdge(T source, T destination) {
        addEdge(source, destination, 1.0);
    }

    /**
     * Remove a vertex and all its edges
     */
    public boolean removeVertex(T vertex) {
        if (vertex == null || !adjacencyList.containsKey(vertex)) {
            return false;
        }

        // Count edges to be removed
        int edgesRemoved = adjacencyList.get(vertex).size();

        // Remove all edges pointing to this vertex
        CustomLinkedList<T> vertices = adjacencyList.keySet();
        for (int i = 0; i < vertices.size(); i++) {
            T v = vertices.get(i);
            if (!v.equals(vertex)) {
                CustomLinkedList<Edge<T>> edges = adjacencyList.get(v);
                for (int j = edges.size() - 1; j >= 0; j--) {
                    if (edges.get(j).getDestination().equals(vertex)) {
                        edges.remove(j);
                        if (isDirected) {
                            edgesRemoved++;
                        }
                    }
                }
            }
        }

        // Remove the vertex itself
        adjacencyList.remove(vertex);
        vertexCount--;
        edgeCount -= edgesRemoved;

        return true;
    }

    /**
     * Remove an edge between two vertices
     */
    public boolean removeEdge(T source, T destination) {
        if (source == null || destination == null ||
                !adjacencyList.containsKey(source) ||
                !adjacencyList.containsKey(destination)) {
            return false;
        }

        boolean removed = false;

        // Remove edge from source to destination
        CustomLinkedList<Edge<T>> sourceEdges = adjacencyList.get(source);
        for (int i = sourceEdges.size() - 1; i >= 0; i--) {
            if (sourceEdges.get(i).getDestination().equals(destination)) {
                sourceEdges.remove(i);
                removed = true;
                break;
            }
        }

        // If undirected, remove edge from destination to source
        if (!isDirected && removed) {
            CustomLinkedList<Edge<T>> destEdges = adjacencyList.get(destination);
            for (int i = destEdges.size() - 1; i >= 0; i--) {
                if (destEdges.get(i).getDestination().equals(source)) {
                    destEdges.remove(i);
                    break;
                }
            }
        }

        if (removed) {
            edgeCount--;
        }

        return removed;
    }

    /**
     * Check if there's an edge between two vertices
     */
    public boolean hasEdge(T source, T destination) {
        if (source == null || destination == null || !adjacencyList.containsKey(source)) {
            return false;
        }

        CustomLinkedList<Edge<T>> edges = adjacencyList.get(source);
        for (int i = 0; i < edges.size(); i++) {
            if (edges.get(i).getDestination().equals(destination)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get weight of edge between two vertices
     */
    public double getEdgeWeight(T source, T destination) {
        if (!hasEdge(source, destination)) {
            throw new RuntimeException("Edge does not exist");
        }

        CustomLinkedList<Edge<T>> edges = adjacencyList.get(source);
        for (int i = 0; i < edges.size(); i++) {
            Edge<T> edge = edges.get(i);
            if (edge.getDestination().equals(destination)) {
                return edge.getWeight();
            }
        }

        throw new RuntimeException("Edge not found"); // Should never reach here
    }

    /**
     * Get all neighbors of a vertex
     */
    public CustomLinkedList<T> getNeighbors(T vertex) {
        if (vertex == null || !adjacencyList.containsKey(vertex)) {
            return new CustomLinkedList<>();
        }

        CustomLinkedList<T> neighbors = new CustomLinkedList<>();
        CustomLinkedList<Edge<T>> edges = adjacencyList.get(vertex);

        for (int i = 0; i < edges.size(); i++) {
            neighbors.add(edges.get(i).getDestination());
        }

        return neighbors;
    }

    /**
     * Get all vertices in the graph
     */
    public CustomLinkedList<T> getAllVertices() {
        return adjacencyList.keySet();
    }

    /**
     * Get vertex count
     */
    public int getVertexCount() {
        return vertexCount;
    }

    /**
     * Get edge count
     */
    public int getEdgeCount() {
        return edgeCount;
    }

    /**
     * Check if graph is directed
     */
    public boolean isDirected() {
        return isDirected;
    }

    /**
     * Check if graph is empty
     */
    public boolean isEmpty() {
        return vertexCount == 0;
    }

    /**
     * Clear all vertices and edges
     */
    public void clear() {
        adjacencyList.clear();
        vertexCount = 0;
        edgeCount = 0;
    }

    /**
     * Depth-First Search traversal starting from a vertex
     */
    public CustomLinkedList<T> dfs(T startVertex) {
        if (startVertex == null || !adjacencyList.containsKey(startVertex)) {
            return new CustomLinkedList<>();
        }

        CustomLinkedList<T> result = new CustomLinkedList<>();
        CustomSet<T> visited = new CustomSet<>();
        dfsRecursive(startVertex, visited, result);

        return result;
    }

    /**
     * Recursive helper for DFS
     */
    private void dfsRecursive(T vertex, CustomSet<T> visited, CustomLinkedList<T> result) {
        visited.add(vertex);
        result.add(vertex);

        CustomLinkedList<Edge<T>> edges = adjacencyList.get(vertex);
        for (int i = 0; i < edges.size(); i++) {
            T neighbor = edges.get(i).getDestination();
            if (!visited.contains(neighbor)) {
                dfsRecursive(neighbor, visited, result);
            }
        }
    }

    /**
     * Breadth-First Search traversal starting from a vertex
     */
    public CustomLinkedList<T> bfs(T startVertex) {
        if (startVertex == null || !adjacencyList.containsKey(startVertex)) {
            return new CustomLinkedList<>();
        }

        CustomLinkedList<T> result = new CustomLinkedList<>();
        CustomSet<T> visited = new CustomSet<>();
        CustomQueue<T> queue = new CustomQueue<>();

        visited.add(startVertex);
        queue.enqueue(startVertex);

        while (!queue.isEmpty()) {
            T current = queue.dequeue();
            result.add(current);

            CustomLinkedList<Edge<T>> edges = adjacencyList.get(current);
            for (int i = 0; i < edges.size(); i++) {
                T neighbor = edges.get(i).getDestination();
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.enqueue(neighbor);
                }
            }
        }

        return result;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "Graph: Empty";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("Graph (").append(isDirected ? "Directed" : "Undirected").append("):\n");
        sb.append("Vertices: ").append(vertexCount).append(", Edges: ").append(edgeCount).append("\n");

        CustomLinkedList<T> vertices = getAllVertices();
        for (int i = 0; i < vertices.size(); i++) {
            T vertex = vertices.get(i);
            sb.append(vertex).append(" -> ");

            CustomLinkedList<Edge<T>> edges = adjacencyList.get(vertex);
            if (edges.size() == 0) {
                sb.append("[]");
            } else {
                sb.append("[");
                for (int j = 0; j < edges.size(); j++) {
                    sb.append(edges.get(j));
                    if (j < edges.size() - 1) {
                        sb.append(", ");
                    }
                }
                sb.append("]");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
