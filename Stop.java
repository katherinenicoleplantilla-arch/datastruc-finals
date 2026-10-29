import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a single delivery stop for a specific dormitory during a trike delivery run.
 * Aggregates all orders scheduled for delivery to that dormitory and tracks their cumulative loads
 * to enforce the tricycle capacity limit (10 loads maximum per run).
 */
public class Stop {
    private String dorm;              // Dormitory/building name for this stop
    private List<Order> orders;       // Orders delivered together at this dorm (ArrayList)
    private int totalLoads;           // Sum of loads for all orders in this stop

    /**
     * Constructs a new Stop for the specified dormitory.
     *
     * @param dorm the dormitory name
     */
    public Stop(String dorm) {
        this.dorm = dorm;
        this.orders = new ArrayList<>();
        this.totalLoads = 0;
    }

    /**
     * Adds an order to this stop and updates the running load count.
     *
     * @param order the delivery order to add
     */
    public void addOrder(Order order) {
        if (order != null) {
            this.orders.add(order);
            this.totalLoads += order.getLoads();
        }
    }

    /**
     * Returns the earliest delivery deadline among all orders in this stop.
     *
     * @return earliest LocalTime deadline, or null if none of the orders have a deadline
     */
    public LocalTime getEarliestDeadline() {
        LocalTime earliest = null;
        for (Order o : orders) {
            if (o != null && o.getDeliveryDeadline() != null) {
                if (earliest == null || o.getDeliveryDeadline().isBefore(earliest)) {
                    earliest = o.getDeliveryDeadline();
                }
            }
        }
        return earliest;
    }

    /**
     * Returns the total loads aggregated at this stop.
     *
     * @return total loads
     */
    public int getTotalLoads() {
        return totalLoads;
    }

    public String getDorm() {
        return dorm;
    }

    public List<Order> getOrders() {
        return orders;
    }

    @Override
    public String toString() {
        return "Stop{" +
                "dorm='" + dorm + '\'' +
                ", ordersCount=" + orders.size() +
                ", totalLoads=" + totalLoads +
                '}';
    }
}
