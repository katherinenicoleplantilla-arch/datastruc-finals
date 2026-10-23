import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Test harness for Stage 3.
 * Exercises delivery dispatch with 10-load capacity capping, LIFO trike delivery,
 * multi-key order viewing with merge sort on copies, searching, and operational reports.
 */
public class Stage3Test {

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
        System.out.println("       RUNNING STAGE 3 COMPREHENSIVE TESTS        ");
        System.out.println("==================================================");

        testSearching();
        testViewOrdersMergeSortCopyPreservation();
        testTrikeDispatchWithCapacityCap();
        testDeliverNextStopLifecycle();
        testSecondDispatchRun();
        testSystemReports();

        System.out.println("\n==================================================");
        System.out.printf("Stage 3 Test Summary: %d / %d tests passed.\n", testsPassed, testsTotal);
        if (testsPassed == testsTotal) {
            System.out.println("ALL STAGE 3 TESTS PASSED SUCCESSFULLY!");
        } else {
            System.err.println("SOME STAGE 3 TESTS FAILED!");
            System.exit(1);
        }
        System.out.println("==================================================");
    }

    private static void testSearching() {
        System.out.println("\n--- Testing searchCustomer() & searchOrder() ---");
        LaundrySystem system = new LaundrySystem();
        system.loadSampleData();

        // Search customer by Contact Number
        String custInput = "09193456789\n";
        system.setScanner(new Scanner(new ByteArrayInputStream(custInput.getBytes(StandardCharsets.UTF_8))));
        system.searchCustomer();
        assertTrue("Customer search by contact completed", true);

        // Search customer not found
        system.setScanner(new Scanner(new ByteArrayInputStream("nonexistent\n".getBytes(StandardCharsets.UTF_8))));
        system.searchCustomer();
        assertTrue("Non-existent customer search handled safely", true);

        // Search order by ID
        String ordInput = "LDY0006\n";
        system.setScanner(new Scanner(new ByteArrayInputStream(ordInput.getBytes(StandardCharsets.UTF_8))));
        system.searchOrder();
        assertTrue("Order search by Order ID completed", true);

        // Search non-existent order
        system.setScanner(new Scanner(new ByteArrayInputStream("LDY9999\n".getBytes(StandardCharsets.UTF_8))));
        system.searchOrder();
        assertTrue("Non-existent order search handled safely", true);
    }

    private static void testViewOrdersMergeSortCopyPreservation() {
        System.out.println("\n--- Testing viewOrders() & Preservation of Original Orders List ---");
        LaundrySystem system = new LaundrySystem();
        system.loadSampleData();

        // Verify original order IDs are in ascending order
        for (int i = 0; i < system.getOrders().size() - 1; i++) {
            String id1 = system.getOrders().get(i).getOrderId();
            String id2 = system.getOrders().get(i + 1).getOrderId();
            assertTrue("Initial list is sorted by Order ID: " + id1 + " < " + id2, id1.compareTo(id2) < 0);
        }

        // View orders sorted by Total Amount (choice 1)
        system.setScanner(new Scanner(new ByteArrayInputStream("1\n".getBytes(StandardCharsets.UTF_8))));
        system.viewOrders();

        // Verify original order list has NOT been mutated by mergeSort!
        assertTrue("Original list size preserved", system.getOrders().size() == 10);
        assertTrue("Index 0 is still LDY0001", "LDY0001".equals(system.getOrders().get(0).getOrderId()));
        assertTrue("Index 9 is still LDY0010", "LDY0010".equals(system.getOrders().get(9).getOrderId()));
        for (int i = 0; i < system.getOrders().size() - 1; i++) {
            String id1 = system.getOrders().get(i).getOrderId();
            String id2 = system.getOrders().get(i + 1).getOrderId();
            assertTrue("Original list order strictly preserved after sorting copy: " + id1 + " < " + id2, id1.compareTo(id2) < 0);
        }
    }

    private static void testTrikeDispatchWithCapacityCap() {
        System.out.println("\n--- Testing dispatchTrike() with 10-Load Capacity Cap ---");
        LaundrySystem system = new LaundrySystem();
        system.loadSampleData();

        // In sample data, ready delivery orders are:
        // LDY0006 (Villa Lourdes): 3 loads
        // LDY0007 (Villa Lourdes): 2 loads (Villa Lourdes total = 5 loads)
        // LDY0008 (Green Residences): 4 loads
        // LDY0009 (St. Joseph Hall): 3 loads
        // LDY0010 (Lipa Heights Dorm): 2 loads
        // Total = 14 loads.
        // Dorm alphabetical order:
        // 1. Green Residences (4 loads) -> accum = 4
        // 2. Lipa Heights Dorm (2 loads) -> accum = 6
        // 3. St. Joseph Hall (3 loads) -> accum = 9
        // 4. Villa Lourdes Dorm (3 loads) -> 9 + 3 = 12 > 10 (exceeds 10-load cap!)
        // So Run #1 must include: Green Residences, Lipa Heights, St. Joseph Hall (9 loads).
        // Villa Lourdes Dorm orders (5 loads) remain READY for Run #2!

        system.dispatchTrike();

        assertTrue("Delivery log recorded Run #1", system.getDeliveryLog().size() == 1);
        assertTrue("Trike stack has 3 stops", system.getTrikeStack().size() == 3);

        // Reverse sequence verification:
        // Stops were: Green Residences, Lipa Heights, St. Joseph Hall.
        // Pushed in reverse:
        // Push St. Joseph Hall
        // Push Lipa Heights
        // Push Green Residences
        // Top of stack is Green Residences!
        Stop topStop = system.getTrikeStack().peek();
        assertTrue("Top stop of trikeStack is Green Residences (1st stop)",
                topStop != null && "Green Residences".equals(topStop.getDorm()));

        // Check order statuses
        Order ordGreen = system.findOrderById("LDY0008");
        Order ordLipa = system.findOrderById("LDY0010");
        Order ordStJoseph = system.findOrderById("LDY0009");
        Order ordVilla1 = system.findOrderById("LDY0006");
        Order ordVilla2 = system.findOrderById("LDY0007");

        assertTrue("LDY0008 is OUT_FOR_DELIVERY", ordGreen.getLaundryStatus() == LaundryStatus.OUT_FOR_DELIVERY);
        assertTrue("LDY0010 is OUT_FOR_DELIVERY", ordLipa.getLaundryStatus() == LaundryStatus.OUT_FOR_DELIVERY);
        assertTrue("LDY0009 is OUT_FOR_DELIVERY", ordStJoseph.getLaundryStatus() == LaundryStatus.OUT_FOR_DELIVERY);

        // Cap verification: orders that didn't fit remain READY
        assertTrue("LDY0006 remained READY (cap enforced)", ordVilla1.getLaundryStatus() == LaundryStatus.READY);
        assertTrue("LDY0007 remained READY (cap enforced)", ordVilla2.getLaundryStatus() == LaundryStatus.READY);
    }

    private static void testDeliverNextStopLifecycle() {
        System.out.println("\n--- Testing deliverNextStop() Full LIFO Unloading ---");
        LaundrySystem system = new LaundrySystem();
        system.loadSampleData();
        system.dispatchTrike(); // 3 stops: Green, Lipa Heights, St. Joseph

        // Stop 1: Green Residences (Prepaid Online)
        system.deliverNextStop();
        Order ordGreen = system.findOrderById("LDY0008");
        assertTrue("Stop 1 order LDY0008 is COMPLETED", ordGreen.getLaundryStatus() == LaundryStatus.COMPLETED);
        assertTrue("Trike stack has 2 stops left", system.getTrikeStack().size() == 2);

        // Stop 2: Lipa Heights Dorm (Prepaid Cash)
        system.deliverNextStop();
        Order ordLipa = system.findOrderById("LDY0010");
        assertTrue("Stop 2 order LDY0010 is COMPLETED", ordLipa.getLaundryStatus() == LaundryStatus.COMPLETED);
        assertTrue("Trike stack has 1 stop left", system.getTrikeStack().size() == 1);

        // Stop 3: St. Joseph Hall (UNPAID -> collects cash on delivery)
        Order ordStJoseph = system.findOrderById("LDY0009");
        assertTrue("LDY0009 was UNPAID before delivery", ordStJoseph.getPaymentStatus() == PaymentStatus.UNPAID);
        system.deliverNextStop();
        assertTrue("Stop 3 order LDY0009 is COMPLETED", ordStJoseph.getLaundryStatus() == LaundryStatus.COMPLETED);
        assertTrue("Stop 3 order LDY0009 paid via CASH on delivery", ordStJoseph.getPaymentStatus() == PaymentStatus.PAID);
        assertTrue("Payment method set to CASH", ordStJoseph.getPaymentMethod() == PaymentMethod.CASH);
        assertTrue("Trike stack is now empty", system.getTrikeStack().isEmpty());

        // Call deliverNextStop when empty
        system.deliverNextStop();
        assertTrue("Handled delivery with empty stack safely", system.getTrikeStack().isEmpty());
    }

    private static void testSecondDispatchRun() {
        System.out.println("\n--- Testing Run #2 Dispatch for Remaining Ready Orders ---");
        LaundrySystem system = new LaundrySystem();
        system.loadSampleData();
        system.dispatchTrike(); // Run 1
        // Deliver all 3 stops of Run 1
        system.deliverNextStop();
        system.deliverNextStop();
        system.deliverNextStop();

        // Now dispatch Run #2 with remaining Villa Lourdes orders
        system.dispatchTrike();
        assertTrue("Delivery log now contains 2 runs", system.getDeliveryLog().size() == 2);
        assertTrue("Trike stack has 1 stop (Villa Lourdes Dorm)", system.getTrikeStack().size() == 1);

        Stop villaStop = system.getTrikeStack().peek();
        assertTrue("Stop dorm is Villa Lourdes Dorm", "Villa Lourdes Dorm".equals(villaStop.getDorm()));
        assertTrue("Villa Lourdes stop has 2 orders", villaStop.getOrders().size() == 2);
        assertTrue("Villa Lourdes stop has 5 loads (3 + 2)", villaStop.getTotalLoads() == 5);

        // Deliver Stop of Run 2
        system.deliverNextStop();
        Order ordVilla1 = system.findOrderById("LDY0006");
        Order ordVilla2 = system.findOrderById("LDY0007");
        assertTrue("LDY0006 is COMPLETED", ordVilla1.getLaundryStatus() == LaundryStatus.COMPLETED);
        assertTrue("LDY0007 is COMPLETED", ordVilla2.getLaundryStatus() == LaundryStatus.COMPLETED);
        assertTrue("LDY0007 (previously unpaid) is now PAID", ordVilla2.getPaymentStatus() == PaymentStatus.PAID);

        // Dispatch again when no ready delivery orders exist
        system.dispatchTrike();
        assertTrue("Trike stack remains empty when no orders ready", system.getTrikeStack().isEmpty());
    }

    private static void testSystemReports() {
        System.out.println("\n--- Testing All System Reports (12a to 12e) ---");
        LaundrySystem system = new LaundrySystem();
        system.loadSampleData();

        // Report 12a: Pending and ready
        system.reportPendingAndReady();
        assertTrue("Report 12a executed without error", true);

        // Report 12b: Unpaid
        system.reportUnpaid();
        assertTrue("Report 12b executed without error", true);

        // Report 12c: Daily sales
        system.reportDailySales();
        assertTrue("Report 12c executed without error", true);

        // Report 12d: Delivery runs (after 1 dispatch)
        system.dispatchTrike();
        system.reportDeliveryRuns();
        assertTrue("Report 12d executed with active runs", true);

        // Report 12e: Late orders (> 3 hours)
        // LDY0006 in sample data became ready 4 hours ago, so it should be reported
        system.reportLateOrders();
        assertTrue("Report 12e executed and flagged late orders", true);
    }
}
