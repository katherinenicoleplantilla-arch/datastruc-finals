# PRESENTATION FLOW: QMAR LAUNDRY ORDER MANAGEMENT SYSTEM
**Course:** Final Project – Data Structures and Algorithms (Datastrc)  
**Institution:** De La Salle Lipa – College of Information Technology & Engineering, Computer Science Department  
**Client:** QMAR Laundry Shop (Villa Lourdes, Brgy. Mataas na Lupa, Lipa City)  
**Target Duration:** 10–12 Minutes (+ 3–5 Minutes Panel Q&A)  
**Target Grade:** 50/50 Points (Outstanding Rating per Course Rubric)

---

## 👥 Speaker Allocation & Role Matrix (Flexible for 2 to 5 Members)

| Section / Topic | 5-Member Team | 4-Member Team | 3-Member Team | 2-Member Team | Time |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **1. Intro & Business Problem** | Speaker 1 | Speaker 1 | Speaker 1 | Speaker 1 | 1.5 mins |
| **2. Architecture & OOP Design** | Speaker 2 | Speaker 2 | Speaker 1 | Speaker 1 | 2.0 mins |
| **3. DSA Justification & Math** | Speaker 3 | Speaker 3 | Speaker 2 | Speaker 2 | 2.5 mins |
| **4. Live System Demonstration** | Speaker 4 | Speaker 4 | Speaker 3 | Speaker 2 | 3.5 mins |
| **5. Edge Cases & Robustness** | Speaker 5 | Speaker 3 | Speaker 2 | Speaker 1 | 1.5 mins |
| **6. Summary & Panel Defense** | All Members | All Members | All Members | All Members | Q&A |

---

## ⏱️ Detailed Slide-by-Slide & Speaking Script Flow

```
========================================================================================
PART 1: INTRODUCTION & BUSINESS PROBLEM CONTEXT
Speaker: Member 1 | Time: 0:00 - 1:30
========================================================================================
```

### Slide 1: Title & Team Introduction
* **Visuals:** Project Title, DLSL CITE Logo, QMAR Laundry Shop Logo, Group Member Names & Roles.
* **Speaker Script:**
  > "Good day, esteemed panel members, Faculty Professor Balita, and fellow peers. Today, our team is proud to present our final project for Data Structures and Algorithms: the **Laundry Order Management System for QMAR Laundry Shop** in Lipa City. 
  > Our objective is not just to build a console tool, but to bridge academic theoretical algorithms with an operational real-world business solution."

### Slide 2: The Client & The Real-World Operational Challenge
* **Visuals:** Photos/map of Villa Lourdes, Lipa City; Comparison of Paper Logbook vs. Automated System.
* **Key Rubric Points Addressed:** *Thoroughly details client's operational problem (15 pts).*
* **Speaker Script:**
  > "QMAR Laundry Shop is located right beside major student dormitories near De La Salle Lipa—such as Villa Lourdes Dorm, Green Residences, and St. Joseph Hall. They handle wash-and-fold, dry cleaning, pickup, and trike deliveries.
  > However, the shop has operated entirely on **paper logbooks and physical whiteboard notes**. On high-volume days like Sundays:
  > 1. **Mixed-up laundry and misplaced bags** occur because intake and washing queues are untracked.
  > 2. **Unclaimed laundry piles up** on the shop floor without payment tracking.
  > 3. **Delivery routing is chaotic**—trike drivers deliver without route planning, causing urgent student deadlines to be missed.
  > 4. **Mistaken entries cannot be audited**, leading to cash discrepancies.
  > 
  > To solve this, we engineered a pure JDK Java application that uses core Data Structures and Algorithms to automate, organize, and safeguard every single operational workflow."

*(Verbal Handoff: "I now pass the floor to [Speaker 2] to discuss our system architecture and design.")*

---

```
========================================================================================
PART 2: SYSTEM ARCHITECTURE & OBJECT-ORIENTED DESIGN
Speaker: Member 2 | Time: 1:30 - 3:30
========================================================================================
```

### Slide 3: High-Level System Architecture & Layered Design
* **Visuals:** System Architecture Diagram (Console UI -> Controller -> Utilities & Data Structures Store -> Domain Models).
* **Key Rubric Points Addressed:** *Complete program architecture/design and OOP principles (15 pts).*
* **Speaker Script:**
  > "Our system adheres strictly to the **Model-View-Controller (MVC) and Separation of Concerns principles**:
  > - **The View/Driver (`Main.java`):** Contains zero business logic. It handles the interactive user loop, parses inputs, catches numeric exceptions, and cleanly handles end-of-stream EOF signals.
  > - **The Controller (`LaundrySystem.java`):** Central brain coordinating all 15 operational menu actions, queues, stacks, lists, and business transactions.
  > - **Domain Models (`Customer`, `Order`, `Stop`, `UndoAction`):** Pure state capsules representing physical shop entities.
  > - **Utility Classes (`Validator`, `SearchUtil`, `SortUtil`):** Stateless algorithmic engines carrying zero persistent state, handling regex parsing, linear/binary search, and merge sort."

### Slide 4: UML Class Diagram & OOP Principles
* **Visuals:** UML Class Diagram (Figure 1 in Documentation), highlighting Encapsulation, Polymorphism, and Aggregation.
* **Speaker Script:**
  > "Our design demonstrates core Object-Oriented Programming:
  > - **Encapsulation:** Model fields are private. For instance, in `Order.java`, `loads` and `totalAmount` are computed strictly inside the class via `computeLoads()` and `computeTotal()`. External classes cannot maliciously alter financial figures.
  > - **Polymorphism via Interfaces:** We declare all structures by their Java Collections interfaces—`Queue<Order>`, `Deque<Stop>`, and `List<Customer>`—instantiating them as `LinkedList`, `ArrayDeque`, and `ArrayList`.
  > - **Aggregation & Association:** An `Order` associates with exactly one `Customer`. A delivery `Stop` aggregates multiple `Order`s for a single dorm. An `UndoAction` holds an `Order` reference and a status snapshot for transactional rollbacks."

*(Verbal Handoff: "Next, [Speaker 3] will explain our mathematical justification and implementation of Data Structures and Algorithms.")*

---

```
========================================================================================
PART 3: CONCEPTS OF DATA STRUCTURES & ALGORITHMS (DSA JUSTIFICATION)
Speaker: Member 3 | Time: 3:30 - 6:00
========================================================================================
```

### Slide 5: Queue Implementations (FIFO - First-In, First-Out)
* **Visuals:** Queue diagram showing `washingQueue`, `pickupQueue`, and `stopQueue`.
* **DSA Focus:** `Queue<T>` implemented as `LinkedList<T>`.
* **Speaker Script:**
  > "Why did we implement Queues?
  > In a student laundry service, fairness is critical: nobody's laundry should be skipped.
  > - **`washingQueue` (FIFO `LinkedList<Order>`):** When orders are created, they enqueue at the tail ($O(1)$ `add`). When the washer is free, staff choose option 3 to dequeue the front order ($O(1)$ `poll`). Arrival order strictly equals washing order.
  > - **`pickupQueue` (FIFO `LinkedList<Customer>`):** Tracks student requests for laundry pickup from dorms.
  > - **`stopQueue` (FIFO `LinkedList<Stop>`):** Holds aggregated dormitory stops for trike dispatch before stack loading."

### Slide 6: Stack Implementations (LIFO - Last-In, First-Out)
* **Visuals:** Diagram showing physical trike loading LIFO and the Undo History Stack.
* **DSA Focus:** `Deque<T>` implemented as `ArrayDeque<T>`.
* **Speaker Script:**
  > "We utilize Stacks in two distinct, real-world areas:
  > 1. **`trikeStack` (Physical Trike Delivery Stack):** 
  >    A delivery tricycle is packed physically from front to back. The stops scheduled for the first dorm must be loaded *last* so they sit right at the tricycle tailgate for immediate unloading. By pushing stops onto `trikeStack` in reverse sequence, popping the top of the stack ($O(1)$ `pop`) delivers Stop 1, then Stop 2, matching real-world physical mechanics.
  > 2. **`undoStack` (Operational Undo History):** 
  >    Mistakes happen at the front counter. When staff record a payment or mark an order ready, an `UndoAction` snapshot is pushed ($O(1)$). Popping the stack reverses the most recent action first, ensuring transactional state safety."

### Slide 7: Searching & Sorting Algorithms (Linear, Binary, & Merge Sort)
* **Visuals:** Complexity Comparison Table + Divide-and-Conquer Merge Sort visualization.
* **DSA Focus:** Hand-written iterative Linear/Binary Search and recursive Merge Sort.
* **Speaker Script:**
  > "To satisfy project requirements without reliance on black-box utilities like `Collections.sort`:
  > - **Linear Search ($O(n)$):** In `SearchUtil.linearSearchCustomer()`, we search unsorted customer lists by partial name, ID, or phone number. For order intake, we enforce strict ID-only linear search (`findCustomerById`) to prevent duplicate name misattributions.
  > - **Binary Search ($O(\log n)$):** Because new orders are appended with auto-incrementing IDs (`LDY0001`, `LDY0002`), the master `orders` list remains strictly sorted by Order ID. `binarySearchOrder()` finds orders in logarithmic time, powering Mark Ready, Record Payment, and Claim.
  > - **Merge Sort ($O(n \log n)$ stable):** A custom recursive divide-and-conquer implementation. It operates out-of-place on a list copy, leaving the original order list sorted for binary search. It powers multi-key sorting in View Orders and intelligent deadline-driven delivery routing."

*(Verbal Handoff: "Let us now witness the system running live with [Speaker 4] leading our demonstration.")*

---

```
========================================================================================
PART 4: LIVE PROGRAM DEMONSTRATION FLOW
Speaker: Member 4 (Driver / Presenter) | Time: 6:00 - 9:30
========================================================================================
```

### Live Demo Step-by-Step Script:

#### Step 1: System Boot & Realistic Sample Data
* **Action:** Launch `java Main`.
* **Show Screen:** Clean 15-option categorized main menu with 30-character border.
* **Command:** Type `14` (Load Sample Data).
* **Narration:** *"We begin by loading realistic sample data consisting of 5 student dorm customers and 10 orders spanning Waiting, Processing, Ready, and Completed states across Lipa City dormitories."*

#### Step 2: Customer Registration & Duplicate Warning
* **Action:** Type `1` (Add Customer).
* **Input:** Name: `Juan Dela Cruz` -> Notice appears: *"Another customer named 'Juan Dela Cruz' already registered (ID: C001)"*.
* **Input:** Phone: `09171234567` -> Warning appears: *"Contact number already registered to Juan Dela Cruz (C001)"*.
* **Input:** Dorm: Select `[2] Green Residences` | Room: `301`.
* **Narration:** *"Notice our duplicate warning system! The registration succeeds as C006, but alerts staff to avoid student identity mix-ups."*

#### Step 3: Order Intake & Strict Validation
* **Action:** Type `2` (Create Order).
* **Test Ambiguity:** Enter `Juan Dela Cruz` -> System rejects with `"Customer not found!"` *(proves orders can never be silently misattributed to the wrong student)*.
* **Input:** Enter `C006`.
* **Test Weight Defense:** Enter `Infinity` or `5d` -> System rejects with `"Invalid Weight"`.
* **Input:** Enter `7.0` kg -> Computed: 1 load, Php 220.00.
* **Input:** Return: `[2] Delivery` | Deadline: `16:30` | Payment: `[1] Pay Now` -> `[1] Cash`.
* **Narration:** *"The order is created, appended to the sorted order database, and enqueued at the back of the washing line as position #3."*

#### Step 4: Washing Workflow & LIFO Undo
* **Action:** 
  1. Type `3` (Process Next Order) -> Dequeues front order `LDY0002` from `washingQueue` to `PROCESSING`.
  2. Type `4` (Mark Order Ready) -> Enters `LDY0004` -> Status becomes `READY`.
  3. Type `13` (Undo Last Action) -> Popped from `undoStack`!
* **Narration:** *"Watch the undo stack in action. The status of LDY0004 instantly rolls back from READY to PROCESSING, and its timestamp is cleared. If we choose undo again, it pops the next action in reverse sequence."*

#### Step 5: Trike Dispatch with Deadline Routing & Capacity Cap
* **Action:** Type `7` (Dispatch Trike).
* **Point Out on Screen:**
  - Total ready delivery orders = 14 loads. Trike capacity = 10 loads.
  - Notice the route order:
    - **Stop 1:** Green Residences `[Target Deadline: 14:00]` (4 loads)
    - **Stop 2:** Villa Lourdes Dorm `[Target Deadline: 15:00]` (5 loads)
    - Total = 9 loads. Capacity reached! St. Joseph (18:00) and Lipa Heights (No deadline) are deferred to Run #2!
* **Narration:** *"This highlights our deadline-driven routing algorithm. Rather than delivering alphabetically, the trike groups by dorm and prioritizes urgent deadlines, packing the trike with the 14:00 and 15:00 orders first."*

#### Step 6: LIFO Physical Delivery & Cash on Delivery
* **Action:** 
  1. Type `8` (Deliver Next Stop) -> Arrives at Stop 1: Green Residences (14:00 deadline). Pre-paid online order marked COMPLETED.
  2. Type `8` (Deliver Next Stop) -> Arrives at Stop 2: Villa Lourdes. Order LDY0007 is UNPAID -> System collects Php 440.00 cash on the spot and marks COMPLETED.
  3. Try Type `8` again -> System displays `"No active delivery run!"`.
* **Narration:** *"LIFO unloading complete. The trike is now empty and ready for Run #2."*

#### Step 7: Multi-Key Merge Sort & Operational Reports
* **Action:** 
  1. Type `11` (View Orders) -> Sort by `[4] Delivery Deadline`. Show table sorted chronologically.
  2. Type `12` (System Reports) -> Type `[5]` (Late Orders Report). 
* **Narration:** *"Order LDY0006 is automatically flagged as LATE because its ready duration exceeded the 3-hour threshold calculated via `java.time.Duration`."*

*(Verbal Handoff: "I will now pass the floor to [Speaker 5] to discuss edge-case engineering and reliability.")*

---

```
========================================================================================
PART 5: EDGE CASES, SYSTEM ROBUSTNESS & BUG RESILIENCE
Speaker: Member 5 | Time: 9:30 - 11:00
========================================================================================
```

### Slide 8: Enterprise-Grade Edge Case Engineering
* **Visuals:** Grid showing GitHub Issues resolved: #2, #3, #4, #5.
* **Key Rubric Points Addressed:** *Functions flawlessly without crashes or logic errors across all edge cases (20 pts).*
* **Speaker Script:**
  > "During rigorous QA and stress testing, we discovered and resolved critical boundary conditions:
  > 1. **Input Stream Exhaustion (Issue #5):** When input reaches EOF or stream closing, `promptLine()` throws `NoSuchElementException` caught by `Main.java`, terminating cleanly without infinite log loops.
  > 2. **Numeric Validation & Overflow Defense (Issue #2):** Java's `Double.parseDouble` naturally accepts strings like `'Infinity'` or `'1e300'`. We added regex filtering (`^\\d+(\\.\\d+)?$`), `Double.isFinite()`, and a `1000.0 kg` upper bound.
  > 3. **Student Ambiguity & Identity Collisions (Issue #3):** We separated general search from transactional lookups. Order intake and pickups require exact Customer IDs.
  > 4. **Deadline-Driven Routing & Cap Guard (Issue #4):** Replaced static alphabetical grouping with deadline-prioritized routing, and guarded the grouping loop so runs never stall."

---

```
========================================================================================
PART 6: CONCLUSION & PANEL DEFENSE PREPARATION
All Members | Time: 11:00 - End
========================================================================================
```

### Slide 9: Summary of Learnings & Impact
* **Speaker Script:**
  > "To conclude: this project demonstrated how theoretical data structures solve concrete commercial bottlenecks. 
  > - Queues eliminate customer disputes over priority.
  > - Stacks mirror real-world physical delivery vehicle packing and transactional undo.
  > - Binary Search provides instant lookups as order volume scales.
  > - Merge Sort delivers orders to students before their deadlines expire.
  > 
  > Thank you very much. We are now open for your questions and panel defense."

---

## 🎯 Panel Q&A Defense Guide (Comprehensive Model Answers)

### Question 1: *"Why did you use Merge Sort instead of Quick Sort?"*
* **Ideal Answer:**
  > "We specifically selected Merge Sort because of **stability** and **worst-case time complexity guarantee**.
  > 1. **Stability:** In our system, orders are grouped by dormitory. A stable sort preserves the existing relative order of orders within the same dorm or with identical deadlines.
  > 2. **Guaranteed $O(n \log n)$:** Quick Sort degrades to $O(n^2)$ when inputs are nearly sorted (which happens frequently when orders arrive chronologically). Merge Sort guarantees $O(n \log n)$ performance across best, average, and worst cases."

### Question 2: *"Why is `binarySearchOrder()` viable, but customer searching uses linear search?"*
* **Ideal Answer:**
  > "Binary search strictly requires an indexed collection sorted by the search key. New orders receive sequential, auto-incrementing IDs (`LDY0001`, `LDY0002`, etc.) and are appended to `ArrayList<Order>`. Thus, the orders list is inherently sorted by Order ID, enabling $O(\log n)$ search with $O(1)$ random access.
  > Conversely, the customers list cannot remain sorted simultaneously by Customer ID, Student Name, and Contact Number. Therefore, `SearchUtil.linearSearchCustomer()` applies an $O(n)$ search across all fields, which is fast and optimal for a localized shop."

### Question 3: *"Why did you declare `washingQueue` as `Queue<Order>` but instantiate it as `LinkedList<Order>`?"*
* **Ideal Answer:**
  > "This demonstrates **Polymorphism and Dependency Inversion**. By typing the variable to the `Queue` interface, the rest of the application interacts only with standard queue operations (`add`, `poll`, `peek`, `isEmpty`). We selected `LinkedList` as the concrete backing implementation because inserting at the tail and removing from the head are strictly $O(1)$ pointer operations without the array-resizing overhead of an `ArrayList`."

### Question 4: *"What happens if a trike driver tries to dispatch a new run while stops are still on the trike?"*
* **Ideal Answer:**
  > "We implemented an active run safety guard in `dispatchTrike()`: `if (!trikeStack.isEmpty())`. The system inspects the trike stack and refuses to start a new dispatch until the driver completes option `[8] Deliver Next Stop` for all remaining stops, preventing undelivered bags from being overwritten."

### Question 5: *"How does your Undo stack prevent data corruption if an order has already been delivered?"*
* **Ideal Answer:**
  > "`UndoAction` captures the order reference, description, and expected status snapshots. If staff attempt to undo a 'Mark Ready' action, but the order has already been loaded onto the trike (`OUT_FOR_DELIVERY`) or claimed at the counter (`COMPLETED`), the system detects that the live status differs from expected and safely rejects the rollback."

---

## 📋 Copy-Paste Instructions for Google Docs
1. Open your shared Google Doc: [https://docs.google.com/document/d/1Bg0XWk6OJi6-NH6S67heXuoeO9_wdjhXq5a65CXfpas/edit](https://docs.google.com/document/d/1Bg0XWk6OJi6-NH6S67heXuoeO9_wdjhXq5a65CXfpas/edit)
2. Click the **Document Tabs** icon on the left margin (or select `Insert > Document Tabs`).
3. Click `+ Add tab` and name the tab **`Presentation flow`**.
4. Copy the entire contents of this guide and paste it directly into that tab!
