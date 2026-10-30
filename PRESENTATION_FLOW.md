# PRESENTATION FLOW: QMAR LAUNDRY ORDER MANAGEMENT SYSTEM
**Course:** Data Structures and Algorithms (Datastrc) — Final Project Presentation  
**Institution:** De La Salle Lipa (DLSL) — College of Information Technology & Engineering, Computer Science Department  
**Client:** QMAR Laundry Shop (Villa Lourdes, Brgy. Mataas na Lupa, Lipa City)  
**Format:** 4 Speakers | 10–12 Minutes Total Presentation (+ 3–5 Minutes Panel Defense)  
**Target Grade:** 50/50 Points (Exemplary Rating across all Rubric Categories)

---

## 👥 4-Speaker Team Allocation & Roles

| Speaker | Assigned Role | Core Topics Covered | Presentation Window | Target Time |
| :--- | :--- | :--- | :--- | :--- |
| **Speaker 1** | **The Client Storyteller & Business Architect** | Real-world dorm context, QMAR shop bottlenecks, client interviews, High-level MVC architecture, OOP principles | Slide 1 – Slide 3 | ~2.5 mins (0:00 – 2:30) |
| **Speaker 2** | **The Data Structure Architect** | FIFO Queues (`washingQueue`, `pickupQueue`), LIFO Stacks (`trikeStack` physical vehicle packing, `undoStack` transactional rollback) | Slide 4 – Slide 5 | ~2.5 mins (2:30 – 5:00) |
| **Speaker 3** | **The Algorithm & Live Demo Lead** | Searching ($O(n)$ Linear vs $O(\log n)$ Binary), Recursive Merge Sort ($O(n \log n)$ stable deadline routing), Full Live Console Demo | Slide 6 – Slide 7 | ~3.5 mins (5:00 – 8:30) |
| **Speaker 4** | **The Quality Assurance & Resilience Lead** | Edge-case engineering (the 4 GitHub issues battle: EOF streams, Infinity weights, name collisions, delivery limits), Wrap-up, Q&A kickoff | Slide 8 – Slide 9 | ~2.5 mins (8:30 – 11:00) |
| **All 4** | **The Unified Engineering Team** | Technical Panel Q&A Defense (Pre-assigned specialized domains) | Defense / Q&A | ~3–5 mins (11:00 – 15:00) |

---

## 🎭 Stage Presence & Delivery Guidelines ("The Human Touch")

1. **Be Conversational, Not Robotic:**  
   Don't read verbatim off the slides or notes. Speak to the panel as colleagues and engineering mentors. Use hand gestures, smile, and make direct eye contact with Professor Balita and the panel.
2. **Tell the Student Story:**  
   Ground every technical decision in the lived reality of Villa Lourdes dormers and laundry shop staff. Relatable anecdotes keep the panel engaged.
3. **Seamless Verbal Handoffs:**  
   Never let dead silence fall between speakers. Use warm, natural transitions (e.g., *"To break down how we mapped these physical baskets into textbook queues and stacks, here is [Speaker 2]!"*).
4. **Physical Team Chemistry:**  
   While one member speaks, other members should stay actively engaged—nodding, smiling, or assisting with live console typing. It shows cohesive teamwork.

---

## 🎬 Detailed Slide-by-Slide Speaking Script & Flow

```
========================================================================================
PART 1: THE HOOK, CLIENT CONTEXT & ARCHITECTURE
Speaker: Speaker 1 (The Client Storyteller & Business Architect)
Time: 0:00 – 2:30 (2.5 Minutes)
========================================================================================
```

### Slide 1: Welcome & Project Introduction
* **On Screen:** Clean slide with DLSL CITE logo, QMAR Laundry Shop logo, System Title: *"QMAR Laundry Shop Order Management System: Bridging Data Structures to Real-World Operations"*, and the names of all 4 group members.
* **Stage Direction:** *[Stand centered, smile warmly, look at Professor Balita and the panel, speak with confident, conversational energy.]*
* **Speaker 1 Script:**
  > "Good day, Professor Balita, esteemed panel members, and fellow computer science students!
  > 
  > Let me start with a quick question that every single student in this room will relate to:
  > *Have you ever experienced that dreadful Sunday night panic in your dorm—where you have a 7:30 AM presentation on Monday morning, you walk over to pick up your laundry, and the ate at the counter says, 'Ay teka lang, hanapin ko muna sa bunton ng labahan yung basket mo'?*
  > 
  > *[Pause for a slight chuckle / smiles from the panel]*
  > 
  > That exact student reality is why our group chose **QMAR Laundry Shop** in Villa Lourdes, Mataas na Lupa, right outside De La Salle Lipa. Today, we're proud to present our final project for Data Structures and Algorithms: a complete, resilient Order Management System built not just to check academic boxes, but to solve real, everyday operational headaches using pure Java and foundational computer science principles."

---

### Slide 2: The Client & The Paper Logbook Chaos
* **On Screen:** Photo/map of Villa Lourdes, Lipa City showing proximity to DLSL; split graphic: A messy, water-stained physical logbook vs. Our clean, structured digital terminal system.
* **Rubric Target:** *Discussion of Documentation: Thoroughly details client's operational problem (15 pts).*
* **Speaker 1 Script:**
  > "When we visited QMAR, we found a bustling business serving hundreds of students from Villa Lourdes Dorm, Green Residences, and St. Joseph Hall. But behind the friendly service, their workflow ran entirely on **handwritten logbooks, sticky notes, and physical whiteboards**.
  > 
  > On high-volume peak days like Friday drop-offs and Sunday pick-ups, this created four serious operational problems:
  > 1. **Misplaced Baskets & Broken Queueing:** Orders were washed based on which basket was closest to the washer door, meaning students who dropped off clothes first often waited the longest.
  > 2. **Unclaimed Laundry Piles:** Clothes sat on the floor for weeks with zero automated status tracking or payment alerts.
  > 3. **Delivery Route Confusion:** Tricycle drivers loaded deliveries at random. A student with an urgent 1:00 PM class deadline would see their uniform delayed because the driver took an arbitrary route.
  > 4. **Zero Audit Trail:** Erased pencil entries and lost receipts meant cash discrepancies at closing time.
  > 
  > We knew technology could fix this—not with bloated third-party libraries, but with the precise data structures we studied this semester."

---

### Slide 3: System Architecture & Object-Oriented Principles
* **On Screen:** Clean Layered Architecture Diagram:
  - **View Layer:** `Main.java` (Console UI, Menus, Scanner I/O, EOF protection)
  - **Controller Layer:** `LaundrySystem.java` (Business Logic, In-Memory Collections)
  - **Algorithmic Engines:** `SearchUtil.java`, `SortUtil.java`, `Validator.java`
  - **Domain Models:** `Customer`, `Order`, `Stop`, `UndoAction`, Enums
* **Rubric Target:** *Discussion of Documentation: Complete program architecture/design and OOP principles (15 pts).*
* **Speaker 1 Script:**
  > "To ensure our codebase is maintainable, we structured it around the **Model-View-Controller (MVC)** design pattern and strict Object-Oriented principles:
  > 
  > - **The View (`Main.java`):** Holds zero business logic. It simply renders menus, captures user keystrokes, and protects the system against abrupt terminal signals.
  > - **The Controller (`LaundrySystem.java`):** The operational brain. It coordinates all 15 menu actions, manages our queues and stacks, and maintains business invariants.
  > - **The Models & Utility Engines:** Domain entities like `Customer` and `Order` are strictly encapsulated with private fields and internal financial computations. Meanwhile, classes like `SearchUtil` and `SortUtil` are completely stateless, pure algorithmic engines.
  > - **Polymorphism in Action:** Throughout our code, we program to interfaces—using `Queue<Order>`, `Deque<Stop>`, and `List<Customer>`—while backing them with concrete `LinkedList`, `ArrayDeque`, and `ArrayList` implementations.
  > 
  > But the true magic of this system lies in *how* we selected each data structure to mirror physical shop operations. To explain how real-world laundry baskets turn into queues and tricycle sidecars become stacks, let me hand the floor over to our Data Structure Architect, **[Speaker 2]**!"

*(Speaker 1 smiles, gestures smoothly toward Speaker 2, and steps to the side while Speaker 2 steps forward.)*

---

```
========================================================================================
PART 2: DATA STRUCTURES ARCHITECTURE (QUEUES & STACKS)
Speaker: Speaker 2 (The Data Structure Architect)
Time: 2:30 – 5:00 (2.5 Minutes)
========================================================================================
```

### Slide 4: Queues in Real Life — FIFO (First-In, First-Out)
* **On Screen:** Visual diagram of `washingQueue` and `pickupQueue` implemented as `LinkedList<T>`. Animated visual showing an order enqueuing at the tail with $O(1)$ and dequeuing at the head with $O(1)$.
* **Rubric Target:** *Discussion of Documentation: Comprehensive justification of data structure choices (15 pts).*
* **Speaker 2 Script:**
  > "Thank you, [Speaker 1]! Good day everyone.
  > 
  > When our team first sat down to write code, we established one guiding philosophy: *we should never force a data structure onto a problem just because it looks fancy in an exam. The data structure must match the physical physics of the business.*
  > 
  > Take our **FIFO Queues**. In a student laundry service, fairness is non-negotiable. If student A brings their uniform in at 8:00 AM, and student B arrives at 9:00 AM, student A’s clothes *must* enter the washing machine first.
  > 
  > We implemented this using `Queue<Order> washingQueue = new LinkedList<>()`:
  > - When staff create an order via option `[2]`, it immediately enqueues at the tail in **$O(1)$ constant time**.
  > - When the washing machine opens up, option `[3]` calls `washingQueue.poll()`. The front-most order is dequeued in **$O(1)$ constant time**, moving its state from `WAITING` to `PROCESSING`.
  > 
  > We chose `LinkedList` over an `ArrayList` because queue operations only ever touch the ends. Inserting at the tail or removing from the head is a simple pointer reassignment—zero memory copying, zero resizing overhead, and zero chance of an order 'cutting the line'."

---

### Slide 5: Stacks in Real Life — LIFO (Last-In, First-Out)
* **On Screen:** Dual split graphic:
  1. *Physical Tricycle Loading:* Diagram of a tricycle sidecar showing Stop 2 loaded first at the deep front, and Stop 1 loaded last right at the tailgate door.
  2. *Transactional Undo Stack:* Snapshot diagram of `undoStack` holding `UndoAction` objects.
* **Speaker 2 Script:**
  > "Now, while queues represent fairness, **Stacks represent physical reality and human error**.
  > 
  > We use LIFO Stacks in two critical areas:
  > 
  > **First: The Tricycle Delivery Stack (`trikeStack`).**
  > If you've ever watched a delivery kuya load a motorized tricycle in Lipa City, you know you *cannot* load the first house at the very front of the sidecar. If you do, when you arrive at Stop 1, you'd have to physically unload every single student's laundry onto the dusty road just to reach that one bag at the bottom!
  > Instead, the driver packs the route in reverse: Stop 3 goes in deepest, Stop 2 goes in the middle, and **Stop 1 is packed last, right at the tailgate door**.
  > We model this with `Deque<Stop> trikeStack = new ArrayDeque<>()`. The route is pushed in reverse order, so when the driver arrives at dorms, calling `trikeStack.pop()` in **$O(1)$ time** instantly pops the exact bags needed for that stop!
  > 
  > **Second: The Counter Undo History (`undoStack`).**
  > Counter staff work fast. In the rush between classes, an ate might accidentally mark the wrong student's clothes as ready or enter a payment on the wrong ticket.
  > With our `undoStack`, every status change and payment pushes an `UndoAction` snapshot. Hitting option `[13]` pops the most recent action in **$O(1)$ time**, perfectly rolling back the order's state and financial balances.
  > 
  > Stacks keep our delivery trucks organized and our counter staff protected. But to find records instantly and organize delivery routes intelligently, we need algorithms. Here is **[Speaker 3]** to take us through our searching, sorting, and our live system demonstration!"

*(Speaker 2 smiles, nods to Speaker 3, and transitions the presentation.)*

---

```
========================================================================================
PART 3: ALGORITHMS & LIVE SYSTEM DEMONSTRATION
Speaker: Speaker 3 (The Algorithm & Live Demo Lead)
Time: 5:00 – 8:30 (3.5 Minutes)
========================================================================================
```

### Slide 6: Algorithmic Rigor — Searching & Merge Sort
* **On Screen:** Complexity Table + Visual diagram of recursive divide-and-conquer Merge Sort:
  - $O(n)$ Linear Search for multi-field customer lookups
  - $O(\log n)$ Binary Search for auto-incrementing Order IDs
  - $O(n \log n)$ Stable Merge Sort with Deadline-Driven delivery grouping
* **Rubric Target:** *Discussion of Documentation: Mathematical/algorithmic justification (15 pts).*
* **Speaker 3 Script:**
  > "Thank you, [Speaker 2]! Hi everyone, I'm [Speaker 3].
  > 
  > In building our algorithmic foundation, we didn't use black-box shortcuts like Java's built-in `Collections.sort()`. Every search and sort was hand-crafted:
  > 
  > 1. **Binary Search ($O(\log n)$):** Notice that when orders are placed, they receive sequential, auto-incrementing IDs like `LDY0001`, `LDY0002`. Because they are appended to an `ArrayList`, our master order list is *always strictly sorted by Order ID*. This allows our `binarySearchOrder()` method to locate any order among thousands in logarithmic time—powering instant lookups when a student comes to claim their clothes.
  > 2. **Linear Search ($O(n)$):** For customers, a student might search by their phone number, full name, or ID. Because a list cannot be sorted by three different fields simultaneously, we use `linearSearchCustomer()` for broad search, but enforce strict ID matching during order intake to eliminate student identity collisions.
  > 3. **Custom Recursive Merge Sort ($O(n \log n)$):** Why Merge Sort instead of Quick Sort? Two words: **Stability and Worst-Case Guarantees**.
  >    In a laundry shop, if two orders have the same 2:00 PM delivery deadline, a stable sort preserves the exact order they were received. Furthermore, Quick Sort degrades to $O(n^2)$ when data is already nearly sorted. Merge Sort guarantees $O(n \log n)$ performance every single time.
  > 
  > Now, enough slides—let's open up the console and watch our system solve these problems live!"

---

### Slide 7: Live Console Demonstration (Guided Walkthrough)
* **On Screen:** Switch to the live Windows terminal running `java Main`.
* **Rubric Target:** *Program: Functions flawlessly without crashes or logic errors (20 pts); Presentation: Cohesive, engaging, active participation (15 pts).*
* **Live Action Script & Narration:**

#### Action 1: System Boot & Realistic Sample Data
* *[Speaker 3 types `14` and presses Enter]*
* **Speaker 3 Narration:**
  > "Here is our categorized main menu. We begin by typing `14` to load realistic sample data: 5 student customers from Villa Lourdes, Green Residences, and St. Joseph Hall, and 10 orders spanning various stages of washing, ready, and completed states."

#### Action 2: Customer Registration & Duplicate Warning
* *[Speaker 3 types `1` to Add Customer]*
* *[Input: Name: `Juan Dela Cruz`]*
* **Speaker 3 Narration:**
  > "Watch what happens when a new student arrives named 'Juan Dela Cruz'. The system immediately flags a warning: *'Another customer named Juan Dela Cruz already registered with ID C001.'*
  > If we enter phone `09171234567`, it alerts: *'Contact number already registered!'* It doesn't crash or block honest registrations, but gives staff the intelligence to avoid mix-ups."
* *[Input: Dorm: `[2] Green Residences` | Room: `301` -> Registered as `C006`]*

#### Action 3: Strict Validation & Order Creation
* *[Speaker 3 types `2` to Create Order]*
* *[Test Ambiguity: Enter `Juan Dela Cruz` -> System rejects: `Customer not found!`]*
* **Speaker 3 Narration:**
  > "Notice our strict validation: typing the name Juan Dela Cruz is rejected because two exist. We must enter exact ID `C006`.
  > Now for weight: if someone types `Infinity` or `5d` or negative numbers, our validator rejects it instantly. Let's enter `7.0` kg.
  > The system automatically computes: 1 load, Php 220.00. We select Delivery, set a deadline of `16:30`, and select Pay Now with Cash."

#### Action 4: Washing Queue & The Undo Safety Net
* *[Speaker 3 types `3` (Process Next Order) -> Dequeues order to `PROCESSING`]*
* *[Speaker 3 types `4` (Mark Order Ready) -> Enters `LDY0004` -> Status becomes `READY`]*
* *[Speaker 3 types `13` (Undo Last Action)]*
* **Speaker 3 Narration:**
  > "Watch the `undoStack` in action! Staff marked `LDY0004` as ready by mistake. Option `13` pops the undo stack, rolls the status back to `PROCESSING`, and clears the ready timestamp. That's real transactional safety."

#### Action 5: Intelligent Deadline-Driven Trike Dispatch & LIFO Delivery
* *[Speaker 3 types `7` (Dispatch Trike)]*
* **Speaker 3 Narration:**
  > "This is our crowning feature: **Deadline-Driven Trike Routing**.
  > Look at the screen: we have 14 loads ready for delivery, but our trike has a strict safety limit of 10 loads.
  > Instead of picking dorms randomly, our Merge Sort evaluated the earliest deadlines. It loaded **Green Residences first because their deadline is 14:00**, and **Villa Lourdes second because their deadline is 15:00**, totaling 9 loads! St. Joseph with an 18:00 deadline is deferred to Run #2.
  > 
  > Now let's deliver:
  > *[Types `8` - Deliver Next Stop]* -> Arrives at Stop 1: Green Residences. Order completed!
  > *[Types `8` - Deliver Next Stop]* -> Arrives at Stop 2: Villa Lourdes. The order is UNPAID, so the system collects Php 440.00 cash on delivery and marks it completed!
  > The trike is now empty and ready for the next run."

#### Action 6: Late Orders Report
* *[Speaker 3 types `12` -> `[5]` Late Orders Report]*
* **Speaker 3 Narration:**
  > "Finally, our management reports. Notice order `LDY0006` is automatically flagged as LATE because our system calculated that its ready duration exceeded the 3-hour SLA using Java's `Duration` API.
  > 
  > Everything runs smoothly on the surface. But behind this smooth demo was an intense battle with tricky edge cases. To explain how we stress-tested this system and made it bulletproof, here is our Quality Lead, **[Speaker 4]**!"

*(Speaker 3 gestures to Speaker 4, who takes center stage.)*

---

```
========================================================================================
PART 4: EDGE CASES, ROBUSTNESS & PANEL DEFENSE
Speaker: Speaker 4 (The Quality Assurance & Resilience Lead)
Time: 8:30 – 11:00 (2.5 Minutes)
========================================================================================
```

### Slide 8: The Battle with Edge Cases — 4 Critical Bugs Squashed
* **On Screen:** Grid detailing the 4 GitHub Issues resolved during development:
  1. **Issue #5:** Input Stream EOF Protection (`NoSuchElementException`)
  2. **Issue #2:** Numeric Parsing Vulnerabilities (`Infinity`, `1e300`, NaN defense)
  3. **Issue #3:** Customer Identity Collision Defense
  4. **Issue #4:** Trike Dispatch Deadlock & Deadline Prioritization
* **Rubric Target:** *Program: Functions flawlessly without crashes across edge cases (20 pts).*
* **Speaker 4 Script:**
  > "Thank you, [Speaker 3]! 
  > 
  > You know, in computer science, it's easy to build a demo that works when the user types exactly what you expect. But as software engineers, our job is to make sure the program never crashes when the user does the *unexpected*.
  > 
  > During our QA testing, we tracked down and squashed four critical vulnerabilities:
  > 
  > 1. **The Infinite Stream Loop (Issue #5):**
  >    When testing our console with automated pipe inputs, the input stream reached End-Of-File (EOF). Instead of terminating, our validation loops treated the closed stream as empty strings, creating an infinite loop that generated **138 megabytes of error logs in 5 seconds**! We overhauled `promptLine()` to throw `NoSuchElementException` upon stream exhaustion, which `Main.java` catches to exit gracefully.
  > 2. **The 'Infinity' Bill Bug (Issue #2):**
  >    Did you know that standard Java `Double.parseDouble()` happily accepts the strings `'Infinity'`, `'1e300'`, and hex floats like `'0x1p4'` as valid positive numbers? If a tired cashier accidentally typed that, a student would be billed billions of pesos! We fortified `Validator.java` with a strict regex (`^\\d+(\\.\\d+)?$`), enforced `Double.isFinite()`, and capped weight at 1,000 kg.
  > 3. **The Same-Name Collision (Issue #3):**
  >    In a college town, having two students named 'Juan Dela Cruz' or 'Mark Santos' is an everyday occurrence. Our earlier linear search checked both name and ID, causing orders to silently attach to the first Juan! We decoupled fuzzy customer search from order intake, strictly mandating exact Customer IDs for all transactions.
  > 4. **The Trike Capacity Deadlock (Issue #4):**
  >    When testing high-volume dispatches, if the first 9 loads fit onto the trike, but the next dorm required 3 loads, our original loop kept spinning trying to fit the oversized stop. We introduced an immediate capacity break flag, allowing the trike to dispatch with 9 loads and defer the rest cleanly."

---

### Slide 9: Conclusion, Reflection & Team Closing
* **On Screen:** Summary Slide:
  - Theoretical DSA solving physical operational bottlenecks
  - Pure JDK 8+ architecture, 0 external dependencies
  - 100% test coverage across all core menu paths
  - Thank you message & team member credits
* **Speaker 4 Script:**
  > "To conclude: this project taught our team that Data Structures and Algorithms are not just abstract mathematical puzzles for exams.
  > - **Queues** protect customer fairness.
  > - **Stacks** mirror physical delivery sidecars and transactional rollbacks.
  > - **Binary Search** gives us lightning-fast access as order books grow.
  > - **Merge Sort** ensures students get their clothes before their class deadlines.
  > 
  > We transformed a chaotic, paper-based shop into an organized, automated, and mathematically sound system that QMAR Laundry Shop can genuinely rely on.
  > 
  > On behalf of [Speaker 1], [Speaker 2], [Speaker 3], and myself, thank you so much for your time and guidance, Professor Balita and members of the panel. 
  > 
  > We are now ready and excited to answer your questions!"

*(All 4 team members stand together in a line, smile, and prepare for Q&A.)*

---

```
========================================================================================
PART 5: PANEL Q&A DEFENSE STRATEGY & MODEL ANSWERS
Speakers: All 4 Members (Role-Divided Participation)
Time: 11:00 – 15:00 (3–5 Minutes)
========================================================================================
```

> **Panel Defense Team Strategy:**  
> When the panel asks a question, the assigned specialist steps forward to answer. If appropriate, a second teammate can add a 10-second supporting remark. This guarantees that **all 4 members earn full marks for active, confident defense**.

---

### Question 1 (To Speaker 3): *"Why did you use Merge Sort instead of Quick Sort? Isn't Quick Sort faster in practice?"*
* **Primary Respondent:** **Speaker 3 (Algorithms Lead)**
* **Model Answer:**
  > "While Quick Sort often has lower constant factors in memory, we specifically chose **Merge Sort** for two decisive reasons:
  > 
  > 1. **Stability:** In our system, delivery orders are grouped by dormitory. A *stable* sort preserves the original relative order of orders that have the identical deadline. Quick Sort is inherently unstable and would scramble that arrival order.
  > 2. **Guaranteed $O(n \log n)$ Worst-Case:** Quick Sort degrades to $O(n^2)$ when the pivot selection is poor or when data is already nearly sorted—which frequently occurs in a laundry shop where orders are entered chronologically. Merge Sort guarantees $O(n \log n)$ performance under all circumstances, ensuring our delivery dispatch never hangs."

---

### Question 2 (To Speaker 2): *"Why is `binarySearchOrder()` viable, but for customers you had to use linear search?"*
* **Primary Respondent:** **Speaker 2 (Data Structures Architect)**
* **Model Answer:**
  > "Binary Search strictly requires a collection that is indexed and pre-sorted by the search key.
  > - For **Orders**, each new order receives an auto-incrementing sequential ID (`LDY0001`, `LDY0002`). Because orders are appended sequentially to an `ArrayList`, the list is inherently, permanently sorted by Order ID. That gives us $O(1)$ random access and allows `binarySearchOrder()` to execute in $O(\log n)$ time.
  > - For **Customers**, a user might search by name, contact number, or ID. A single list cannot be sorted simultaneously by three different fields without maintaining multiple secondary indices. Given the localized scale of a neighborhood laundry shop, an $O(n)$ linear search across all fields is computationally lightweight, simple, and avoids indexing overhead."

---

### Question 3 (To Speaker 1): *"Why declare variables as `Queue<Order>` or `Deque<Stop>` instead of using `LinkedList` directly?"*
* **Primary Respondent:** **Speaker 1 (Architecture & OOP Lead)**
* **Model Answer:**
  > "That is a direct application of the **Liskov Substitution Principle** and **Programming to an Interface**.
  > By declaring `Queue<Order> washingQueue = new LinkedList<>()`, the rest of `LaundrySystem` interacts only with the abstract behavioral contract of a Queue—namely `add()`, `poll()`, `peek()`, and `isEmpty()`.
  > If in the future we decide to replace `LinkedList` with a bounded `ArrayBlockingQueue` or a priority-based queue, we only change the instantiation line. None of the operational methods in the system have to be rewritten. It decouples implementation from specification."

---

### Question 4 (To Speaker 4): *"How does your system prevent data corruption if a cashier tries to Undo an action that has already progressed?"*
* **Primary Respondent:** **Speaker 4 (Quality & Robustness Lead)**
* **Model Answer:**
  > "We implemented snapshot validation inside `UndoAction`.
  > When an action is performed, `UndoAction` records the Order reference, the previous status, and the expected current status.
  > If staff attempt to undo a 'Mark Ready' action, but that order has *already* been loaded onto the tricycle (`OUT_FOR_DELIVERY`) or claimed by the student (`COMPLETED`), our undo logic checks: `if (order.getStatus() != action.getExpectedCurrentStatus())`.
  > If the state has moved forward, the system safely aborts the rollback and informs the cashier that the action can no longer be undone, preserving data integrity."

---

### Question 5 (To Speaker 4 / Speaker 1): *"What happens if input is redirected from a file or automated test script and reaches the end?"*
* **Primary Respondent:** **Speaker 4 (Quality & Robustness Lead)**
* **Model Answer:**
  > "That was exactly what we resolved in **Issue #5**.
  > Previously, when a piped input reached EOF, `Scanner.nextLine()` would throw an exception or return empty strings, causing an infinite validation retry loop that flooded the disk.
  > We refactored `promptLine()` to detect when `hasNextLine()` is false and throw a dedicated `NoSuchElementException`. In `Main.java`, the entire execution loop is enclosed in a top-level try-catch block that intercepts stream exhaustion, displays a clean exit message, and gracefully shuts down the program with return code 0."

---

## 📋 Copy-Paste Instructions for Google Docs

To put this presentation flow into your group's shared Google Doc:
1. Open your document: [https://docs.google.com/document/d/1Bg0XWk6OJi6-NH6S67heXuoeO9_wdjhXq5a65CXfpas/edit](https://docs.google.com/document/d/1Bg0XWk6OJi6-NH6S67heXuoeO9_wdjhXq5a65CXfpas/edit)
2. In the left-hand margin, click the **Document Tabs** icon (or go to `Insert > Document Tabs`).
3. Click `+ Add tab` and title it **`Presentation flow`**.
4. Copy the entire contents of this markdown file and paste it directly into that tab!
