import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Scanner;

/**
 * Controller class managing the business operations for QMAR Laundry Shop in Lipa City.
 *
 * Coordinates domain models (Customer, Order, Stop, UndoAction), validation rules (Validator),
 * sorting (SortUtil), and searching (SearchUtil).
 *
 * Data Structures Employed:
 * 1. ArrayList (List<Customer> customers): Linear-searchable repository of registered students.
 * 2. ArrayList (List<Order> orders): Binary-searchable order database, maintained in strict
 *    increasing Order ID order (LDY0001, LDY0002...).
 * 3. LinkedList (Queue<Order> washingQueue): First-In-First-Out (FIFO) queue for orders waiting
 *    to be washed and dried.
 * 4. LinkedList (Queue<Customer> pickupQueue): FIFO queue for student laundry pickup requests.
 * 5. LinkedList (Queue<Stop> stopQueue): FIFO queue holding grouped dorm stops for delivery runs.
 * 6. ArrayDeque (Deque<Stop> trikeStack): Last-In-First-Out (LIFO) stack modeling physical trike
 *    loading, where stops are pushed in reverse order so the first stop pops first.
 * 7. ArrayDeque (Deque<UndoAction> undoStack): LIFO stack recording reversible operations
 *    (payments and ready statuses).
 * 8. ArrayList (List<List<Stop>> deliveryLog): Historical archive of all dispatched delivery runs.
 */
public class LaundrySystem {

    // --- System Constants ---
    public static final double KG_PER_LOAD = 7.0;       // 7.0 kg per single washing load
    public static final double PRICE_PER_LOAD = 220.0;  // Php 220 per load
    public static final int TRIKE_CAPACITY = 10;        // Maximum 10 loads per tricycle delivery run
    public static final int LATE_HOURS = 3;             // 3 hours threshold for flagging ready orders as late

    // --- Data Structures ---
    private List<Customer> customers;          // All registered customers (ArrayList)
    private List<Order> orders;                // All orders, stored in Order ID order (ArrayList)
    private List<String> dorms;                // Dynamic list of custom dormitories registered by staff (ArrayList)
    private Queue<Order> washingQueue;         // FIFO washing queue (LinkedList)
    private Queue<Customer> pickupQueue;       // FIFO pickup requests queue (LinkedList)
    private Queue<Stop> stopQueue;             // FIFO delivery stop queue (LinkedList)
    private Deque<Stop> trikeStack;            // LIFO trike delivery stack (ArrayDeque)
    private Deque<UndoAction> undoStack;       // LIFO undo history stack (ArrayDeque)
    private List<List<Stop>> deliveryLog;      // Dispatched delivery runs log (ArrayList)

    // --- State Counters & Input ---
    private int customerCounter;               // Counter for C### auto-generated IDs
    private int orderCounter;                  // Counter for LDY#### auto-generated IDs
    private Scanner scanner;                   // Console input reader

    /**
     * Default constructor initializing all collections and standard console scanner.
     */
    public LaundrySystem() {
        this(new Scanner(System.in));
    }

    /**
     * Overloaded constructor accepting an external Scanner (enables automated test drivers).
     *
     * @param scanner input scanner instance
     */
    public LaundrySystem(Scanner scanner) {
        this.customers = new ArrayList<>();
        this.orders = new ArrayList<>();
        this.dorms = new ArrayList<>();
        this.washingQueue = new LinkedList<>();
        this.pickupQueue = new LinkedList<>();
        this.stopQueue = new LinkedList<>();
        this.trikeStack = new ArrayDeque<>();
        this.undoStack = new ArrayDeque<>();
        this.deliveryLog = new ArrayList<>();
        this.customerCounter = 0;
        this.orderCounter = 0;
        this.scanner = (scanner != null) ? scanner : new Scanner(System.in);
    }

    // =========================================================================
    // ID GENERATION AND HELPER METHODS
    // =========================================================================

    /**
     * Generates the next sequential Customer ID formatted as C followed by 3 digits (e.g. C001).
     *
     * @return auto-generated customer ID
     */
    public String generateCustomerId() {
        customerCounter++;
        return String.format("C%03d", customerCounter);
    }

    /**
     * Generates the next sequential Order ID formatted as LDY followed by 4 digits (e.g. LDY0001).
     *
     * @return auto-generated order ID
     */
    public String generateOrderId() {
        orderCounter++;
        return String.format("LDY%04d", orderCounter);
    }

    /**
     * Wrapper method that invokes SearchUtil.binarySearchOrder() to find an order by ID.
     * Assumes the orders list is kept sorted by Order ID.
     *
     * @param orderId the order ID to find
     * @return matched Order object, or null if not found
     */
    public Order findOrderById(String orderId) {
        return SearchUtil.binarySearchOrder(this.orders, orderId);
    }

    /**
     * Registers a custom dormitory name in the system's dynamic dorms list.
     * Prevents case-insensitive duplicates.
     *
     * @param dormName the dormitory name to register
     * @return true if added, false if already exists or invalid
     */
    public boolean addCustomDorm(String dormName) {
        if (!Validator.isValidDorm(dormName)) {
            return false;
        }
        String trimmed = dormName.trim();
        for (String d : dorms) {
            if (d.equalsIgnoreCase(trimmed)) {
                return false; // Already registered
            }
        }
        dorms.add(trimmed);
        return true;
    }

    /**
     * Returns the dynamic list of custom dormitories registered by staff.
     *
     * @return list of dormitory names
     */
    public List<String> getDorms() {
        return dorms;
    }

    /**
     * Helper to read a trimmed line of text from the console.
     * Throws NoSuchElementException when the input stream ends
     * to prevent infinite loops in interactive sub-prompts.
     *
     * @param prompt prompt message to display
     * @return trimmed user input
     * @throws NoSuchElementException if end of input stream is reached
     */
    private String promptLine(String prompt) {
        System.out.print(prompt);
        if (scanner.hasNextLine()) {
            return scanner.nextLine().trim();
        }
        throw new NoSuchElementException("End of input stream reached.");
    }

    // =========================================================================
    // MENU OPTION 1: ADD CUSTOMER
    // =========================================================================

    /**
     * Menu 1: Reads and validates customer details using do-while loops,
     * generates a unique C### ID, and appends the customer to the customers list.
     */
    public void addCustomer() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("               REGISTER NEW CUSTOMER              ");
        System.out.println("--------------------------------------------------");

        // Read and validate student name
        String name;
        do {
            name = promptLine("Enter Customer Name: ");
            if (!Validator.isValidName(name)) {
                System.out.println(">> Invalid Name! Customer name cannot be blank.");
            }
        } while (!Validator.isValidName(name));

        // Read and validate 11-digit mobile number starting with 09
        String contact;
        do {
            contact = promptLine("Enter Contact Number (11 digits, starts with 09): ");
            if (!Validator.isValidContact(contact)) {
                System.out.println(">> Invalid Contact! Must be exactly 11 digits starting with 09.");
            }
        } while (!Validator.isValidContact(contact));

        // Read and validate dormitory (select from registered dorms or enter a new custom dorm)
        String dorm = "";
        do {
            if (!dorms.isEmpty()) {
                System.out.println("\nRegistered Dormitories:");
                for (int i = 0; i < dorms.size(); i++) {
                    System.out.printf("  [%d] %s\n", (i + 1), dorms.get(i));
                }
                String dormInput = promptLine("Enter Dormitory Name (select 1-" + dorms.size() + " or type a custom dorm): ");
                if (dormInput.isEmpty()) {
                    System.out.println(">> Invalid Dormitory! Dormitory name cannot be blank.");
                    continue;
                }

                boolean isNumber = false;
                try {
                    int idx = Integer.parseInt(dormInput);
                    isNumber = true;
                    if (idx >= 1 && idx <= dorms.size()) {
                        dorm = dorms.get(idx - 1);
                    } else {
                        System.out.printf(">> Invalid selection! Please enter a number between 1 and %d, or type a custom dorm name.\n", dorms.size());
                    }
                } catch (NumberFormatException e) {
                    // Not a number, treat as custom dorm name
                }

                if (!isNumber && Validator.isValidDorm(dormInput)) {
                    dorm = dormInput.trim();
                    addCustomDorm(dorm);
                }
            } else {
                String dormInput = promptLine("Enter Custom Dormitory Name: ");
                if (Validator.isValidDorm(dormInput)) {
                    dorm = dormInput.trim();
                    addCustomDorm(dorm);
                } else {
                    System.out.println(">> Invalid Dormitory! Dormitory name cannot be blank.");
                }
            }
        } while (dorm.isEmpty());

        // Read and validate room number
        String room;
        do {
            room = promptLine("Enter Room Number: ");
            if (!Validator.isValidRoom(room)) {
                System.out.println(">> Invalid Room! Room number cannot be blank.");
            }
        } while (!Validator.isValidRoom(room));

        // Auto-generate ID and create Customer object
        String customerId = generateCustomerId();
        Customer customer = new Customer(customerId, name, contact, dorm, room);
        customers.add(customer);

        System.out.println("--------------------------------------------------");
        System.out.printf("Customer registered successfully!\nID: %s | Name: %s | Dorm: %s R%s\n",
                customerId, name, dorm, room);
        System.out.println("--------------------------------------------------");
    }

    // =========================================================================
    // MENU OPTION 2: CREATE ORDER
    // =========================================================================

    /**
     * Menu 2: Creates a new laundry order.
     * Looks up customer via linear search, validates weight, calculates loads and bill,
     * chooses receive/return methods and optional deadline, handles payment choice,
     * adds order to the orders list (maintaining ID order), and enqueues into washingQueue.
     */
    public void createOrder() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("                 CREATE NEW ORDER                 ");
        System.out.println("--------------------------------------------------");

        String customerId = promptLine("Enter Customer ID: ");
        Customer customer = SearchUtil.linearSearchCustomer(this.customers, customerId);

        if (customer == null) {
            System.out.println("Customer not found!");
            return;
        }

        System.out.printf("Customer: %s (%s, Room %s)\n",
                customer.getName(), customer.getDorm(), customer.getRoomNumber());

        // Read and validate weight (> 0)
        double weight = 0.0;
        do {
            String weightStr = promptLine("Enter Laundry Weight (kg): ");
            try {
                weight = Double.parseDouble(weightStr);
                if (!Validator.isValidWeight(weight)) {
                    System.out.println("Invalid Weight");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid Weight");
                weight = 0.0;
            }
        } while (!Validator.isValidWeight(weight));

        // Choose Receive Method
        ReceiveMethod receiveMethod = null;
        do {
            System.out.println("\nSelect Receive Method:");
            System.out.println("  [1] Drop-off (Customer brought laundry to shop)");
            System.out.println("  [2] Pickup (Shop picked up from dorm)");
            String rChoice = promptLine("Enter choice (1-2): ");
            if ("1".equals(rChoice)) {
                receiveMethod = ReceiveMethod.DROP_OFF;
            } else if ("2".equals(rChoice)) {
                receiveMethod = ReceiveMethod.PICKUP;
            } else {
                System.out.println(">> Invalid choice! Please enter 1 or 2.");
            }
        } while (receiveMethod == null);

        // Choose Return Method
        ReturnMethod returnMethod = null;
        do {
            System.out.println("\nSelect Return Method:");
            System.out.println("  [1] Claim (Customer picks up from shop)");
            System.out.println("  [2] Delivery (Shop delivers to dorm via trike)");
            String retChoice = promptLine("Enter choice (1-2): ");
            if ("1".equals(retChoice)) {
                returnMethod = ReturnMethod.CLAIM;
            } else if ("2".equals(retChoice)) {
                returnMethod = ReturnMethod.DELIVERY;
            } else {
                System.out.println(">> Invalid choice! Please enter 1 or 2.");
            }
        } while (returnMethod == null);

        // Reject DELIVERY if the order alone exceeds trike capacity
        int previewLoadsCheck = (int) Math.ceil(weight / KG_PER_LOAD);
        if (returnMethod == ReturnMethod.DELIVERY && previewLoadsCheck > TRIKE_CAPACITY) {
            System.out.printf(
                ">> This order is %d loads (%.1f kg), which exceeds the trike capacity of %d loads (%d kg).%n" +
                "   Delivery is not available for oversized orders. Return method changed to CLAIM.%n",
                previewLoadsCheck, weight, TRIKE_CAPACITY, (int)(TRIKE_CAPACITY * KG_PER_LOAD));
            returnMethod = ReturnMethod.CLAIM;
        }

        // Optional Delivery Deadline (only for Delivery)
        LocalTime deliveryDeadline = null;
        if (returnMethod == ReturnMethod.DELIVERY) {
            boolean validDeadline = false;
            do {
                String deadlineInput = promptLine("Enter Optional Delivery Deadline (HH:MM, or press Enter to skip): ");
                if (deadlineInput.isEmpty()) {
                    validDeadline = true; // skipped
                } else if (Validator.isValidDeadline(deadlineInput)) {
                    try {
                        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
                        deliveryDeadline = LocalTime.parse(deadlineInput, fmt);
                        validDeadline = true;
                    } catch (Exception e) {
                        try {
                            DateTimeFormatter flex = DateTimeFormatter.ofPattern("H:mm");
                            deliveryDeadline = LocalTime.parse(deadlineInput, flex);
                            validDeadline = true;
                        } catch (Exception e2) {
                            System.out.println("Invalid Deadline Format");
                        }
                    }
                } else {
                    System.out.println("Invalid Deadline Format");
                }
            } while (!validDeadline);
        }

        // Compute preview of loads and cost
        int previewLoads = (int) Math.ceil(weight / KG_PER_LOAD);
        double previewTotal = previewLoads * PRICE_PER_LOAD;
        System.out.printf("\nOrder Summary: %.1f kg -> %d load(s) | Total: Php %.2f\n",
                weight, previewLoads, previewTotal);

        // Choose Payment: Pay Now or Pay Later
        PaymentStatus paymentStatus = PaymentStatus.UNPAID;
        PaymentMethod paymentMethod = null;
        boolean paymentChosen = false;
        do {
            System.out.println("Payment Option:");
            System.out.println("  [1] Pay Now");
            System.out.println("  [2] Pay Later");
            String payChoice = promptLine("Enter choice (1-2): ");
            if ("1".equals(payChoice)) {
                paymentStatus = PaymentStatus.PAID;
                boolean methodChosen = false;
                do {
                    System.out.println("Select Payment Method:");
                    System.out.println("  [1] Cash");
                    System.out.println("  [2] Online (GCash / Maya)");
                    String mChoice = promptLine("Enter choice (1-2): ");
                    if ("1".equals(mChoice)) {
                        paymentMethod = PaymentMethod.CASH;
                        methodChosen = true;
                    } else if ("2".equals(mChoice)) {
                        paymentMethod = PaymentMethod.ONLINE;
                        methodChosen = true;
                    } else {
                        System.out.println(">> Invalid choice! Please enter 1 or 2.");
                    }
                } while (!methodChosen);
                paymentChosen = true;
            } else if ("2".equals(payChoice)) {
                paymentStatus = PaymentStatus.UNPAID;
                paymentMethod = null;
                paymentChosen = true;
            } else {
                System.out.println(">> Invalid choice! Please enter 1 or 2.");
            }
        } while (!paymentChosen);

        // Generate Order ID, create Order, add to orders list and enqueue in washingQueue
        String orderId = generateOrderId();
        Order order = new Order(orderId, customer, weight, receiveMethod, returnMethod,
                deliveryDeadline, paymentStatus, paymentMethod);

        // Appending to the end maintains ascending Order ID order for Binary Search
        orders.add(order);

        // FIFO Queue insertion
        washingQueue.add(order);

        // Ternary operator natural usage for return description
        String returnDesc = (order.getReturnMethod() == ReturnMethod.CLAIM) ? "shop counter claim" : "trike dorm delivery";

        System.out.println("--------------------------------------------------");
        System.out.printf("Order %s created successfully!\n", orderId);
        System.out.printf("Customer: %s | Weight: %.1f kg (%d loads) | Total: Php %.2f\n",
                customer.getName(), order.getWeightKg(), order.getLoads(), order.getTotalAmount());
        System.out.printf("Dispatch Plan: Scheduled for %s\n", returnDesc);
        System.out.printf("Payment: %s %s\n",
                order.getPaymentStatus(),
                (order.getPaymentMethod() != null ? "(" + order.getPaymentMethod() + ")" : ""));
        System.out.printf("Position in Washing Queue: #%d\n", washingQueue.size());
        System.out.println("--------------------------------------------------");
    }

    // =========================================================================
    // MENU OPTION 3: PROCESS NEXT ORDER
    // =========================================================================

    /**
     * Menu 3: Dequeues the front order from the washingQueue (FIFO) and sets its
     * laundryStatus to PROCESSING.
     */
    public void processNextOrder() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("                PROCESS NEXT ORDER                ");
        System.out.println("--------------------------------------------------");

        if (washingQueue.isEmpty()) {
            System.out.println("No orders in the washing line!");
            return;
        }

        Order order = washingQueue.poll();
        order.setLaundryStatus(LaundryStatus.PROCESSING);

        System.out.printf("Order %s is now PROCESSING!\n", order.getOrderId());
        System.out.printf("Customer: %s | Loads: %d | Remaining in washing line: %d\n",
                order.getCustomer().getName(), order.getLoads(), washingQueue.size());
        System.out.println("--------------------------------------------------");
    }

    // =========================================================================
    // MENU OPTION 4: MARK ORDER READY
    // =========================================================================

    /**
     * Menu 4: Finds an order via binary search. Verifies it is in PROCESSING status,
     * updates its status to READY and sets timeReady to now.
     * Pushes a MARK_READY UndoAction onto the undoStack.
     */
    public void markOrderReady() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("                 MARK ORDER READY                 ");
        System.out.println("--------------------------------------------------");

        String orderId = promptLine("Enter Order ID: ");
        Order order = findOrderById(orderId);

        if (order == null) {
            System.out.println("Order not found!");
            return;
        }

        if (order.getLaundryStatus() != LaundryStatus.PROCESSING) {
            System.out.println("Only orders being processed can be marked Ready!");
            return;
        }

        order.setLaundryStatus(LaundryStatus.READY);
        order.setTimeReady(LocalDateTime.now());

        // Push UndoAction onto LIFO stack
        String desc = String.format("Marked Ready for Order %s (%s)", order.getOrderId(), order.getCustomer().getName());
        undoStack.push(new UndoAction(UndoAction.MARK_READY, order, desc));

        System.out.printf("Order %s is now marked READY!\n", order.getOrderId());
        System.out.printf("Return Plan: %s | Ready at: %s\n",
                order.getReturnMethod(),
                order.getTimeReady().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        System.out.println("--------------------------------------------------");
    }

    // =========================================================================
    // MENU OPTION 5: RECORD PAYMENT
    // =========================================================================

    /**
     * Menu 5: Finds an order via binary search.
     * Validates that the order exists and is not already paid.
     * Prompts for Cash or Online, updates paymentStatus to PAID,
     * pushes a PAYMENT UndoAction onto the undoStack, and prints confirmation.
     */
    public void recordPayment() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("                  RECORD PAYMENT                  ");
        System.out.println("--------------------------------------------------");

        String orderId = promptLine("Enter Order ID: ");
        Order order = findOrderById(orderId);

        if (order == null) {
            System.out.println("Order not found!");
            return;
        }

        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            System.out.println("Order already paid!");
            return;
        }

        System.out.printf("Order %s found. Total Due: Php %.2f\n", order.getOrderId(), order.getTotalAmount());
        PaymentMethod method = null;
        do {
            System.out.println("Select Payment Method:");
            System.out.println("  [1] Cash");
            System.out.println("  [2] Online (GCash / Maya)");
            String choice = promptLine("Enter choice (1-2): ");
            if ("1".equals(choice)) {
                method = PaymentMethod.CASH;
            } else if ("2".equals(choice)) {
                method = PaymentMethod.ONLINE;
            } else {
                System.out.println(">> Invalid choice! Please enter 1 or 2.");
            }
        } while (method == null);

        order.setPaymentStatus(PaymentStatus.PAID);
        order.setPaymentMethod(method);

        // Push UndoAction onto undoStack
        String desc = String.format("Payment of Php %.2f (%s) for Order %s",
                order.getTotalAmount(), method, order.getOrderId());
        undoStack.push(new UndoAction(UndoAction.PAYMENT, order, desc));

        System.out.printf("Payment recorded! Amount: Php %.2f (%s)\n", order.getTotalAmount(), method);
        System.out.println("--------------------------------------------------");
    }

    // =========================================================================
    // MENU OPTION 6: PICKUP REQUESTS (6a / 6b)
    // =========================================================================

    /**
     * Menu 6 Sub-menu: Manages student pickup requests.
     */
    public void managePickupRequests() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("              PICKUP REQUESTS QUEUE               ");
        System.out.println("--------------------------------------------------");
        System.out.println("  [1] Log Pickup Request (Enqueue)");
        System.out.println("  [2] Process Next Pickup (Dequeue)");
        String choice = promptLine("Enter choice (1-2): ");
        if ("1".equals(choice)) {
            logPickupRequest();
        } else if ("2".equals(choice)) {
            processNextPickup();
        } else {
            System.out.println(">> Invalid choice!");
        }
    }

    /**
     * Menu 6a: Enqueues a customer onto pickupQueue (FIFO) and displays their position.
     */
    public void logPickupRequest() {
        System.out.println("\n--- Log Pickup Request ---");
        String customerId = promptLine("Enter Customer ID: ");
        Customer customer = SearchUtil.linearSearchCustomer(this.customers, customerId);

        if (customer == null) {
            System.out.println("Customer not found!");
            return;
        }

        pickupQueue.add(customer);
        System.out.printf("Pickup request logged for %s! Position in queue: #%d\n",
                customer.getName(), pickupQueue.size());
    }

    /**
     * Menu 6b: Dequeues the front customer from pickupQueue and shows dorm, room, and name.
     */
    public void processNextPickup() {
        System.out.println("\n--- Process Next Pickup ---");
        if (pickupQueue.isEmpty()) {
            System.out.println("No pickup requests!");
            return;
        }

        Customer customer = pickupQueue.poll();
        System.out.printf("Pickup Target -> Dorm: %s | Room: %s | Customer: %s (Contact: %s)\n",
                customer.getDorm(), customer.getRoomNumber(), customer.getName(), customer.getContactNumber());
        System.out.printf("Remaining pickup requests in queue: %d\n", pickupQueue.size());
    }

    // =========================================================================
    // MENU OPTION 7: DISPATCH TRIKE (DELIVERY ROUTING & CAPACITY)
    // =========================================================================

    /**
     * Groups dorm-sorted orders into Stop objects in a single pass using nested loops,
     * enforcing the TRIKE_CAPACITY (10 loads maximum per run).
     *
     * @param sortedOrders delivery orders sorted by dorm name
     * @return list of Stop objects fitting within the 10-load limit
     */
    public List<Stop> buildStops(List<Order> sortedOrders) {
        List<Stop> stops = new ArrayList<>();
        int loadCount = 0;
        int i = 0;

        // Outer loop: iterate across distinct dorm groups
        while (i < sortedOrders.size()) {
            Order firstOrder = sortedOrders.get(i);
            String currentDorm = firstOrder.getCustomer().getDorm();

            Stop currentStop = new Stop(currentDorm);

            boolean capacityReached = false;
            // Nested loop: consume all orders belonging to the current dorm
            while (i < sortedOrders.size()) {
                Order order = sortedOrders.get(i);
                if (!order.getCustomer().getDorm().equalsIgnoreCase(currentDorm)) {
                    break; // Moved to the next dormitory; exit inner loop
                }
                // Skip orders that alone exceed the full trike capacity -
                // they can never fit in any run and must not block other dorms
                if (order.getLoads() > TRIKE_CAPACITY) {
                    System.out.printf(
                        ">> Order %s skipped: %d load(s) exceeds trike capacity of %d. Change to CLAIM.%n",
                        order.getOrderId(), order.getLoads(), TRIKE_CAPACITY);
                    i++;
                    continue;
                }
                // Once adding the next order would overflow the trike, stop this run
                // and defer the remaining orders to the next dispatch
                if (loadCount + order.getLoads() > TRIKE_CAPACITY) {
                    System.out.printf(
                        ">> Trike capacity limit reached (%d/%d loads). Remaining orders deferred to next run.%n",
                        loadCount, TRIKE_CAPACITY);
                    capacityReached = true;
                    break;
                }
                currentStop.addOrder(order);
                loadCount += order.getLoads();
                i++;
            }

            if (!currentStop.getOrders().isEmpty()) {
                stops.add(currentStop);
            }

            // If capacity limit stopped grouping or trike is full, stop building stops for this run
            if (capacityReached || loadCount >= TRIKE_CAPACITY) {
                break;
            }
        }

        return stops;
    }

    /**
     * Menu 7: Filters orders that are READY and returnMethod == DELIVERY.
     * Sorts them by dorm using Merge Sort (SortUtil.BY_DORM).
     * Invokes buildStops() to group by dorm and apply 10-load trike limit.
     * Enqueues stops into stopQueue (FIFO), transfers to temporary list,
     * and pushes onto trikeStack (LIFO) in reverse order so the 1st stop pops first.
     * Sets included orders to OUT_FOR_DELIVERY, archives the run in deliveryLog,
     * and displays run summary.
     */
    public void dispatchTrike() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("              DISPATCH DELIVERY TRIKE             ");
        System.out.println("--------------------------------------------------");

        // Guard: refuse to dispatch while a run is still active
        if (!trikeStack.isEmpty()) {
            System.out.printf(
                ">> Cannot dispatch: %d stop(s) are still on the trike from the current run.%n" +
                "   Use option [8] Deliver Next Stop to complete all remaining stops first.%n",
                trikeStack.size());
            return;
        }

        // 1. Filter orders that are READY and DELIVERY
        List<Order> readyDeliveryOrders = new ArrayList<>();
        for (Order o : this.orders) {
            if (o.getLaundryStatus() == LaundryStatus.READY && o.getReturnMethod() == ReturnMethod.DELIVERY) {
                readyDeliveryOrders.add(o);
            }
        }

        if (readyDeliveryOrders.isEmpty()) {
            System.out.println("No orders ready for delivery!");
            return;
        }

        // 2. Merge sort orders grouped by dorm and prioritized by earliest delivery deadline (resolves issue #4)
        List<Order> sortedOrders = SortUtil.mergeSort(readyDeliveryOrders, SortUtil.byDormDeadline(readyDeliveryOrders));

        // 3. Build dorm-grouped stops enforcing the 10-load limit
        List<Stop> stops = buildStops(sortedOrders);
        if (stops.isEmpty()) {
            System.out.println("No stops could be formed within capacity limit.");
            return;
        }

        // 4. Enqueue created stops into stopQueue (FIFO)
        for (Stop s : stops) {
            stopQueue.add(s);
        }

        // 5. Transfer stops from stopQueue to a temporary list
        List<Stop> tempStops = new ArrayList<>();
        while (!stopQueue.isEmpty()) {
            tempStops.add(stopQueue.poll());
        }

        // 6. Push stops onto trikeStack in reverse order (LIFO) so 1st stop ends up on top
        for (int k = tempStops.size() - 1; k >= 0; k--) {
            trikeStack.push(tempStops.get(k));
        }

        // 7. Update status of included orders to OUT_FOR_DELIVERY
        int totalRunLoads = 0;
        int totalRunOrders = 0;
        for (Stop s : tempStops) {
            totalRunLoads += s.getTotalLoads();
            for (Order o : s.getOrders()) {
                o.setLaundryStatus(LaundryStatus.OUT_FOR_DELIVERY);
                totalRunOrders++;
            }
        }

        // 8. Add dispatched run to deliveryLog
        deliveryLog.add(new ArrayList<>(tempStops));

        // 9. Display run summary
        System.out.println("--------------------------------------------------");
        System.out.printf("Tricycle Dispatched Successfully! (Run #%d)\n", deliveryLog.size());
        System.out.printf("Total Stops: %d | Total Orders: %d | Total Loads: %d / %d loads\n",
                tempStops.size(), totalRunOrders, totalRunLoads, TRIKE_CAPACITY);
        System.out.println("Delivery Route Order (Prioritized by Earliest Deadline):");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        int stopNum = 1;
        for (Stop s : tempStops) {
            String targetTime = (s.getEarliestDeadline() != null)
                    ? s.getEarliestDeadline().format(timeFmt)
                    : "No deadline";
            System.out.printf("  Stop %d: %s [Target Deadline: %s] (%d load(s), %d order(s))\n",
                    stopNum++, s.getDorm(), targetTime, s.getTotalLoads(), s.getOrders().size());
            for (Order o : s.getOrders()) {
                String oDeadline = (o.getDeliveryDeadline() != null)
                        ? o.getDeliveryDeadline().format(timeFmt)
                        : "None";
                System.out.printf("    - [%s] %s | Room %s | Deadline: %s | Php %.2f | %s\n",
                        o.getOrderId(), o.getCustomer().getName(), o.getCustomer().getRoomNumber(),
                        oDeadline, o.getTotalAmount(), o.getPaymentStatus());
            }
        }
        System.out.println("--------------------------------------------------");
    }

    // =========================================================================
    // MENU OPTION 8: DELIVER NEXT STOP
    // =========================================================================

    /**
     * Menu 8: Pops the top stop from trikeStack (LIFO).
     * Displays dorm and target rooms.
     * For each order: if UNPAID, collects CASH payment; marks order COMPLETED.
     */
    public void deliverNextStop() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("                 DELIVER NEXT STOP                ");
        System.out.println("--------------------------------------------------");

        if (trikeStack.isEmpty()) {
            System.out.println("No active delivery run!");
            return;
        }

        // Pop top stop from LIFO trikeStack
        Stop stop = trikeStack.pop();

        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        String targetTime = (stop.getEarliestDeadline() != null)
                ? stop.getEarliestDeadline().format(timeFmt)
                : "No deadline";
        System.out.printf("Arrived at Stop: %s (Target Deadline: %s)\n", stop.getDorm(), targetTime);
        System.out.printf("Orders to deliver: %d | Loads: %d\n", stop.getOrders().size(), stop.getTotalLoads());
        System.out.println("--------------------------------------------------");

        for (Order o : stop.getOrders()) {
            Customer c = o.getCustomer();
            System.out.printf("Delivering Order %s to Room %s (%s)...\n",
                    o.getOrderId(), c.getRoomNumber(), c.getName());

            if (o.getPaymentStatus() == PaymentStatus.UNPAID) {
                System.out.printf(">> Collect Php %.2f from Room %s\n", o.getTotalAmount(), c.getRoomNumber());
                o.setPaymentStatus(PaymentStatus.PAID);
                o.setPaymentMethod(PaymentMethod.CASH);
                System.out.printf("   [Payment Received] Php %.2f (Cash recorded)\n", o.getTotalAmount());
            } else {
                System.out.printf("   [Prepaid] Php %.2f via %s\n", o.getTotalAmount(), o.getPaymentMethod());
            }

            o.setLaundryStatus(LaundryStatus.COMPLETED);
        }

        System.out.printf("\nStop completed! %d order(s) delivered to %s.\n",
                stop.getOrders().size(), stop.getDorm());
        if (trikeStack.isEmpty()) {
            System.out.println(">> Entire delivery run has been successfully completed!");
        } else {
            System.out.printf(">> %d stop(s) remaining on the trike.\n", trikeStack.size());
        }
        System.out.println("--------------------------------------------------");
    }

    // =========================================================================
    // MENU OPTION 9: CLAIM ORDER
    // =========================================================================

    /**
     * Menu 9: Claims a ready order at the shop counter.
     * Order must be in READY status and have ReturnMethod CLAIM.
     * If unpaid, collects payment first, then marks COMPLETED.
     */
    public void claimOrder() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("                    CLAIM ORDER                   ");
        System.out.println("--------------------------------------------------");

        String orderId = promptLine("Enter Order ID: ");
        Order order = findOrderById(orderId);

        if (order == null) {
            System.out.println("Order not found!");
            return;
        }

        if (order.getLaundryStatus() != LaundryStatus.READY) {
            System.out.printf("Order is not ready for claim! Current status: %s\n", order.getLaundryStatus());
            return;
        }

        if (order.getReturnMethod() != ReturnMethod.CLAIM) {
            System.out.println("This order is designated for DELIVERY, not shop counter claim!");
            return;
        }

        // If unpaid, record payment first
        if (order.getPaymentStatus() == PaymentStatus.UNPAID) {
            System.out.printf("Order is UNPAID. Total Amount Due: Php %.2f\n", order.getTotalAmount());
            PaymentMethod method = null;
            do {
                System.out.println("Select Payment Method:");
                System.out.println("  [1] Cash");
                System.out.println("  [2] Online");
                String choice = promptLine("Enter choice (1-2): ");
                if ("1".equals(choice)) {
                    method = PaymentMethod.CASH;
                } else if ("2".equals(choice)) {
                    method = PaymentMethod.ONLINE;
                } else {
                    System.out.println(">> Invalid choice! Please enter 1 or 2.");
                }
            } while (method == null);

            order.setPaymentStatus(PaymentStatus.PAID);
            order.setPaymentMethod(method);

            String desc = String.format("Payment of Php %.2f on claim for Order %s",
                    order.getTotalAmount(), order.getOrderId());
            undoStack.push(new UndoAction(UndoAction.PAYMENT, order, desc));
            System.out.printf("Payment recorded! Amount: Php %.2f\n", order.getTotalAmount());
        }

        // Mark order completed
        order.setLaundryStatus(LaundryStatus.COMPLETED);
        System.out.printf("Order %s has been successfully claimed by %s and marked COMPLETED!\n",
                order.getOrderId(), order.getCustomer().getName());
        System.out.println("--------------------------------------------------");
    }

    // =========================================================================
    // MENU OPTION 10: SEARCH (10a / 10b)
    // =========================================================================

    /**
     * Menu 10 Sub-menu: Coordinates customer and order searches.
     */
    public void searchMenu() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("                  SEARCH RECORDS                  ");
        System.out.println("--------------------------------------------------");
        System.out.println("  [1] Search Customer (by ID, Name, or Contact)");
        System.out.println("  [2] Search Order (by Order ID)");
        String choice = promptLine("Enter choice (1-2): ");
        if ("1".equals(choice)) {
            searchCustomer();
        } else if ("2".equals(choice)) {
            searchOrder();
        } else {
            System.out.println(">> Invalid choice!");
        }
    }

    /**
     * Menu 10a: Linear search for customer by ID, Name, or Contact number.
     */
    public void searchCustomer() {
        System.out.println("\n--- Search Customer ---");
        String query = promptLine("Enter Customer ID, Name, or Contact Number: ");
        Customer c = SearchUtil.linearSearchCustomer(this.customers, query);

        if (c == null) {
            System.out.println("Customer not found!");
            return;
        }

        System.out.println("--------------------------------------------------");
        System.out.println("Customer Record Found:");
        System.out.printf("  Customer ID:    %s\n", c.getCustomerId());
        System.out.printf("  Name:           %s\n", c.getName());
        System.out.printf("  Contact Number: %s\n", c.getContactNumber());
        System.out.printf("  Dormitory:      %s\n", c.getDorm());
        System.out.printf("  Room Number:    %s\n", c.getRoomNumber());

        // Display associated orders
        System.out.println("  Order History:");
        boolean hasOrders = false;
        for (Order o : this.orders) {
            if (o.getCustomer().getCustomerId().equalsIgnoreCase(c.getCustomerId())) {
                hasOrders = true;
                System.out.printf("    - [%s] %.1f kg (%d loads) | Php %.2f | %s | %s\n",
                        o.getOrderId(), o.getWeightKg(), o.getLoads(), o.getTotalAmount(),
                        o.getLaundryStatus(), o.getPaymentStatus());
            }
        }
        if (!hasOrders) {
            System.out.println("    (No orders recorded for this customer yet)");
        }
        System.out.println("--------------------------------------------------");
    }

    /**
     * Menu 10b: Binary search for order by Order ID.
     */
    public void searchOrder() {
        System.out.println("\n--- Search Order ---");
        String orderId = promptLine("Enter Order ID: ");
        Order o = findOrderById(orderId);

        if (o == null) {
            System.out.println("Order not found!");
            return;
        }

        DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

        System.out.println("--------------------------------------------------");
        System.out.println("Order Details Found:");
        System.out.printf("  Order ID:          %s\n", o.getOrderId());
        System.out.printf("  Customer:          %s (ID: %s)\n", o.getCustomer().getName(), o.getCustomer().getCustomerId());
        System.out.printf("  Address:           %s, Room %s\n", o.getCustomer().getDorm(), o.getCustomer().getRoomNumber());
        System.out.printf("  Weight & Loads:    %.1f kg -> %d load(s)\n", o.getWeightKg(), o.getLoads());
        System.out.printf("  Total Amount:      Php %.2f\n", o.getTotalAmount());
        System.out.printf("  Receive Method:    %s\n", o.getReceiveMethod());
        System.out.printf("  Return Method:     %s\n", o.getReturnMethod());
        System.out.printf("  Delivery Deadline: %s\n", (o.getDeliveryDeadline() != null ? o.getDeliveryDeadline().format(timeFmt) : "None"));
        System.out.printf("  Laundry Status:    %s\n", o.getLaundryStatus());
        System.out.printf("  Payment Status:    %s (%s)\n", o.getPaymentStatus(), (o.getPaymentMethod() != null ? o.getPaymentMethod() : "N/A"));
        System.out.printf("  Time Received:     %s\n", (o.getTimeReceived() != null ? o.getTimeReceived().format(dtFmt) : "N/A"));
        System.out.printf("  Time Ready:        %s\n", (o.getTimeReady() != null ? o.getTimeReady().format(dtFmt) : "Not yet ready"));
        System.out.println("--------------------------------------------------");
    }

    // =========================================================================
    // MENU OPTION 11: VIEW ORDERS (MERGE SORT ON COPY)
    // =========================================================================

    /**
     * Menu 11: Prompts for a sort key, performs recursive Merge Sort on a fresh
     * copy of the orders list (preserving the original list's Order ID order),
     * and prints a formatted tabular view.
     */
    public void viewOrders() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("                   VIEW ORDERS                    ");
        System.out.println("--------------------------------------------------");

        if (this.orders.isEmpty()) {
            System.out.println("No orders recorded in the system yet.");
            return;
        }

        System.out.println("Select Sort Key:");
        System.out.println("  [1] Total Amount (Ascending)");
        System.out.println("  [2] Customer Name (A-Z)");
        System.out.println("  [3] Time Received (Oldest to Newest)");
        System.out.println("  [4] Delivery Deadline (Earliest to Latest)");
        System.out.println("  [5] Dormitory Name (A-Z)");

        Comparator<Order> chosenComparator = null;
        String keyChoice = promptLine("Enter choice (1-5): ");

        switch (keyChoice) {
            case "1":
                chosenComparator = SortUtil.BY_TOTAL_AMOUNT;
                break;
            case "2":
                chosenComparator = SortUtil.BY_CUSTOMER_NAME;
                break;
            case "3":
                chosenComparator = SortUtil.BY_TIME_RECEIVED;
                break;
            case "4":
                chosenComparator = SortUtil.BY_DELIVERY_DEADLINE;
                break;
            case "5":
                chosenComparator = SortUtil.BY_DORM;
                break;
            default:
                System.out.println(">> Invalid key selection! Defaulting to Order ID order.");
                break;
        }

        // Always sort a COPY to maintain binary-searchability of the original orders list
        List<Order> copy = new ArrayList<>(this.orders);
        List<Order> displayList = (chosenComparator != null) ? SortUtil.mergeSort(copy, chosenComparator) : copy;

        printOrdersTable(displayList);
    }

    /**
     * Formats and prints a list of orders in an ASCII tabular structure.
     *
     * @param list list of orders to print
     */
    private void printOrdersTable(List<Order> list) {
        System.out.println("\n" + "=".repeat(120));
        System.out.printf("%-9s | %-16s | %-18s | %-5s | %-5s | %-9s | %-10s | %-16s | %-8s | %-8s\n",
                "Order ID", "Customer", "Dormitory & Room", "Kg", "Loads", "Total Php", "Return", "Status", "Payment", "Deadline");
        System.out.println("-".repeat(120));

        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        for (Order o : list) {
            String customerName = o.getCustomer().getName();
            if (customerName.length() > 16) customerName = customerName.substring(0, 13) + "...";

            String dormRoom = o.getCustomer().getDorm() + " R" + o.getCustomer().getRoomNumber();
            if (dormRoom.length() > 18) dormRoom = dormRoom.substring(0, 15) + "...";

            String deadline = (o.getDeliveryDeadline() != null) ? o.getDeliveryDeadline().format(timeFmt) : "--";

            System.out.printf("%-9s | %-16s | %-18s | %5.1f | %5d | %9.2f | %-10s | %-16s | %-8s | %-8s\n",
                    o.getOrderId(), customerName, dormRoom,
                    o.getWeightKg(), o.getLoads(), o.getTotalAmount(),
                    o.getReturnMethod(), o.getLaundryStatus(), o.getPaymentStatus(), deadline);
        }
        System.out.println("=".repeat(120));
        System.out.printf("Total Orders Shown: %d\n", list.size());
    }

    // =========================================================================
    // MENU OPTION 12: SYSTEM REPORTS (12a to 12e)
    // =========================================================================

    /**
     * Menu 12 Sub-menu: Coordinates all 5 operational reports.
     */
    public void showReports() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("                  SYSTEM REPORTS                  ");
        System.out.println("--------------------------------------------------");
        System.out.println("  [1] Pending and Ready Orders (12a)");
        System.out.println("  [2] Unpaid Orders (12b)");
        System.out.println("  [3] Daily Sales Summary (12c)");
        System.out.println("  [4] Dispatched Delivery Runs Log (12d)");
        System.out.println("  [5] Late Ready Orders (> 3 hours) (12e)");
        System.out.println("  [6] View All Reports Sequentially");
        String choice = promptLine("Select report (1-6): ");

        switch (choice) {
            case "1": reportPendingAndReady(); break;
            case "2": reportUnpaid(); break;
            case "3": reportDailySales(); break;
            case "4": reportDeliveryRuns(); break;
            case "5": reportLateOrders(); break;
            case "6":
                reportPendingAndReady();
                reportUnpaid();
                reportDailySales();
                reportDeliveryRuns();
                reportLateOrders();
                break;
            default:
                System.out.println(">> Invalid choice!");
                break;
        }
    }

    /**
     * Report 12a: Displays pending (Waiting, Processing) and Ready orders.
     * Includes elapsed waiting time for READY orders calculated via Duration.
     */
    public void reportPendingAndReady() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("      REPORT 12a: PENDING AND READY ORDERS        ");
        System.out.println("--------------------------------------------------");

        List<Order> active = new ArrayList<>();
        for (Order o : this.orders) {
            if (o.getLaundryStatus() == LaundryStatus.WAITING ||
                o.getLaundryStatus() == LaundryStatus.PROCESSING ||
                o.getLaundryStatus() == LaundryStatus.READY) {
                active.add(o);
            }
        }

        if (active.isEmpty()) {
            System.out.println("No pending or ready orders at this time.");
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        System.out.printf("%-9s | %-16s | %-10s | %-12s | %-18s\n",
                "Order ID", "Customer", "Loads", "Status", "Waiting / Elapsed");
        System.out.println("-".repeat(75));

        for (Order o : active) {
            String elapsed = "In Washing Line";
            if (o.getLaundryStatus() == LaundryStatus.PROCESSING) {
                elapsed = "Currently Washing";
            } else if (o.getLaundryStatus() == LaundryStatus.READY && o.getTimeReady() != null) {
                Duration d = Duration.between(o.getTimeReady(), now);
                long hours = d.toHours();
                long minutes = d.toMinutesPart();
                elapsed = String.format("Ready for %dh %02dm", hours, minutes);
            }
            System.out.printf("%-9s | %-16s | %-10d | %-12s | %-18s\n",
                    o.getOrderId(), o.getCustomer().getName(), o.getLoads(), o.getLaundryStatus(), elapsed);
        }
        System.out.println("--------------------------------------------------");
    }

    /**
     * Report 12b: Displays all unpaid orders with customer dorm details and calculates
     * total outstanding accounts receivable.
     */
    public void reportUnpaid() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("            REPORT 12b: UNPAID ORDERS             ");
        System.out.println("--------------------------------------------------");

        double totalUnpaid = 0.0;
        int unpaidCount = 0;

        System.out.printf("%-9s | %-16s | %-20s | %-10s | %-10s\n",
                "Order ID", "Customer", "Dorm & Room", "Loads", "Amount Due");
        System.out.println("-".repeat(75));

        for (Order o : this.orders) {
            if (o.getPaymentStatus() == PaymentStatus.UNPAID) {
                unpaidCount++;
                totalUnpaid += o.getTotalAmount();
                System.out.printf("%-9s | %-16s | %-20s | %-10d | Php %8.2f\n",
                        o.getOrderId(), o.getCustomer().getName(),
                        o.getCustomer().getDorm() + " R" + o.getCustomer().getRoomNumber(),
                        o.getLoads(), o.getTotalAmount());
            }
        }

        if (unpaidCount == 0) {
            System.out.println("All orders are paid! No outstanding balances.");
        } else {
            System.out.println("-".repeat(75));
            System.out.printf("Total Unpaid Orders: %d | Total Outstanding Balance: Php %.2f\n",
                    unpaidCount, totalUnpaid);
        }
        System.out.println("--------------------------------------------------");
    }

    /**
     * Report 12c: Displays daily sales totals split between Cash and Online payments.
     */
    public void reportDailySales() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("          REPORT 12c: DAILY SALES REPORT          ");
        System.out.println("--------------------------------------------------");

        double cashTotal = 0.0;
        double onlineTotal = 0.0;
        int paidCount = 0;

        for (Order o : this.orders) {
            if (o.getPaymentStatus() == PaymentStatus.PAID) {
                paidCount++;
                if (o.getPaymentMethod() == PaymentMethod.CASH) {
                    cashTotal += o.getTotalAmount();
                } else if (o.getPaymentMethod() == PaymentMethod.ONLINE) {
                    onlineTotal += o.getTotalAmount();
                }
            }
        }

        double grandTotal = cashTotal + onlineTotal;
        System.out.printf("Total Paid Orders:       %d\n", paidCount);
        System.out.printf("Total Cash Payments:     Php %10.2f\n", cashTotal);
        System.out.printf("Total Online Payments:   Php %10.2f\n", onlineTotal);
        System.out.println("-".repeat(45));
        System.out.printf("GRAND TOTAL SALES:       Php %10.2f\n", grandTotal);
        System.out.println("--------------------------------------------------");
    }

    /**
     * Report 12d: Displays all tricycle delivery runs dispatched today from deliveryLog.
     */
    public void reportDeliveryRuns() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("       REPORT 12d: DISPATCHED DELIVERY RUNS       ");
        System.out.println("--------------------------------------------------");

        if (deliveryLog.isEmpty()) {
            System.out.println("No delivery runs dispatched yet today.");
            return;
        }

        int runNum = 1;
        for (List<Stop> run : deliveryLog) {
            int runLoads = 0;
            int runOrders = 0;
            System.out.printf("=== RUN #%d === (Stops: %d)\n", runNum++, run.size());
            int stopNum = 1;
            for (Stop s : run) {
                runLoads += s.getTotalLoads();
                runOrders += s.getOrders().size();
                System.out.printf("  Stop %d: %s (Total Loads: %d)\n", stopNum++, s.getDorm(), s.getTotalLoads());
                for (Order o : s.getOrders()) {
                    System.out.printf("    - [%s] %s (Room %s) | %.1f kg | Php %.2f | %s\n",
                            o.getOrderId(), o.getCustomer().getName(), o.getCustomer().getRoomNumber(),
                            o.getWeightKg(), o.getTotalAmount(), o.getPaymentStatus());
                }
            }
            System.out.printf("  Run Totals: %d order(s) | %d load(s)\n\n", runOrders, runLoads);
        }
        System.out.println("--------------------------------------------------");
    }

    /**
     * Report 12e: Identifies ready orders waiting longer than LATE_HOURS (3 hours)
     * using java.time.Duration.
     */
    public void reportLateOrders() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("    REPORT 12e: LATE READY ORDERS (> 3 HOURS)     ");
        System.out.println("--------------------------------------------------");

        LocalDateTime now = LocalDateTime.now();
        int lateCount = 0;

        System.out.printf("%-9s | %-16s | %-18s | %-10s | %-18s\n",
                "Order ID", "Customer", "Dormitory", "Ready Time", "Waiting Duration");
        System.out.println("-".repeat(80));

        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        for (Order o : this.orders) {
            if (o.getLaundryStatus() == LaundryStatus.READY && o.getTimeReady() != null) {
                Duration duration = Duration.between(o.getTimeReady(), now);
                long hours = duration.toHours();
                if (hours >= LATE_HOURS) {
                    lateCount++;
                    long minutes = duration.toMinutesPart();
                    String durStr = String.format("%dh %02dm (LATE)", hours, minutes);
                    System.out.printf("%-9s | %-16s | %-18s | %-10s | %-18s\n",
                            o.getOrderId(), o.getCustomer().getName(),
                            o.getCustomer().getDorm(),
                            o.getTimeReady().format(timeFmt), durStr);
                }
            }
        }

        if (lateCount == 0) {
            System.out.println("No late orders! All ready laundry orders are within the 3-hour window.");
        } else {
            System.out.println("-".repeat(80));
            System.out.printf("Total Flagged Late Orders: %d (exceeded %d-hour SLA threshold)\n",
                    lateCount, LATE_HOURS);
        }
        System.out.println("--------------------------------------------------");
    }

    // =========================================================================
    // MENU OPTION 13: UNDO LAST ACTION
    // =========================================================================

    /**
     * Menu 13: Pops the top UndoAction from undoStack (LIFO).
     * Reverses PAYMENT (reverts to UNPAID and clears method) or
     * MARK_READY (reverts to PROCESSING and clears timeReady).
     */
    public void undoLastAction() {
        System.out.println("\n--------------------------------------------------");
        System.out.println("                 UNDO LAST ACTION                 ");
        System.out.println("--------------------------------------------------");

        if (undoStack.isEmpty()) {
            System.out.println("Nothing to undo!");
            return;
        }

        UndoAction action = undoStack.pop();
        Order order = action.getOrder();

        if (order == null) {
            System.out.println("Cannot undo: order record not found.");
            return;
        }

        if (UndoAction.MARK_READY.equals(action.getType())) {
            // Only allow undo if current status matches what Mark Ready produced (READY)
            if (order.getLaundryStatus() != LaundryStatus.READY) {
                System.out.printf(">> Cannot undo Mark Ready: Order %s status has changed to %s (expected READY).\n",
                        order.getOrderId(), order.getLaundryStatus());
                return;
            }
            order.setLaundryStatus(LaundryStatus.PROCESSING);
            order.setTimeReady(null);
        } else if (UndoAction.PAYMENT.equals(action.getType())) {
            // Only allow undo if the order is still PAID and its status matches what it was when logged
            if (order.getPaymentStatus() != PaymentStatus.PAID) {
                System.out.printf(">> Cannot undo payment: Order %s is not marked PAID.\n",
                        order.getOrderId());
                return;
            }
            if (order.getLaundryStatus() == LaundryStatus.COMPLETED ||
                    order.getLaundryStatus() == LaundryStatus.OUT_FOR_DELIVERY ||
                    order.getLaundryStatus() != action.getExpectedLaundryStatus()) {
                System.out.printf(">> Cannot undo payment: Order %s status has changed to %s.\n",
                        order.getOrderId(), order.getLaundryStatus());
                return;
            }
            order.setPaymentStatus(PaymentStatus.UNPAID);
            order.setPaymentMethod(null);
        }

        System.out.printf("Last action undone: %s\n", action.getDescription());
        System.out.println("--------------------------------------------------");
    }

    // =========================================================================
    // MENU OPTION 14: LOAD SAMPLE DATA
    // =========================================================================

    /**
     * Menu 14: Loads realistic sample data for QMAR Laundry Shop.
     * Creates 5 students and 10 orders across diverse statuses (Waiting, Processing,
     * Ready for Delivery in different dorms, Ready for Claim, Paid, Unpaid, Completed).
     * Ensures ready delivery orders exceed 10 loads in total to test the trike capacity cap.
     */
    public void loadSampleData() {
        System.out.println("\nLoading realistic sample data for QMAR Laundry Shop...");

        // Clear existing state to start clean demonstration
        customers.clear();
        orders.clear();
        dorms.clear();
        washingQueue.clear();
        pickupQueue.clear();
        stopQueue.clear();
        trikeStack.clear();
        undoStack.clear();
        deliveryLog.clear();

        // 5 Customers in Lipa City student dormitories
        Customer c1 = new Customer("C001", "Juan Dela Cruz", "09171234567", "Villa Lourdes Dorm", "101");
        Customer c2 = new Customer("C002", "Maria Clara", "09182345678", "Green Residences", "204");
        Customer c3 = new Customer("C003", "Crisostomo Ibarra", "09193456789", "St. Joseph Hall", "312");
        Customer c4 = new Customer("C004", "Elias Santos", "09204567890", "Lipa Heights Dorm", "105");
        Customer c5 = new Customer("C005", "Salome Rivera", "09215678901", "De La Salle Dorm", "402");

        customers.add(c1);
        customers.add(c2);
        customers.add(c3);
        customers.add(c4);
        customers.add(c5);
        this.customerCounter = 5;

        // Register custom dorms from sample customers
        addCustomDorm(c1.getDorm());
        addCustomDorm(c2.getDorm());
        addCustomDorm(c3.getDorm());
        addCustomDorm(c4.getDorm());
        addCustomDorm(c5.getDorm());

        LocalDateTime now = LocalDateTime.now();

        // Orders kept strictly in ascending Order ID order for Binary Search:
        // LDY0001: Completed, Claim, Paid Cash
        Order o1 = new Order("LDY0001", c1, 7.0, ReceiveMethod.DROP_OFF, ReturnMethod.CLAIM, null,
                LaundryStatus.COMPLETED, PaymentStatus.PAID, PaymentMethod.CASH,
                now.minusHours(6), now.minusHours(4));

        // LDY0002: Waiting in washingQueue, Claim, Unpaid
        Order o2 = new Order("LDY0002", c2, 6.5, ReceiveMethod.DROP_OFF, ReturnMethod.CLAIM, null,
                LaundryStatus.WAITING, PaymentStatus.UNPAID, null,
                now.minusHours(3), null);

        // LDY0003: Waiting in washingQueue, Delivery (Deadline 16:00), Paid Online
        Order o3 = new Order("LDY0003", c3, 13.5, ReceiveMethod.PICKUP, ReturnMethod.DELIVERY, LocalTime.of(16, 0),
                LaundryStatus.WAITING, PaymentStatus.PAID, PaymentMethod.ONLINE,
                now.minusHours(2).minusMinutes(30), null);

        // LDY0004: Processing, Claim, Paid Cash
        Order o4 = new Order("LDY0004", c4, 7.0, ReceiveMethod.DROP_OFF, ReturnMethod.CLAIM, null,
                LaundryStatus.PROCESSING, PaymentStatus.PAID, PaymentMethod.CASH,
                now.minusHours(2), null);

        // LDY0005: Ready for Claim, Unpaid (Tests claim payment flow)
        Order o5 = new Order("LDY0005", c5, 14.0, ReceiveMethod.DROP_OFF, ReturnMethod.CLAIM, null,
                LaundryStatus.READY, PaymentStatus.UNPAID, null,
                now.minusHours(4), now.minusMinutes(45));

        // Ready for Delivery orders designed to test Dorm Grouping & 10-Load Trike Cap:
        // LDY0006: Villa Lourdes Dorm (c1), 21.0 kg -> 3 loads, Delivery, Paid Cash, Ready 4 hours ago (Late!)
        Order o6 = new Order("LDY0006", c1, 21.0, ReceiveMethod.DROP_OFF, ReturnMethod.DELIVERY, LocalTime.of(15, 0),
                LaundryStatus.READY, PaymentStatus.PAID, PaymentMethod.CASH,
                now.minusHours(5), now.minusHours(4)); // Ready for 4 hrs -> Flagged Late (>3h)

        // LDY0007: Villa Lourdes Dorm (c1), 14.0 kg -> 2 loads, Delivery, Unpaid, Ready 1 hour ago
        Order o7 = new Order("LDY0007", c1, 14.0, ReceiveMethod.PICKUP, ReturnMethod.DELIVERY, LocalTime.of(17, 30),
                LaundryStatus.READY, PaymentStatus.UNPAID, null,
                now.minusHours(2), now.minusHours(1));

        // LDY0008: Green Residences (c2), 28.0 kg -> 4 loads, Delivery, Paid Online, Ready 2 hours ago
        Order o8 = new Order("LDY0008", c2, 28.0, ReceiveMethod.DROP_OFF, ReturnMethod.DELIVERY, LocalTime.of(14, 0),
                LaundryStatus.READY, PaymentStatus.PAID, PaymentMethod.ONLINE,
                now.minusHours(3), now.minusHours(2));

        // LDY0009: St. Joseph Hall (c3), 21.0 kg -> 3 loads, Delivery, Unpaid, Ready 1.5 hours ago
        Order o9 = new Order("LDY0009", c3, 21.0, ReceiveMethod.PICKUP, ReturnMethod.DELIVERY, LocalTime.of(18, 0),
                LaundryStatus.READY, PaymentStatus.UNPAID, null,
                now.minusHours(2), now.minusMinutes(90));

        // LDY0010: Lipa Heights Dorm (c4), 14.0 kg -> 2 loads, Delivery, Paid Cash, Ready 30 mins ago
        Order o10 = new Order("LDY0010", c4, 14.0, ReceiveMethod.DROP_OFF, ReturnMethod.DELIVERY, null,
                LaundryStatus.READY, PaymentStatus.PAID, PaymentMethod.CASH,
                now.minusHours(1), now.minusMinutes(30));

        // Note on Ready Delivery loads:
        // Villa Lourdes (o6 + o7) = 3 + 2 = 5 loads
        // Green Residences (o8) = 4 loads
        // St. Joseph Hall (o9) = 3 loads
        // Lipa Heights (o10) = 2 loads
        // Total ready delivery loads = 14 loads. Since TRIKE_CAPACITY = 10,
        // the first run can only carry up to 10 loads, leaving the rest ready for next run!

        orders.add(o1);
        orders.add(o2);
        orders.add(o3);
        orders.add(o4);
        orders.add(o5);
        orders.add(o6);
        orders.add(o7);
        orders.add(o8);
        orders.add(o9);
        orders.add(o10);
        this.orderCounter = 10;

        // Populate queues appropriately
        washingQueue.add(o2);
        washingQueue.add(o3);

        // Populate pickup queue with a pending student pickup
        pickupQueue.add(c3);

        System.out.println("Sample data loaded successfully!");
        System.out.printf("  Registered Customers: %d\n", customers.size());
        System.out.printf("  Orders in System: %d\n", orders.size());
        System.out.printf("  Orders in Washing Queue: %d\n", washingQueue.size());
        System.out.printf("  Customers in Pickup Queue: %d\n", pickupQueue.size());
        System.out.println("  Ready Delivery Orders: 5 orders totaling 14 loads (exceeds 10-load trike limit).");
        System.out.println("--------------------------------------------------");
    }

    // =========================================================================
    // GETTERS FOR TESTING AND STAGE 3/4 INTEGRATION
    // =========================================================================

    public List<Customer> getCustomers() { return customers; }
    public List<Order> getOrders() { return orders; }
    public Queue<Order> getWashingQueue() { return washingQueue; }
    public Queue<Customer> getPickupQueue() { return pickupQueue; }
    public Queue<Stop> getStopQueue() { return stopQueue; }
    public Deque<Stop> getTrikeStack() { return trikeStack; }
    public Deque<UndoAction> getUndoStack() { return undoStack; }
    public List<List<Stop>> getDeliveryLog() { return deliveryLog; }
    public int getCustomerCounter() { return customerCounter; }
    public int getOrderCounter() { return orderCounter; }
    public Scanner getScanner() { return scanner; }
    public void setScanner(Scanner scanner) { this.scanner = scanner; }
}
