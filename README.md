# QMAR Laundry Order Management System

A Java console application developed for **QMAR Laundry Shop**, located inside Villa Lourdes, Barangay Mataas na Lupa, Lipa City. The shop serves students from nearby universities and dormitories, offering laundry management from intake to washing, drying, payment processing, pickup queuing, tricycle delivery routing, and reporting.

---

## Technical Specifications

- **Language & Runtime:** Java 17+ (LTS) — built and verified on Java 25.
- **Dependencies:** None (Pure Java Standard Library / JDK only).
- **Architecture:** Object-Oriented Design (OOP) following strict Separation of Concerns (Driver, Controller, Domain Models, Enums, and Utility classes).
- **Data Persistence:** In-memory collection architecture.
- **Algorithms:** 100% hand-crafted Divide-and-Conquer Merge Sort, Binary Search, and Linear Search without third-party libraries or Java Collections sorting shortcuts.

---

## Compilation and Execution

### 1. Compile the Application
From the project root directory, run:
```bash
javac *.java
```

### 2. Run the Application
Launch the console UI:
```bash
java Main
```

### 3. Run Automated Tests
All automated test harnesses are organized in the `tests/` directory:
```bash
javac -cp . tests/*.java
java -cp ".;tests" EndToEndTest
java -cp ".;tests" Stage1Test
java -cp ".;tests" Stage2Test
java -cp ".;tests" Stage3Test
```

---

## Data Structures and Algorithms Mapping

| Concept | Implementation Type | Program Variable / Location | Purpose and Complexity |
| :--- | :--- | :--- | :--- |
| **Linear Search** | Iterative algorithm | `SearchUtil.linearSearchCustomer()` | Searches student records by Customer ID, Name, or 11-digit Contact Number ($O(n)$ time). Used during customer intake, order creation, and search. |
| **Binary Search** | Iterative algorithm (`low`, `high`, `mid`) | `SearchUtil.binarySearchOrder()` / `LaundrySystem.findOrderById()` | Fast $O(\log n)$ lookup of orders by unique Order ID (`LDY####`). Leverages the guarantee that `orders` is kept in ascending Order ID order. |
| **Merge Sort** | Recursive Divide-and-Conquer | `SortUtil.mergeSort()`, `merge()` | Stable $O(n \log n)$ sort with 5 custom comparators (Total Amount, Customer Name, Time Received, Delivery Deadline, Dormitory). Operates strictly on a list copy in `viewOrders()` to protect original Order ID order. Also sorts delivery orders by dorm in `dispatchTrike()`. |
| **FIFO Queue** | `LinkedList<Order>` | `LaundrySystem.washingQueue` | First-In, First-Out queue ensuring laundry is washed strictly in the order it was dropped off or received ($O(1)$ enqueue/dequeue). |
| **FIFO Queue** | `LinkedList<Customer>` | `LaundrySystem.pickupQueue` | First-In, First-Out queue managing dorm laundry pickup requests submitted by students ($O(1)$ enqueue/dequeue). |
| **FIFO Queue** | `LinkedList<Stop>` | `LaundrySystem.stopQueue` | First-In, First-Out staging queue holding aggregated delivery stops before loading onto the delivery tricycle. |
| **LIFO Stack** | `ArrayDeque<Stop>` | `LaundrySystem.trikeStack` | Last-In, First-Out stack modeling physical trike packing. Delivery stops are pushed in reverse order so the first scheduled stop pops off the top upon arrival. |
| **LIFO Stack** | `ArrayDeque<UndoAction>` | `LaundrySystem.undoStack` | Last-In, First-Out stack supporting instantaneous reversal of payment records or accidental "Mark Ready" actions ($O(1)$ push/pop). |
| **Dynamic List** | `ArrayList<Customer>` | `LaundrySystem.customers` | Dynamically growing linear repository storing registered student profiles. |
| **Dynamic List** | `ArrayList<Order>` | `LaundrySystem.orders` | Central order repository, strictly maintained in ascending Order ID order for binary search efficiency. |
| **Dynamic List** | `ArrayList<Order>` | `Stop.orders` | Aggregates all student orders belonging to the same dormitory for a single delivery run. |
| **Nested List** | `ArrayList<List<Stop>>` | `LaundrySystem.deliveryLog` | Historical audit log recording all dispatched delivery runs throughout the day. |

---

## System Menu Structure

```text
=======================================================================
                    MAIN MENU - QMAR LAUNDRY SHOP                      
=======================================================================
  [1]  Add Customer
  [2]  Create Order
  [3]  Process Next Order (Washing Line)
  [4]  Mark Order Ready
  [5]  Record Payment
  [6]  Pickup Requests (Enqueue / Dequeue)
  [7]  Dispatch Trike (Delivery Routing & Capacity Cap)
  [8]  Deliver Next Stop (LIFO Trike Unloading)
  [9]  Claim Order
  [10] Search (Customer / Order)
  [11] View Orders (Multi-Key Sort)
  [12] System Reports (Pending/Unpaid/Sales/Delivery/Late)
  [13] Undo Last Action (Payment / Mark Ready)
  [14] Load Sample Data
  [15] Exit
=======================================================================
```

---

## Business Constants & Configuration

- **`KG_PER_LOAD = 7.0`**: Laundry weight per load; computed as `(int) Math.ceil(weightKg / 7.0)`.
- **`PRICE_PER_LOAD = 220.0`**: Shop billing rate per load (`Php 220.00`).
- **`TRIKE_CAPACITY = 10`**: Maximum load capacity per delivery run enforced during dispatch.
- **`LATE_HOURS = 3`**: Threshold for flagging ready orders as delayed/late via `java.time.Duration`.
- **Custom Dormitories**: The shop does not restrict student addresses to a rigid static list. Staff can dynamically add and manage custom dormitories when registering customers, while previously registered dormitories are indexed for quick one-key selection.
