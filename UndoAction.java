/**
 * Represents an undoable operation performed in QMAR Laundry Shop.
 * Pushed onto undoStack when an order is marked ready or when a payment is recorded.
 * Allows reversing the operation in LIFO order.
 */
public class UndoAction {
    public static final String PAYMENT = "PAYMENT";
    public static final String MARK_READY = "MARK_READY";

    private String type;           // Action type: PAYMENT or MARK_READY
    private Order order;           // The order affected by this action
    private String description;    // Human-readable summary of the undone action

    /**
     * Constructs a new UndoAction record.
     *
     * @param type        the type of action (PAYMENT or MARK_READY)
     * @param order       the affected order
     * @param description description shown when the action is undone
     */
    public UndoAction(String type, Order order, String description) {
        this.type = type;
        this.order = order;
        this.description = description;
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

    @Override
    public String toString() {
        return "UndoAction{" +
                "type='" + type + '\'' +
                ", orderId=" + (order != null ? order.getOrderId() : "null") +
                ", description='" + description + '\'' +
                '}';
    }
}
