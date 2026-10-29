import java.util.Scanner;

/**
 * Driver class for the QMAR Laundry Order Management System.
 *
 * Responsibilities:
 * - Serves as the program entry point (main).
 * - Creates exactly one LaundrySystem controller instance and one Scanner.
 * - Displays the 15-option console menu.
 * - Robustly handles input parsing with try-catch (NumberFormatException).
 * - Dispatches user selections to the controller via a switch statement.
 * - Contains NO business logic, strictly adhering to Separation of Concerns.
 */
public class Main {

    /**
     * Program entry point. Initializes scanner and controller, then enters the menu loop.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LaundrySystem system = new LaundrySystem(scanner);

        System.out.println("=======================================================================");
        System.out.println("         WELCOME TO QMAR LAUNDRY ORDER MANAGEMENT SYSTEM               ");
        System.out.println("            Villa Lourdes, Brgy. Mataas na Lupa, Lipa City             ");
        System.out.println("=======================================================================");

        boolean running = true;
        while (running && scanner.hasNextLine()) {
            displayMenu();
            System.out.print("Enter your choice (1-15): ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String input = scanner.nextLine().trim();

            int choice = -1;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("\n>> Invalid input! Please enter a numeric option between 1 and 15.\n");
                continue;
            }

            try {
                switch (choice) {
                    case 1:
                        system.addCustomer();
                        break;
                    case 2:
                        system.createOrder();
                        break;
                    case 3:
                        system.processNextOrder();
                        break;
                    case 4:
                        system.markOrderReady();
                        break;
                    case 5:
                        system.recordPayment();
                        break;
                    case 6:
                        system.managePickupRequests();
                        break;
                    case 7:
                        system.dispatchTrike();
                        break;
                    case 8:
                        system.deliverNextStop();
                        break;
                    case 9:
                        system.claimOrder();
                        break;
                    case 10:
                        system.searchMenu();
                        break;
                    case 11:
                        system.viewOrders();
                        break;
                    case 12:
                        system.showReports();
                        break;
                    case 13:
                        system.undoLastAction();
                        break;
                    case 14:
                        system.loadSampleData();
                        break;
                    case 15:
                        running = false;
                        System.out.println("\n=======================================================================");
                        System.out.println("    Thank you for using QMAR Laundry Order Management System. Goodbye! ");
                        System.out.println("=======================================================================");
                        break;
                    default:
                        System.out.println("\n>> Invalid choice! Option must be between 1 and 15.\n");
                        break;
                }
            } catch (java.util.NoSuchElementException e) {
                System.out.println("\n>> Input stream ended. Exiting application.");
                running = false;
            }
        }

        scanner.close();
    }

    /**
     * Displays the 15-option main menu to the shop staff.
     */
    public static void displayMenu() {
        System.out.println("\n=======================================================================");
        System.out.println("                    MAIN MENU - QMAR LAUNDRY SHOP                      ");
        System.out.println("=======================================================================");
        System.out.println("  [1]  Add Customer");
        System.out.println("  [2]  Create Order");
        System.out.println("  [3]  Process Next Order (Washing Line)");
        System.out.println("  [4]  Mark Order Ready");
        System.out.println("  [5]  Record Payment");
        System.out.println("  [6]  Pickup Requests (Enqueue / Dequeue)");
        System.out.println("  [7]  Dispatch Trike (Delivery Routing & Capacity Cap)");
        System.out.println("  [8]  Deliver Next Stop (LIFO Trike Unloading)");
        System.out.println("  [9]  Claim Order");
        System.out.println("  [10] Search (Customer / Order)");
        System.out.println("  [11] View Orders (Multi-Key Sort)");
        System.out.println("  [12] System Reports (Pending/Unpaid/Sales/Delivery/Late)");
        System.out.println("  [13] Undo Last Action (Payment / Mark Ready)");
        System.out.println("  [14] Load Sample Data");
        System.out.println("  [15] Exit");
        System.out.println("=======================================================================");
    }
}
