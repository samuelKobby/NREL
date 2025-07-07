<!-- Use this file to provide workspace-specific custom instructions to Copilot. For more details, visit https://code.visualstudio.com/docs/copilot/copilot-customization#_use-a-githubcopilotinstructionsmd-file -->

# NREL Expenditure Management System - Copilot Instructions

This is a Java-based expenditure management system for Nkwa Real Estate Ltd built from scratch using custom data structures.

## Key Requirements:
- Build all data structures from scratch (no external libraries)
- Use: arrays, stacks, queues, linked lists, sets, maps, trees, hash maps, and graphs
- Offline-first application with file-based persistence
- Menu-driven command-line interface
- Focus on accountant workflows and construction industry needs

## Project Structure:
- `models/` - Core business entities (Expenditure, BankAccount, etc.)
- `datastructures/` - Custom implementations of data structures
- `services/` - Business logic and operations
- `cli/` - Command-line interface components
- `storage/` - File-based data persistence

## Code Style:
- Use proper Java naming conventions
- Include comprehensive JavaDoc comments
- Implement proper error handling
- Follow object-oriented principles
- Ensure thread safety where applicable

## Data Structures to Implement:
1. CustomHashMap - For expenditure storage and retrieval
2. CustomLinkedList - For category management and lists
3. CustomStack - For receipt/invoice processing queues
4. CustomQueue - For transaction processing
5. CustomSet - For unique category management
6. CustomTree - For hierarchical data organization
7. MinHeap - For bank account balance monitoring
8. Graph - For account relationship mapping
