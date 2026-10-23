/**
 * Enum representing the current stage of an order in the laundry lifecycle.
 * WAITING: Order created and waiting in washing queue.
 * PROCESSING: Currently being washed and dried.
 * READY: Clean and folded, ready for claim or delivery.
 * OUT_FOR_DELIVERY: Loaded onto the trike for delivery.
 * COMPLETED: Claimed by student or delivered and paid.
 */
public enum LaundryStatus {
    WAITING,
    PROCESSING,
    READY,
    OUT_FOR_DELIVERY,
    COMPLETED
}
