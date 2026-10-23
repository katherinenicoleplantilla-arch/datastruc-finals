import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Test harness for Stage 2.
 * Thoroughly exercises LaundrySystem setup, order operations, payments,
 * undo actions, pickup queue, and sample data.
 */
public class Stage2Test {

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
        System.out.println("       RUNNING STAGE 2 COMPREHENSIVE TESTS        ");
        System.out.println("==================================================");

        testSampleDataAndLookups();
        testWashingAndReadyProcess();
        testUndoMarkReady();
        testPaymentAndUndoPayment();
        testClaimOrder();
        testPickupQueueOperations();
        testAddCustomerInteractive();
        testCreateOrderInteractive();

        System.out.println("\n==================================================");
        System.out.printf("Stage 2 Test Summary: %d / %d tests passed.\n", testsPassed, testsTotal);
        if (testsPassed == testsTotal) {
            System.out.println("ALL STAGE 2 TESTS PASSED SUCCESSFULLY!");
        } else {
            System.err.println("SOME STAGE 2 TESTS FAILED!");
            System.exit(1);
        }
        System.out.println("==================================================");
    }

    private static void testSampleDataAndLookups() {
        System.out.println("\n--- Testing loadSampleData() & findOrderById() ---");
        LaundrySystem system = new LaundrySystem();
        system.loadSampleData();

        assertTrue("Loaded 5 customers", system.getCustomers().size() == 5);
        assertTrue("Loaded 10 orders", system.getOrders().size() == 10);
        assertTrue("Washing queue has 2 orders", system.getWashingQueue().size() == 2);
        assertTrue("Pickup queue has 1 customer", system.getPickupQueue().size() == 1);

        // Binary search lookups
        Order head = system.findOrderById("LDY0001");
        Order mid = system.findOrderById("ldy0005"); // case-insensitive
        Order tail = system.findOrderById("LDY0010");
        Order missing = system.findOrderById("LDY9999");

        assertTrue("Found head order LDY0001", head != null && "LDY0001".equals(head.getOrderId()));
        assertTrue("Found mid order LDY0005 case-insensitively", mid != null && "LDY0005".equals(mid.getOrderId()));
        assertTrue("Found tail order LDY0010", tail != null && "LDY0010".equals(tail.getOrderId()));
        assertTrue("Missing order returns null", missing == null);

        // Check ready delivery orders total loads
        int readyDeliveryLoads = 0;
        for (Order o : system.getOrders()) {
            if (o.getLaundryStatus() == LaundryStatus.READY && o.getReturnMethod() == ReturnMethod.DELIVERY) {
                readyDeliveryLoads += o.getLoads();
            }
        }
        assertTrue("Ready delivery orders total 14 loads (> 10 trike cap)", readyDeliveryLoads == 14);
    }

    private static void testWashingAndReadyProcess() {
        System.out.println("\n--- Testing processNextOrder() & markOrderReady() ---");
        LaundrySystem system = new LaundrySystem();
        system.loadSampleData();

        // Front of washing queue is LDY0002
        Order frontOrder = system.getWashingQueue().peek();
        assertTrue("Front order is LDY0002", frontOrder != null && "LDY0002".equals(frontOrder.getOrderId()));
        assertTrue("Initial status is WAITING", frontOrder.getLaundryStatus() == LaundryStatus.WAITING);

        // Process next order
        system.processNextOrder();
        assertTrue("LDY0002 status transitioned to PROCESSING", frontOrder.getLaundryStatus() == LaundryStatus.PROCESSING);
        assertTrue("Washing queue now has 1 remaining order", system.getWashingQueue().size() == 1);

        // Try to mark an order that is WAITING or COMPLETED as ready (should be rejected)
        String simulateReject = "LDY0001\n";
        system.setScanner(new Scanner(new ByteArrayInputStream(simulateReject.getBytes(StandardCharsets.UTF_8))));
        system.markOrderReady();
        Order completedOrder = system.findOrderById("LDY0001");
        assertTrue("Completed order remains COMPLETED", completedOrder.getLaundryStatus() == LaundryStatus.COMPLETED);

        // Mark LDY0002 (PROCESSING) as READY
        String simulateReady = "LDY0002\n";
        system.setScanner(new Scanner(new ByteArrayInputStream(simulateReady.getBytes(StandardCharsets.UTF_8))));
        system.markOrderReady();
        assertTrue("LDY0002 status is now READY", frontOrder.getLaundryStatus() == LaundryStatus.READY);
        assertTrue("LDY0002 timeReady is recorded", frontOrder.getTimeReady() != null);
        assertTrue("Undo stack contains 1 action", system.getUndoStack().size() == 1);
        assertTrue("Undo action is MARK_READY", UndoAction.MARK_READY.equals(system.getUndoStack().peek().getType()));
    }

    private static void testUndoMarkReady() {
        System.out.println("\n--- Testing undoLastAction() for MARK_READY ---");
        LaundrySystem system = new LaundrySystem();
        system.loadSampleData();

        Order order = system.findOrderById("LDY0004"); // PROCESSING
        assertTrue("LDY0004 initially PROCESSING", order.getLaundryStatus() == LaundryStatus.PROCESSING);

        // Mark READY
        system.setScanner(new Scanner(new ByteArrayInputStream("LDY0004\n".getBytes(StandardCharsets.UTF_8))));
        system.markOrderReady();
        assertTrue("LDY0004 became READY", order.getLaundryStatus() == LaundryStatus.READY);
        assertTrue("timeReady is populated", order.getTimeReady() != null);

        // Undo
        system.undoLastAction();
        assertTrue("LDY0004 reverted to PROCESSING", order.getLaundryStatus() == LaundryStatus.PROCESSING);
        assertTrue("timeReady cleared to null", order.getTimeReady() == null);
        assertTrue("Undo stack is now empty", system.getUndoStack().isEmpty());

        // Undo when empty (graceful handling)
        system.undoLastAction();
        assertTrue("Undo on empty stack handled safely", system.getUndoStack().isEmpty());
    }

    private static void testPaymentAndUndoPayment() {
        System.out.println("\n--- Testing recordPayment() and undoLastAction() for PAYMENT ---");
        LaundrySystem system = new LaundrySystem();
        system.loadSampleData();

        Order unpaidOrder = system.findOrderById("LDY0002"); // UNPAID
        assertTrue("LDY0002 initially UNPAID", unpaidOrder.getPaymentStatus() == PaymentStatus.UNPAID);

        // Record payment with Cash (Option 1)
        String paymentInput = "LDY0002\n1\n";
        system.setScanner(new Scanner(new ByteArrayInputStream(paymentInput.getBytes(StandardCharsets.UTF_8))));
        system.recordPayment();

        assertTrue("LDY0002 paymentStatus is now PAID", unpaidOrder.getPaymentStatus() == PaymentStatus.PAID);
        assertTrue("LDY0002 paymentMethod is CASH", unpaidOrder.getPaymentMethod() == PaymentMethod.CASH);
        assertTrue("Undo stack has payment action", system.getUndoStack().size() == 1);

        // Try paying again (should reject double payment)
        String repayInput = "LDY0002\n";
        system.setScanner(new Scanner(new ByteArrayInputStream(repayInput.getBytes(StandardCharsets.UTF_8))));
        system.recordPayment();
        assertTrue("Double payment prevented, undo stack still size 1", system.getUndoStack().size() == 1);

        // Undo payment
        system.undoLastAction();
        assertTrue("LDY0002 paymentStatus reverted to UNPAID", unpaidOrder.getPaymentStatus() == PaymentStatus.UNPAID);
        assertTrue("LDY0002 paymentMethod cleared to null", unpaidOrder.getPaymentMethod() == null);
        assertTrue("Undo stack empty after reverting payment", system.getUndoStack().isEmpty());
    }

    private static void testClaimOrder() {
        System.out.println("\n--- Testing claimOrder() ---");
        LaundrySystem system = new LaundrySystem();
        system.loadSampleData();

        // LDY0005 is READY, CLAIM, UNPAID
        Order order = system.findOrderById("LDY0005");
        assertTrue("LDY0005 is READY", order.getLaundryStatus() == LaundryStatus.READY);
        assertTrue("LDY0005 is CLAIM", order.getReturnMethod() == ReturnMethod.CLAIM);
        assertTrue("LDY0005 is UNPAID", order.getPaymentStatus() == PaymentStatus.UNPAID);

        // Claim it, paying via Cash (choice 1)
        String claimInput = "LDY0005\n1\n";
        system.setScanner(new Scanner(new ByteArrayInputStream(claimInput.getBytes(StandardCharsets.UTF_8))));
        system.claimOrder();

        assertTrue("LDY0005 status became COMPLETED", order.getLaundryStatus() == LaundryStatus.COMPLETED);
        assertTrue("LDY0005 paymentStatus became PAID", order.getPaymentStatus() == PaymentStatus.PAID);
        assertTrue("LDY0005 paymentMethod became CASH", order.getPaymentMethod() == PaymentMethod.CASH);

        // Claiming an order that is DELIVERY (e.g. LDY0006) should be rejected
        system.setScanner(new Scanner(new ByteArrayInputStream("LDY0006\n".getBytes(StandardCharsets.UTF_8))));
        system.claimOrder();
        Order deliveryOrder = system.findOrderById("LDY0006");
        assertTrue("Delivery order LDY0006 remains READY (not claimed)", deliveryOrder.getLaundryStatus() == LaundryStatus.READY);
    }

    private static void testPickupQueueOperations() {
        System.out.println("\n--- Testing logPickupRequest() and processNextPickup() ---");
        LaundrySystem system = new LaundrySystem();
        system.loadSampleData();

        // Sample data loaded C003 into pickup queue
        assertTrue("Pickup queue initially has 1 request", system.getPickupQueue().size() == 1);

        // Enqueue another request (C001)
        system.setScanner(new Scanner(new ByteArrayInputStream("C001\n".getBytes(StandardCharsets.UTF_8))));
        system.logPickupRequest();
        assertTrue("Pickup queue now has 2 requests", system.getPickupQueue().size() == 2);

        // Process first pickup (should be C003 FIFO)
        system.processNextPickup();
        assertTrue("Pickup queue has 1 remaining", system.getPickupQueue().size() == 1);

        // Process second pickup (should be C001)
        system.processNextPickup();
        assertTrue("Pickup queue is now empty", system.getPickupQueue().isEmpty());

        // Process when empty
        system.processNextPickup();
        assertTrue("Process on empty pickup queue handled gracefully", system.getPickupQueue().isEmpty());
    }

    private static void testAddCustomerInteractive() {
        System.out.println("\n--- Testing addCustomer() with Validation Re-prompting ---");
        LaundrySystem system = new LaundrySystem();

        // Simulated inputs:
        // 1. Name: "" (invalid), "   " (invalid), "Andres Bonifacio" (valid)
        // 2. Contact: "123" (invalid), "0912345" (invalid), "09179998888" (valid)
        // 3. Dorm: "   " (invalid), "Villa Lourdes Dorm" (valid custom dorm)
        // 4. Room: "   " (invalid), "305-B" (valid)
        String simulatedInput = String.join("\n",
                "",
                "   ",
                "Andres Bonifacio",
                "123",
                "0912345",
                "09179998888",
                "   ",
                "Villa Lourdes Dorm",
                "   ",
                "305-B"
        ) + "\n";

        system.setScanner(new Scanner(new ByteArrayInputStream(simulatedInput.getBytes(StandardCharsets.UTF_8))));
        system.addCustomer();

        assertTrue("Customer registered", system.getCustomers().size() == 1);
        Customer cust = system.getCustomers().get(0);
        assertTrue("Generated ID is C001", "C001".equals(cust.getCustomerId()));
        assertTrue("Customer name matches", "Andres Bonifacio".equals(cust.getName()));
        assertTrue("Customer contact matches", "09179998888".equals(cust.getContactNumber()));
        assertTrue("Customer dorm matches Villa Lourdes Dorm", "Villa Lourdes Dorm".equals(cust.getDorm()));
        assertTrue("Customer room matches", "305-B".equals(cust.getRoomNumber()));
    }

    private static void testCreateOrderInteractive() {
        System.out.println("\n--- Testing createOrder() with Validation and Queuing ---");
        LaundrySystem system = new LaundrySystem();
        system.loadSampleData();

        // Simulated inputs:
        // 1. Customer ID: "C001" (valid)
        // 2. Weight: "-2" (invalid), "abc" (invalid), "0" (invalid), "15.0" (valid -> 3 loads -> Php 660)
        // 3. Receive Method: "1" (Drop-off)
        // 4. Return Method: "2" (Delivery)
        // 5. Deadline: "invalid" (invalid), "26:00" (invalid), "17:30" (valid)
        // 6. Pay Option: "1" (Pay Now)
        // 7. Payment Method: "2" (Online)
        String simulatedOrder = String.join("\n",
                "C001",
                "-2",
                "abc",
                "0",
                "15.0",
                "1",
                "2",
                "invalid",
                "26:00",
                "17:30",
                "1",
                "2"
        ) + "\n";

        system.setScanner(new Scanner(new ByteArrayInputStream(simulatedOrder.getBytes(StandardCharsets.UTF_8))));
        system.createOrder();

        // Orders count should be 11 (10 from sample data + 1)
        assertTrue("Total orders is 11", system.getOrders().size() == 11);
        Order newOrder = system.findOrderById("LDY0011");
        assertTrue("New order LDY0011 created and found by binary search", newOrder != null);
        assertTrue("Order loads is 3 (15.0 kg / 7.0 ceil)", newOrder.getLoads() == 3);
        assertTrue("Order total is Php 660.0", newOrder.getTotalAmount() == 660.0);
        assertTrue("Return method is DELIVERY", newOrder.getReturnMethod() == ReturnMethod.DELIVERY);
        assertTrue("Deadline is 17:30", newOrder.getDeliveryDeadline() != null && "17:30".equals(newOrder.getDeliveryDeadline().toString()));
        assertTrue("Payment status is PAID", newOrder.getPaymentStatus() == PaymentStatus.PAID);
        assertTrue("Payment method is ONLINE", newOrder.getPaymentMethod() == PaymentMethod.ONLINE);
        assertTrue("Laundry status is WAITING", newOrder.getLaundryStatus() == LaundryStatus.WAITING);
    }
}
