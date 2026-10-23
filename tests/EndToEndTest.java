import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Automated End-to-End Test Suite for Main and LaundrySystem.
 * Streams a comprehensive series of inputs covering all 15 menu options,
 * including malformed input (letters, negative numbers, bad formats, missing IDs).
 * Validates that Main and LaundrySystem never crash.
 */
public class EndToEndTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("       STARTING FULL END-TO-END SYSTEM TEST       ");
        System.out.println("==================================================");

        // Build sequence of inputs:
        StringBuilder input = new StringBuilder();

        // 1. Invalid menu choices to verify NumberFormatException try-catch & range check
        input.append("abc\n");        // Non-numeric
        input.append("   \n");        // Blank
        input.append("-5\n");         // Out of bounds
        input.append("99\n");         // Out of bounds

        // 2. Load Sample Data (Option 14)
        input.append("14\n");

        // 3. View Orders with each sort key (Option 11)
        input.append("11\n1\n");      // Total Amount
        input.append("11\n2\n");      // Customer Name
        input.append("11\n3\n");      // Time Received
        input.append("11\n4\n");      // Delivery Deadline
        input.append("11\n5\n");      // Dormitory
        input.append("11\n99\n");     // Invalid sort key (falls back safely)

        // 4. Search (Option 10)
        input.append("10\n1\n09171234567\n"); // 10a Search customer by contact
        input.append("10\n1\nNonExistent\n"); // 10a Search customer not found
        input.append("10\n2\nLDY0006\n");     // 10b Search order by ID
        input.append("10\n2\nLDY9999\n");     // 10b Search order not found
        input.append("10\n99\n");             // Invalid search submenu choice

        // 5. Add Customer (Option 1) with deliberate errors
        input.append("1\n");
        input.append("   \n");                // Bad name (blank)
        input.append("Gabriela Silang\n");    // Valid name
        input.append("0912345\n");            // Bad contact (too short)
        input.append("08123456789\n");        // Bad contact (doesn't start with 09)
        input.append("09170001122\n");        // Valid contact
        input.append("99\n");                 // Bad dorm
        input.append("1\n");                  // Valid dorm (1 = Villa Lourdes Dorm)
        input.append("   \n");                // Bad room
        input.append("501-A\n");              // Valid room -> C006 generated

        // 6. Create Order (Option 2) with deliberate errors
        input.append("2\n");
        input.append("C999\n");               // Customer not found
        input.append("2\n");                  // Try again
        input.append("C006\n");               // Valid customer Gabriela Silang
        input.append("-10\n");                // Bad weight (negative)
        input.append("abc\n");                // Bad weight (letters)
        input.append("0\n");                  // Bad weight (zero)
        input.append("10.5\n");               // Valid weight -> 2 loads -> Php 440
        input.append("1\n");                  // Receive: Drop-off
        input.append("2\n");                  // Return: Delivery
        input.append("bad_time\n");           // Bad deadline
        input.append("25:99\n");              // Bad deadline
        input.append("16:30\n");              // Valid deadline
        input.append("1\n");                  // Pay Option: Pay Now
        input.append("1\n");                  // Method: Cash -> LDY0011 WAITING

        // 7. Process Next Order (Option 3)
        input.append("3\n");                  // Processes LDY0002 -> PROCESSING
        input.append("3\n");                  // Processes LDY0003 -> PROCESSING
        input.append("3\n");                  // Processes LDY0011 -> PROCESSING
        input.append("3\n");                  // Queue now empty -> prints no orders safely

        // 8. Mark Order Ready (Option 4)
        input.append("4\n");
        input.append("LDY9999\n");            // Not found
        input.append("4\n");
        input.append("LDY0001\n");            // Already completed -> rejects non-processing
        input.append("4\n");
        input.append("LDY0002\n");            // PROCESSING -> marked READY

        // 9. Record Payment (Option 5)
        input.append("5\n");
        input.append("LDY9999\n");            // Not found
        input.append("5\n");
        input.append("LDY0001\n");            // Already paid -> rejects
        input.append("5\n");
        input.append("LDY0002\n");            // UNPAID -> prompt method
        input.append("2\n");                  // Online -> marks PAID

        // 10. Undo Last Action (Option 13)
        input.append("13\n");                 // Undoes payment of LDY0002 -> back to UNPAID
        input.append("13\n");                 // Undoes mark ready of LDY0002 -> back to PROCESSING

        // 11. Pickup Requests (Option 6)
        input.append("6\n1\nC001\n");         // 6a Log pickup request for C001
        input.append("6\n2\n");               // 6b Process next pickup (C003 from sample data)
        input.append("6\n2\n");               // 6b Process next pickup (C001)
        input.append("6\n2\n");               // 6b Queue empty -> handles safely

        // 12. Dispatch Trike & Deliver Next Stop (Options 7 & 8)
        input.append("7\n");                  // Dispatch Trike Run #1 (9 loads, 3 stops)
        input.append("8\n");                  // Deliver Stop 1 (Green Residences)
        input.append("8\n");                  // Deliver Stop 2 (Lipa Heights)
        input.append("8\n");                  // Deliver Stop 3 (St. Joseph Hall, collects cash)
        input.append("8\n");                  // Trike empty -> handles safely
        input.append("7\n");                  // Dispatch Run #2 (remaining Villa Lourdes orders)
        input.append("8\n");                  // Deliver Run #2 stop

        // 13. Claim Order (Option 9)
        input.append("9\nLDY9999\n");         // Not found
        input.append("9\nLDY0004\n");         // Not ready -> rejects
        input.append("9\nLDY0005\n");         // READY, CLAIM, UNPAID -> collects payment
        input.append("1\n");                  // Cash payment -> marks COMPLETED

        // 14. System Reports (Option 12)
        input.append("12\n1\n");              // 12a Pending and ready
        input.append("12\n2\n");              // 12b Unpaid
        input.append("12\n3\n");              // 12c Daily sales
        input.append("12\n4\n");              // 12d Delivery runs log
        input.append("12\n5\n");              // 12e Late orders
        input.append("12\n6\n");              // View all reports

        // 15. Exit Program (Option 15)
        input.append("15\n");

        // Redirect System.in to simulated input stream
        ByteArrayInputStream testIn = new ByteArrayInputStream(input.toString().getBytes(StandardCharsets.UTF_8));
        System.setIn(testIn);

        try {
            Main.main(new String[0]);
            System.out.println("\n==================================================");
            System.out.println("  END-TO-END TEST COMPLETED WITHOUT ANY CRASHES!  ");
            System.out.println("==================================================");
        } catch (Exception e) {
            System.err.println("CRASH DETECTED DURING TEST: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
