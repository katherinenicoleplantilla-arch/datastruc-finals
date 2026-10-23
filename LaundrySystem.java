import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
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
    public static final String[] DORMS = Validator.DORMS; // Lipa City student dormitories

    // --- Data Structures ---
    private List<Customer> customers;          // All registered customers (ArrayList)
    private List<Order> orders;                // All orders, stored in Order ID order (ArrayList)
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
     * Helper to read a trimmed line of text from the console.
     *
     * @param prompt prompt message to display
     * @return trimmed user input
     */
    private String promptLine(String prompt) {
        System.out.print(prompt);
        if (scanner.hasNextLine()) {
            return scanner.nextLine().trim();
        }
        return "";
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

        // Display known dormitories and validate selection
        System.out.println("\nAvailable Dormitories in Lipa City:");
        for (int i = 0; i < DORMS.length; i++) {
            System.out.printf("  [%d] %s\n", (i + 1), DORMS[i]);
        }

        String dorm = "";
        do {
            String dormInput = promptLine("Select Dormitory (enter number 1-" + DORMS.length + " or full name): ");
            // Check if entered as a 1-based index
            try {
                int idx = Integer.parseInt(dormInput);
                if (idx >= 1 && idx <= DORMS.length) {
                    dorm = DORMS[idx - 1];
                }
            } catch (NumberFormatException ignored) {
                // Not a number, check name string
            }
            if (dorm.isEmpty() && Validator.isValidDorm(dormInput)) {
                dorm = dormInput;
            }
            if (dorm.isEmpty()) {
                System.out.println(">> Invalid Dormitory! Please pick from the list of known dorms.");
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
                // Choose Cash or Online
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

        if (UndoAction.PAYMENT.equals(action.getType())) {
            order.setPaymentStatus(PaymentStatus.UNPAID);
            order.setPaymentMethod(null);
        } else if (UndoAction.MARK_READY.equals(action.getType())) {
            order.setLaundryStatus(LaundryStatus.PROCESSING);
            order.setTimeReady(null);
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
    // GETTERS FOR TESTING AND STAGE 3 INTEGRATION
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
