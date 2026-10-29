/**
 * Represents an undoable operation performed in QMAR Laundry Shop.
 * Pushed onto undoStack when an order is marked ready or when a payment is recorded.
 * Allows reversing the operation in LIFO order.
 */
public class UndoAction {
    public static final String PAYMENT = "PAYMENT";
    public static final String MARK_READY = "MARK_READY";

    private String type;                           // Action type: PAYMENT or MARK_READY
    private Order order;                           // The order affected by this action
    private String description;                    // Human-readable summary of the undone action
    private LaundryStatus expectedLaundryStatus;   // Laundry status produced/expected when action was logged
    private PaymentStatus expectedPaymentStatus;   // Payment status produced/expected when action was logged

    /**
     * Constructs a new UndoAction record.
     * Captures the order's current laundry and payment statuses as expected statuses for undo validation.
     *
     * @param type        the type of action (PAYMENT or MARK_READY)
     * @param order       the affected order
     * @param description description shown when the action is undone
     */
    public UndoAction(String type, Order order, String description) {
        this.type = type;
        this.order = order;
        this.description = description;
        this.expectedLaundryStatus = (order != null) ? order.getLaundryStatus() : null;
        this.expectedPaymentStatus = (order != null) ? order.getPaymentStatus() : null;
    }

    /**
     * Overloaded constructor allowing explicit definition of expected statuses.
     *
     * @param type                  the type of action
     * @param order                 the affected order
     * @param description           description shown when the action is undone
     * @param expectedLaundryStatus expected laundry status at time of undo
     * @param expectedPaymentStatus expected payment status at time of undo
     */
    public UndoAction(String type, Order order, String description,
                      LaundryStatus expectedLaundryStatus, PaymentStatus expectedPaymentStatus) {
        this.type = type;
        this.order = order;
        this.description = description;
        this.expectedLaundryStatus = expectedLaundryStatus;
        this.expectedPaymentStatus = expectedPaymentStatus;
    }

    public String getType() {
        return type;
    }

    public Order getOrder() {
        return order;
    }

    public String getDescription() {
        return description;
    }

    public LaundryStatus getExpectedLaundryStatus() {
        return expectedLaundryStatus;
    }

    public PaymentStatus getExpectedPaymentStatus() {
        return expectedPaymentStatus;
    }

    @Override
    public String toString() {
        return "UndoAction{" +
                "type='" + type + '\'' +
                ", orderId=" + (order != null ? order.getOrderId() : "null") +
                ", description='" + description + '\'' +
                ", expectedLaundryStatus=" + expectedLaundryStatus +
                ", expectedPaymentStatus=" + expectedPaymentStatus +
                '}';
    }
}
