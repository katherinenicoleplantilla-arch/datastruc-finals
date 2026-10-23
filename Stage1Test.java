import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Temporary test harness for Stage 1.
 * Tests enums, models, validation rules, sorting comparators, and search algorithms.
 */
public class Stage1Test {

    private static int testsPassed = 0;
    private static int testsTotal = 0;

    private static void assertTrue(String message, boolean condition) {
        testsTotal++;
        if (condition) {
            testsPassed++;
            System.out.println("  [PASS] " + message);
        } else {
            System.err.println("  [FAIL] " + message);
        }
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("       RUNNING STAGE 1 COMPREHENSIVE TESTS        ");
        System.out.println("==================================================");

        testValidator();
        testOrderCalculations();
        testStopAndUndoAction();
        testLinearSearchCustomer();
        testBinarySearchOrder();
        testMergeSortWithAllComparators();

        System.out.println("==================================================");
        System.out.printf("Test Summary: %d / %d tests passed.\n", testsPassed, testsTotal);
        if (testsPassed == testsTotal) {
            System.out.println("ALL STAGE 1 TESTS PASSED SUCCESSFULLY!");
        } else {
            System.err.println("SOME TESTS FAILED!");
            System.exit(1);
        }
        System.out.println("==================================================");
    }

    private static void testValidator() {
        System.out.println("\n--- Testing Validator ---");

        // isValidName
        assertTrue("Name 'Juan Dela Cruz' is valid", Validator.isValidName("Juan Dela Cruz"));
        assertTrue("Empty name is invalid", !Validator.isValidName(""));
        assertTrue("Whitespace-only name is invalid", !Validator.isValidName("   "));
        assertTrue("Null name is invalid", !Validator.isValidName(null));

        // isValidContact
        assertTrue("Valid contact '09171234567'", Validator.isValidContact("09171234567"));
        assertTrue("Valid contact with whitespace ' 09171234567 '", Validator.isValidContact(" 09171234567 "));
        assertTrue("Invalid: 10 digits '0917123456'", !Validator.isValidContact("0917123456"));
        assertTrue("Invalid: 12 digits '091712345678'", !Validator.isValidContact("091712345678"));
        assertTrue("Invalid: does not start with 09 '08171234567'", !Validator.isValidContact("08171234567"));
        assertTrue("Invalid: non-digits '0917123456a'", !Validator.isValidContact("0917123456a"));
        assertTrue("Null contact is invalid", !Validator.isValidContact(null));

        // isValidDorm
        assertTrue("Known dorm 'Villa Lourdes Dorm' is valid", Validator.isValidDorm("Villa Lourdes Dorm"));
        assertTrue("Known dorm case-insensitive 'villa lourdes dorm'", Validator.isValidDorm("villa lourdes dorm"));
        assertTrue("Known dorm 'Green Residences' is valid", Validator.isValidDorm("Green Residences"));
        assertTrue("Unknown dorm 'Random Dormitory' is invalid", !Validator.isValidDorm("Random Dormitory"));
        assertTrue("Null dorm is invalid", !Validator.isValidDorm(null));

        // isValidRoom
        assertTrue("Room '101-A' is valid", Validator.isValidRoom("101-A"));
        assertTrue("Empty room is invalid", !Validator.isValidRoom(""));
        assertTrue("Whitespace room is invalid", !Validator.isValidRoom("   "));
        assertTrue("Null room is invalid", !Validator.isValidRoom(null));

        // isValidWeight
        assertTrue("Weight 0.1 > 0 is valid", Validator.isValidWeight(0.1));
        assertTrue("Weight 7.0 is valid", Validator.isValidWeight(7.0));
        assertTrue("Weight 0.0 is invalid", !Validator.isValidWeight(0.0));
        assertTrue("Weight -5.0 is invalid", !Validator.isValidWeight(-5.0));
        assertTrue("Weight string '6.5' is valid", Validator.isValidWeight("6.5"));
        assertTrue("Weight string 'abc' is invalid", !Validator.isValidWeight("abc"));
        assertTrue("Weight string '-1' is invalid", !Validator.isValidWeight("-1"));

        // isValidDeadline
        assertTrue("Deadline '14:30' is valid", Validator.isValidDeadline("14:30"));
        assertTrue("Deadline '09:00' is valid", Validator.isValidDeadline("09:00"));
        assertTrue("Deadline '9:00' flexible format is valid", Validator.isValidDeadline("9:00"));
        assertTrue("Deadline '25:00' invalid hour", !Validator.isValidDeadline("25:00"));
        assertTrue("Deadline '12:60' invalid minute", !Validator.isValidDeadline("12:60"));
        assertTrue("Deadline 'invalid' is invalid", !Validator.isValidDeadline("invalid"));
        assertTrue("Null deadline is invalid", !Validator.isValidDeadline(null));
    }

    private static void testOrderCalculations() {
        System.out.println("\n--- Testing Order Calculations & Model ---");

        Customer cust = new Customer("C001", "Maria Santos", "09181112222", "Villa Lourdes Dorm", "204");
        // 7.0 kg -> 1 load -> Php 220
        Order o1 = new Order("LDY0001", cust, 7.0, ReceiveMethod.DROP_OFF, ReturnMethod.CLAIM, null, PaymentStatus.PAID, PaymentMethod.CASH);
        assertTrue("7.0 kg equals 1 load", o1.getLoads() == 1);
        assertTrue("1 load equals Php 220.0", o1.getTotalAmount() == 220.0);

        // 7.1 kg -> 2 loads -> Php 440
        Order o2 = new Order("LDY0002", cust, 7.1, ReceiveMethod.DROP_OFF, ReturnMethod.DELIVERY, LocalTime.of(15, 0), PaymentStatus.UNPAID, null);
        assertTrue("7.1 kg equals 2 loads (Math.ceil)", o2.getLoads() == 2);
        assertTrue("2 loads equals Php 440.0", o2.getTotalAmount() == 440.0);

        // 14.0 kg -> 2 loads -> Php 440
        Order o3 = new Order("LDY0003", cust, 14.0, ReceiveMethod.PICKUP, ReturnMethod.DELIVERY, null, PaymentStatus.UNPAID, null);
        assertTrue("14.0 kg equals 2 loads", o3.getLoads() == 2);
        assertTrue("14.0 kg total is Php 440.0", o3.getTotalAmount() == 440.0);

        // 14.1 kg -> 3 loads -> Php 660
        Order o4 = new Order("LDY0004", cust, 14.1, ReceiveMethod.PICKUP, ReturnMethod.CLAIM, null, PaymentStatus.UNPAID, null);
        assertTrue("14.1 kg equals 3 loads", o4.getLoads() == 3);
        assertTrue("3 loads equals Php 660.0", o4.getTotalAmount() == 660.0);

        // Dynamic weight update recomputes loads and total
        o1.setWeightKg(21.5);
        assertTrue("Updated weight 21.5 kg yields 4 loads", o1.getLoads() == 4);
        assertTrue("4 loads yields Php 880.0", o1.getTotalAmount() == 880.0);
    }

    private static void testStopAndUndoAction() {
        System.out.println("\n--- Testing Stop and UndoAction ---");

        Customer cust1 = new Customer("C001", "Maria", "09181112222", "Green Residences", "101");
        Order ord1 = new Order("LDY0001", cust1, 6.0, ReceiveMethod.DROP_OFF, ReturnMethod.DELIVERY, null, PaymentStatus.PAID, PaymentMethod.CASH); // 1 load
        Order ord2 = new Order("LDY0002", cust1, 13.0, ReceiveMethod.DROP_OFF, ReturnMethod.DELIVERY, null, PaymentStatus.PAID, PaymentMethod.CASH); // 2 loads

        Stop stop = new Stop("Green Residences");
        stop.addOrder(ord1);
        stop.addOrder(ord2);
        assertTrue("Stop dorm matches", "Green Residences".equals(stop.getDorm()));
        assertTrue("Stop holds 2 orders", stop.getOrders().size() == 2);
        assertTrue("Stop total loads is 3 (1 + 2)", stop.getTotalLoads() == 3);

        UndoAction act1 = new UndoAction(UndoAction.PAYMENT, ord1, "Payment of Php 220.0 for LDY0001");
        assertTrue("UndoAction type is PAYMENT", UndoAction.PAYMENT.equals(act1.getType()));
        assertTrue("UndoAction holds correct order", act1.getOrder() == ord1);
        assertTrue("UndoAction description matches", act1.getDescription().contains("LDY0001"));
    }

    private static void testLinearSearchCustomer() {
        System.out.println("\n--- Testing SearchUtil.linearSearchCustomer ---");

        List<Customer> customers = new ArrayList<>();
        customers.add(new Customer("C001", "Juan Dela Cruz", "09171234567", "Villa Lourdes Dorm", "101"));
        customers.add(new Customer("C002", "Ana Reyes", "09182345678", "Green Residences", "202"));
        customers.add(new Customer("C003", "Carlos Garcia", "09193456789", "St. Joseph Hall", "303"));

        // Match by ID
        Customer foundId = SearchUtil.linearSearchCustomer(customers, "c002");
        assertTrue("Found customer by ID (case-insensitive)", foundId != null && "Ana Reyes".equals(foundId.getName()));

        // Match by Name
        Customer foundName = SearchUtil.linearSearchCustomer(customers, "carlos garcia");
        assertTrue("Found customer by Name (case-insensitive)", foundName != null && "C003".equals(foundName.getCustomerId()));

        // Match by Contact
        Customer foundContact = SearchUtil.linearSearchCustomer(customers, "09171234567");
        assertTrue("Found customer by Contact Number", foundContact != null && "C001".equals(foundContact.getCustomerId()));

        // Not found
        Customer notFound = SearchUtil.linearSearchCustomer(customers, "Unknown Person");
        assertTrue("Non-existent customer returns null", notFound == null);
    }

    private static void testBinarySearchOrder() {
        System.out.println("\n--- Testing SearchUtil.binarySearchOrder ---");

        Customer cust = new Customer("C001", "Test Cust", "09171112222", "Villa Lourdes Dorm", "101");
        List<Order> orders = new ArrayList<>();
        orders.add(new Order("LDY0001", cust, 5.0, ReceiveMethod.DROP_OFF, ReturnMethod.CLAIM, null, PaymentStatus.PAID, PaymentMethod.CASH));
        orders.add(new Order("LDY0002", cust, 8.0, ReceiveMethod.DROP_OFF, ReturnMethod.CLAIM, null, PaymentStatus.PAID, PaymentMethod.CASH));
        orders.add(new Order("LDY0003", cust, 10.0, ReceiveMethod.DROP_OFF, ReturnMethod.CLAIM, null, PaymentStatus.PAID, PaymentMethod.CASH));
        orders.add(new Order("LDY0004", cust, 12.0, ReceiveMethod.DROP_OFF, ReturnMethod.CLAIM, null, PaymentStatus.PAID, PaymentMethod.CASH));
        orders.add(new Order("LDY0005", cust, 15.0, ReceiveMethod.DROP_OFF, ReturnMethod.CLAIM, null, PaymentStatus.PAID, PaymentMethod.CASH));

        // Search at head
        Order foundHead = SearchUtil.binarySearchOrder(orders, "LDY0001");
        assertTrue("Binary search found head order (LDY0001)", foundHead != null && "LDY0001".equals(foundHead.getOrderId()));

        // Search at mid
        Order foundMid = SearchUtil.binarySearchOrder(orders, "ldy0003");
        assertTrue("Binary search found mid order case-insensitively (LDY0003)", foundMid != null && "LDY0003".equals(foundMid.getOrderId()));

        // Search at tail
        Order foundTail = SearchUtil.binarySearchOrder(orders, "LDY0005");
        assertTrue("Binary search found tail order (LDY0005)", foundTail != null && "LDY0005".equals(foundTail.getOrderId()));

        // Search non-existent
        Order notFound = SearchUtil.binarySearchOrder(orders, "LDY9999");
        assertTrue("Binary search for non-existent order returns null", notFound == null);
    }

    private static void testMergeSortWithAllComparators() {
        System.out.println("\n--- Testing SortUtil.mergeSort With All 5 Comparators ---");

        Customer c1 = new Customer("C001", "Zara Tan", "09170000001", "Villa Lourdes Dorm", "101");
        Customer c2 = new Customer("C002", "Ben Lim", "09170000002", "Green Residences", "201");
        Customer c3 = new Customer("C003", "Alice Sy", "09170000003", "De La Salle Dorm", "301");
        Customer c4 = new Customer("C004", "Daniel Cruz", "09170000004", "University Suites", "401");

        LocalDateTime now = LocalDateTime.now();
        List<Order> original = new ArrayList<>();
        // o1: Zara, 21kg (3 loads -> 660), 10:00 received, 17:00 deadline, Villa Lourdes
        Order o1 = new Order("LDY0001", c1, 21.0, ReceiveMethod.DROP_OFF, ReturnMethod.DELIVERY, LocalTime.of(17, 0), LaundryStatus.READY, PaymentStatus.PAID, PaymentMethod.CASH, now.minusHours(4), now);
        // o2: Ben, 7kg (1 load -> 220), 11:00 received, 15:00 deadline, Green Residences
        Order o2 = new Order("LDY0002", c2, 7.0, ReceiveMethod.DROP_OFF, ReturnMethod.DELIVERY, LocalTime.of(15, 0), LaundryStatus.READY, PaymentStatus.PAID, PaymentMethod.CASH, now.minusHours(3), now);
        // o3: Alice, 14kg (2 loads -> 440), 08:00 received, null deadline, De La Salle
        Order o3 = new Order("LDY0003", c3, 14.0, ReceiveMethod.DROP_OFF, ReturnMethod.CLAIM, null, LaundryStatus.READY, PaymentStatus.PAID, PaymentMethod.CASH, now.minusHours(6), now);
        // o4: Daniel, 28kg (4 loads -> 880), 09:00 received, 13:00 deadline, University Suites
        Order o4 = new Order("LDY0004", c4, 28.0, ReceiveMethod.DROP_OFF, ReturnMethod.DELIVERY, LocalTime.of(13, 0), LaundryStatus.READY, PaymentStatus.PAID, PaymentMethod.CASH, now.minusHours(5), now);

        original.add(o1);
        original.add(o2);
        original.add(o3);
        original.add(o4);

        // 1. Sort by Total Amount (220, 440, 660, 880 -> o2, o3, o1, o4)
        List<Order> sortedByAmount = SortUtil.mergeSort(original, SortUtil.BY_TOTAL_AMOUNT);
        assertTrue("Original list unmodified", original.get(0) == o1 && original.size() == 4);
        assertTrue("Sorted by amount size matches", sortedByAmount.size() == 4);
        assertTrue("Sorted by amount o2 (220) is first", sortedByAmount.get(0) == o2);
        assertTrue("Sorted by amount o3 (440) is second", sortedByAmount.get(1) == o3);
        assertTrue("Sorted by amount o1 (660) is third", sortedByAmount.get(2) == o1);
        assertTrue("Sorted by amount o4 (880) is fourth", sortedByAmount.get(3) == o4);

        // 2. Sort by Customer Name (Alice, Ben, Daniel, Zara -> o3, o2, o4, o1)
        List<Order> sortedByName = SortUtil.mergeSort(original, SortUtil.BY_CUSTOMER_NAME);
        assertTrue("Sorted by name: Alice (o3) is first", sortedByName.get(0) == o3);
        assertTrue("Sorted by name: Ben (o2) is second", sortedByName.get(1) == o2);
        assertTrue("Sorted by name: Daniel (o4) is third", sortedByName.get(2) == o4);
        assertTrue("Sorted by name: Zara (o1) is fourth", sortedByName.get(3) == o1);

        // 3. Sort by Time Received (-6h: o3, -5h: o4, -4h: o1, -3h: o2)
        List<Order> sortedByTime = SortUtil.mergeSort(original, SortUtil.BY_TIME_RECEIVED);
        assertTrue("Sorted by time: o3 (-6h) is first", sortedByTime.get(0) == o3);
        assertTrue("Sorted by time: o4 (-5h) is second", sortedByTime.get(1) == o4);
        assertTrue("Sorted by time: o1 (-4h) is third", sortedByTime.get(2) == o1);
        assertTrue("Sorted by time: o2 (-3h) is fourth", sortedByTime.get(3) == o2);

        // 4. Sort by Delivery Deadline (13:00: o4, 15:00: o2, 17:00: o1, null: o3)
        List<Order> sortedByDeadline = SortUtil.mergeSort(original, SortUtil.BY_DELIVERY_DEADLINE);
        assertTrue("Sorted by deadline: o4 (13:00) is first", sortedByDeadline.get(0) == o4);
        assertTrue("Sorted by deadline: o2 (15:00) is second", sortedByDeadline.get(1) == o2);
        assertTrue("Sorted by deadline: o1 (17:00) is third", sortedByDeadline.get(2) == o1);
        assertTrue("Sorted by deadline: o3 (null) is last", sortedByDeadline.get(3) == o3);

        // 5. Sort by Dorm (De La Salle: o3, Green Residences: o2, University Suites: o4, Villa Lourdes: o1)
        List<Order> sortedByDorm = SortUtil.mergeSort(original, SortUtil.BY_DORM);
        assertTrue("Sorted by dorm: De La Salle (o3) is first", sortedByDorm.get(0) == o3);
        assertTrue("Sorted by dorm: Green Residences (o2) is second", sortedByDorm.get(1) == o2);
        assertTrue("Sorted by dorm: University Suites (o4) is third", sortedByDorm.get(2) == o4);
        assertTrue("Sorted by dorm: Villa Lourdes (o1) is fourth", sortedByDorm.get(3) == o1);

        // Stability check: two orders with same amount
        Order o5 = new Order("LDY0005", c1, 7.0, ReceiveMethod.DROP_OFF, ReturnMethod.CLAIM, null, PaymentStatus.PAID, PaymentMethod.CASH); // 220
        List<Order> stabilityList = new ArrayList<>();
        stabilityList.add(o2); // LDY0002 has 220
        stabilityList.add(o5); // LDY0005 has 220
        List<Order> stableSorted = SortUtil.mergeSort(stabilityList, SortUtil.BY_TOTAL_AMOUNT);
        assertTrue("MergeSort is stable (o2 before o5 preserved)", stableSorted.get(0) == o2 && stableSorted.get(1) == o5);
    }
}
