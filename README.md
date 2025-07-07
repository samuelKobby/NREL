# NREL Expenditure Management System

## Overview

The NREL Expenditure Management System is an offline-first, locally stored application designed specifically for **Nkwa Real Estate Ltd**, a growing construction firm focused on delivering low-cost housing solutions across peri-urban communities in Ghana.

This system addresses the challenge of tracking multiple bank accounts, fluctuating supplier costs, and complex construction-phase spending through a menu-driven command-line interface built entirely with custom data structures.

## Features

### Core Functionality
- **Expenditure Records**: Track expenditures with code, amount, date, phase, category, and account information
- **Category Management**: Dynamic expenditure categories with uniqueness enforcement
- **Bank Account Ledger**: Multi-account management with balance tracking and relationship mapping
- **Search & Sort**: Advanced filtering by date range, category, cost, and account
- **Receipt/Invoice Handling**: Document management with validation queues
- **Financial Analysis**: Burn rate tracking, profitability forecasting, and cost analysis

### Technical Features
- **Offline-First**: No internet connection required
- **Custom Data Structures**: Built from scratch using fundamental CS concepts
- **File-Based Persistence**: Local storage using text files
- **Menu-Driven Interface**: User-friendly command-line navigation

## Data Structures Implemented

| Structure | Purpose | Implementation |
|-----------|---------|----------------|
| **CustomHashMap** | Expenditure storage & retrieval | Array + Linked List collision handling |
| **CustomLinkedList** | Dynamic lists & categories | Doubly-linked with head/tail pointers |
| **CustomStack** | Receipt processing queue | Array-based LIFO structure |
| **CustomQueue** | Transaction processing | Circular array implementation |
| **CustomSet** | Unique category management | Hash-based uniqueness |
| **CustomTree** | Hierarchical organization | Binary search tree |
| **MinHeap** | Low balance monitoring | Array-based priority queue |
| **Graph** | Account relationships | Adjacency list representation |

## Project Structure

```
src/main/java/com/nkwarealestate/expenditure/
├── Main.java                          # Application entry point
├── cli/                               # Command-line interface
│   ├── MenuSystem.java               # Main menu navigation
│   └── CommandProcessor.java         # Command handling
├── models/                           # Business entities
│   ├── Expenditure.java             # Expenditure record
│   ├── BankAccount.java             # Bank account entity
│   ├── Category.java                # Expenditure category
│   ├── Receipt.java                 # Receipt/invoice record
│   └── Phase.java                   # Project phase enum
├── datastructures/                  # Custom implementations
│   ├── CustomHashMap.java           # Hash table implementation
│   ├── CustomLinkedList.java        # Linked list implementation
│   ├── CustomStack.java             # Stack implementation
│   ├── CustomQueue.java             # Queue implementation
│   ├── CustomSet.java               # Set implementation
│   ├── CustomTree.java              # Binary search tree
│   ├── MinHeap.java                 # Min heap implementation
│   └── Graph.java                   # Graph implementation
├── services/                        # Business logic
│   ├── ExpenditureService.java      # Expenditure operations
│   ├── CategoryService.java         # Category management
│   ├── BankAccountService.java      # Account operations
│   ├── SearchService.java           # Search functionality
│   ├── SortService.java             # Sorting algorithms
│   └── AnalyticsService.java        # Financial analysis
└── storage/                         # Data persistence
    ├── FileManager.java             # File I/O operations
    └── DataPersistence.java         # Data serialization
```

## Getting Started

### Prerequisites
- Java 11 or higher
- Maven 3.6 or higher

### Installation

1. **Clone or download the project**
   ```bash
   cd "d:\S.K\LECTURES\L300 2nd Sem\DSA 2\GROUP PROJECT\NREL"
   ```

2. **Compile the project**
   ```bash
   mvn compile
   ```

3. **Run the application**
   ```bash
   mvn exec:java -Dexec.mainClass="com.nkwarealestate.expenditure.Main"
   ```

### Quick Start
```bash
# Compile and run in one command
mvn compile exec:java
```

## Usage

Upon starting the application, you'll see the main menu:

```
=================== MAIN MENU ===================
1. Expenditure Management
2. Category Management  
3. Bank Account Management
4. Search & Sort
5. Receipt/Invoice Management
6. Financial Analysis & Reports
7. System Settings
0. Exit
=================================================
```

### Key Workflows

1. **Set Up Bank Accounts**: Start by adding your company's bank accounts
2. **Create Categories**: Define expenditure categories (Cement, Printing, etc.)
3. **Record Expenditures**: Add expenditure records with proper categorization
4. **Upload Receipts**: Link receipts to expenditures for validation
5. **Generate Reports**: Analyze spending patterns and forecasts

## Development

### Building
```bash
mvn clean compile
```

### Testing
```bash
mvn test
```

### Packaging
```bash
mvn package
```

## Data Storage

The application stores data in local text files:
- `data/expenditures.txt` - Expenditure records
- `data/categories.txt` - Category definitions
- `data/accounts.txt` - Bank account information
- `data/receipts.txt` - Receipt/invoice records

## Architecture Principles

- **No External Dependencies**: All data structures built from scratch
- **Offline-First**: Complete functionality without internet
- **Accountant-Centric**: Workflows mirror real accounting processes
- **Performance-Optimized**: Efficient algorithms for large datasets
- **Extensible Design**: Easy to add new features and data structures

## Contributing

This is an educational project demonstrating fundamental computer science concepts. Focus areas for enhancement:

1. **Additional Data Structures**: Implement advanced trees, graphs
2. **Enhanced Analytics**: More sophisticated financial calculations
3. **UI Improvements**: Better command-line interface
4. **Data Validation**: Enhanced input validation and error handling

## License

Educational project for Nkwa Real Estate Ltd.

## Contact

For questions about this implementation, please refer to the course materials or contact the development team.

---

**Built with ❤️ for Nkwa Real Estate Ltd**  
*Delivering affordable housing solutions across Ghana*
