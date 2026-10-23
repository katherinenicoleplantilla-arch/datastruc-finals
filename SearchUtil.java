import java.util.List;

/**
 * Utility class providing searching algorithms for QMAR Laundry Shop.
 * Implements:
 * 1. Linear Search for Customer lookups (by Customer ID, Name, or Contact Number).
 * 2. Binary Search for Order lookups (by Order ID on a list maintained in sorted order).
 *
 * Conforms strictly to project rules: hand-coded algorithms with no library searching.
 */
public class SearchUtil {

    // Private constructor to prevent instantiation
    private SearchUtil() {}

    /**
     * Performs a linear search over the customers list to locate a customer
     * matching the given search query by Customer ID, Name, or Contact Number.
     * The search is case-insensitive and trims surrounding whitespace.
     *
     * Time Complexity: O(n) where n is the number of registered customers.
     * Space Complexity: O(1) auxiliary space.
     *
     * @param customers the list of registered customers
     * @param query     the search string (ID, name, or contact number)
     * @return the matched Customer object, or null if no match is found
     */
    public static Customer linearSearchCustomer(List<Customer> customers, String query) {
        if (customers == null || query == null) {
            return null;
        }
        String trimmed = query.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        // Iterate through the customers list one by one
        for (Customer c : customers) {
            if (c != null) {
                if ((c.getCustomerId() != null && c.getCustomerId().equalsIgnoreCase(trimmed)) ||
                    (c.getName() != null && c.getName().equalsIgnoreCase(trimmed)) ||
                    (c.getContactNumber() != null && c.getContactNumber().equalsIgnoreCase(trimmed))) {
                    return c; // Immediate match found
                }
            }
        }
        return null; // No match found after inspecting all elements
    }

    /**
     * Performs a binary search over the orders list to locate an order by its unique Order ID.
     * Assumes the list of orders is sorted in ascending Order ID order.
     * Uses low, high, and mid index pointers in a while (low <= high) loop.
     *
     * Time Complexity: O(log n) where n is the number of orders.
     * Space Complexity: O(1) auxiliary space.
     *
     * @param orders  the list of orders sorted by Order ID
     * @param orderId the Order ID to find (e.g. "LDY0001", case-insensitive)
     * @return the matched Order object, or null if not found
     */
    public static Order binarySearchOrder(List<Order> orders, String orderId) {
        if (orders == null || orderId == null) {
            return null;
        }
        String targetId = orderId.trim();
        if (targetId.isEmpty()) {
            return null;
        }

        int low = 0;
        int high = orders.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            Order midOrder = orders.get(mid);
            int cmp = midOrder.getOrderId().compareToIgnoreCase(targetId);

            if (cmp == 0) {
                return midOrder; // Target Order ID found
            } else if (cmp < 0) {
                low = mid + 1;   // Target lies in the upper half
            } else {
                high = mid - 1;  // Target lies in the lower half
            }
        }

        return null; // Target Order ID not found
    }
}
