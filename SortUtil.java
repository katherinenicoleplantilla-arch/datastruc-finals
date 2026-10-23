import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class implementing a custom recursive Merge Sort algorithm for Order objects.
 *
 * Algorithm details:
 * - Divide and conquer strategy: recursively splits the input list into halves.
 * - Base case: a list of size 0 or 1 is already sorted.
 * - Merge phase: iterates through both sublists using a while loop, picking the smaller element
 *   (or left on tie, ensuring stability), then appends any remaining items.
 * - Out-of-place: returns a newly allocated ArrayList and never mutates the source list.
 * - Time Complexity: O(n log n) in all cases (worst, average, best).
 * - Space Complexity: O(n) auxiliary space for sublists.
 *
 * Conforms strictly to the project rules: does NOT use Collections.sort, List.sort,
 * Arrays.sort, or stream sorting.
 */
public class SortUtil {

    // Private constructor to prevent instantiation
    private SortUtil() {}

    /**
     * Comparator for sorting orders by Total Amount ascending.
     */
    public static final Comparator<Order> BY_TOTAL_AMOUNT = (o1, o2) -> {
        double a1 = (o1 != null) ? o1.getTotalAmount() : 0.0;
        double a2 = (o2 != null) ? o2.getTotalAmount() : 0.0;
        return Double.compare(a1, a2);
    };

    /**
     * Comparator for sorting orders by Customer Name alphabetically (case-insensitive).
     */
    public static final Comparator<Order> BY_CUSTOMER_NAME = (o1, o2) -> {
        String n1 = (o1 != null && o1.getCustomer() != null && o1.getCustomer().getName() != null)
                ? o1.getCustomer().getName() : "";
        String n2 = (o2 != null && o2.getCustomer() != null && o2.getCustomer().getName() != null)
                ? o2.getCustomer().getName() : "";
        return n1.compareToIgnoreCase(n2);
    };

    /**
     * Comparator for sorting orders chronologically by Time Received.
     */
    public static final Comparator<Order> BY_TIME_RECEIVED = (o1, o2) -> {
        if (o1 == null || o1.getTimeReceived() == null) {
            return (o2 == null || o2.getTimeReceived() == null) ? 0 : 1;
        }
        if (o2 == null || o2.getTimeReceived() == null) {
            return -1;
        }
        return o1.getTimeReceived().compareTo(o2.getTimeReceived());
    };

    /**
     * Comparator for sorting orders by Delivery Deadline ascending (nulls last).
     */
    public static final Comparator<Order> BY_DELIVERY_DEADLINE = (o1, o2) -> {
        LocalTime t1 = (o1 != null) ? o1.getDeliveryDeadline() : null;
        LocalTime t2 = (o2 != null) ? o2.getDeliveryDeadline() : null;
        if (t1 == null && t2 == null) {
            return 0;
        }
        if (t1 == null) {
            return 1;
        }
        if (t2 == null) {
            return -1;
        }
        return t1.compareTo(t2);
    };

    /**
     * Comparator for sorting orders alphabetically by customer's Dormitory name (case-insensitive).
     */
    public static final Comparator<Order> BY_DORM = (o1, o2) -> {
        String d1 = (o1 != null && o1.getCustomer() != null && o1.getCustomer().getDorm() != null)
                ? o1.getCustomer().getDorm() : "";
        String d2 = (o2 != null && o2.getCustomer() != null && o2.getCustomer().getDorm() != null)
                ? o2.getCustomer().getDorm() : "";
        return d1.compareToIgnoreCase(d2);
    };

    // Helper accessor methods matching comparator names
    public static Comparator<Order> byTotalAmount() { return BY_TOTAL_AMOUNT; }
    public static Comparator<Order> byCustomerName() { return BY_CUSTOMER_NAME; }
    public static Comparator<Order> byTimeReceived() { return BY_TIME_RECEIVED; }
    public static Comparator<Order> byDeliveryDeadline() { return BY_DELIVERY_DEADLINE; }
    public static Comparator<Order> byDorm() { return BY_DORM; }

    /**
     * Recursively sorts a list of Order objects using Merge Sort with the given Comparator.
     *
     * @param list       the input list of orders (unmodified)
     * @param comparator the ordering criteria
     * @return a new List containing the elements in sorted order
     */
    public static List<Order> mergeSort(List<Order> list, Comparator<Order> comparator) {
        // Base case: list of size 0 or 1 is already sorted
        if (list == null || list.size() <= 1) {
            return (list == null) ? new ArrayList<>() : new ArrayList<>(list);
        }

        // Divide: split list into left and right halves
        int mid = list.size() / 2;
        List<Order> left = new ArrayList<>();
        for (int i = 0; i < mid; i++) {
            left.add(list.get(i));
        }
        List<Order> right = new ArrayList<>();
        for (int i = mid; i < list.size(); i++) {
            right.add(list.get(i));
        }

        // Conquer: recursively sort each half
        List<Order> sortedLeft = mergeSort(left, comparator);
        List<Order> sortedRight = mergeSort(right, comparator);

        // Combine: merge the sorted halves
        return merge(sortedLeft, sortedRight, comparator);
    }

    /**
     * Merges two sorted lists into a single sorted list.
     * Preserves stability by prioritizing elements from the left list on equality.
     *
     * @param left       the sorted left sublist
     * @param right      the sorted right sublist
     * @param comparator the comparator used for ordering
     * @return merged sorted list
     */
    private static List<Order> merge(List<Order> left, List<Order> right, Comparator<Order> comparator) {
        List<Order> merged = new ArrayList<>();
        int i = 0;
        int j = 0;

        // Compare elements and add the smaller one
        while (i < left.size() && j < right.size()) {
            if (comparator.compare(left.get(i), right.get(j)) <= 0) {
                merged.add(left.get(i));
                i++;
            } else {
                merged.add(right.get(j));
                j++;
            }
        }

        // Append any remaining elements from the left sublist
        while (i < left.size()) {
            merged.add(left.get(i));
            i++;
        }

        // Append any remaining elements from the right sublist
        while (j < right.size()) {
            merged.add(right.get(j));
            j++;
        }

        return merged;
    }
}
