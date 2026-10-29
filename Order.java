import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single laundry order at QMAR Laundry Shop.
 * Encapsulates the order details, customer reference, weight, computed loads,
 * computed total amount, methods, statuses, and tracking timestamps.
 *
 * In accordance with encapsulation, loads and total amount are computed strictly
 * inside this class and cannot be modified externally.
 */
public class Order {
    private String orderId;                     // LDY followed by 4 digits, auto-generated
    private Customer customer;                  // Existing customer who owns the order
    private double weightKg;                    // Actual weight in kg (> 0)
    private int loads;                          // (int) Math.ceil(weightKg / 7.0)
    private double totalAmount;                 // loads * 220.0
    private ReceiveMethod receiveMethod;        // Drop-off or Pickup
    private ReturnMethod returnMethod;          // Claim or Delivery
    private LocalTime deliveryDeadline;         // Optional; only for Delivery
    private LaundryStatus laundryStatus;        // Waiting, Processing, Ready, Out for Delivery, Completed
    private PaymentStatus paymentStatus;        // Unpaid or Paid
    private PaymentMethod paymentMethod;        // Cash or Online (null if unpaid)
    private LocalDateTime timeReceived;         // System timestamp when order was logged
    private LocalDateTime timeReady;            // System timestamp when order became Ready

    /**
     * Primary constructor for a new incoming order.
     */
    public Order(String orderId, Customer customer, double weightKg,
                 ReceiveMethod receiveMethod, ReturnMethod returnMethod,
                 LocalTime deliveryDeadline, PaymentStatus paymentStatus,
                 PaymentMethod paymentMethod) {
        if (!Validator.isValidWeight(weightKg)) {
            throw new IllegalArgumentException("Invalid weight: " + weightKg);
        }
        this.orderId = orderId;
        this.customer = customer;
        this.weightKg = weightKg;
        this.loads = computeLoads();
        this.totalAmount = computeTotal();
        this.receiveMethod = receiveMethod;
        this.returnMethod = returnMethod;
        this.deliveryDeadline = deliveryDeadline;
        this.laundryStatus = LaundryStatus.WAITING;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.timeReceived = LocalDateTime.now();
        this.timeReady = null;
    }

    /**
     * Overloaded constructor supporting full initialization (e.g., for sample data simulation).
     */
    public Order(String orderId, Customer customer, double weightKg,
                 ReceiveMethod receiveMethod, ReturnMethod returnMethod,
                 LocalTime deliveryDeadline, LaundryStatus laundryStatus,
                 PaymentStatus paymentStatus, PaymentMethod paymentMethod,
                 LocalDateTime timeReceived, LocalDateTime timeReady) {
        if (!Validator.isValidWeight(weightKg)) {
            throw new IllegalArgumentException("Invalid weight: " + weightKg);
        }
        this.orderId = orderId;
        this.customer = customer;
        this.weightKg = weightKg;
        this.loads = computeLoads();
        this.totalAmount = computeTotal();
        this.receiveMethod = receiveMethod;
        this.returnMethod = returnMethod;
        this.deliveryDeadline = deliveryDeadline;
        this.laundryStatus = laundryStatus;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.timeReceived = timeReceived;
        this.timeReady = timeReady;
    }

    /**
     * Computes the number of loads based on weight (7.0 kg per load, rounded up).
     *
     * @return number of loads
     */
    public int computeLoads() {
        return (int) Math.ceil(this.weightKg / 7.0);
    }

    /**
     * Computes total bill based on computed loads (Php 220.0 per load).
     *
     * @return total amount in Php
     */
    public double computeTotal() {
        return this.loads * 220.0;
    }

    // --- Getters and Setters ---

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        if (!Validator.isValidWeight(weightKg)) {
            throw new IllegalArgumentException("Invalid weight: " + weightKg);
        }
        this.weightKg = weightKg;
        this.loads = computeLoads();
        this.totalAmount = computeTotal();
    }

    public int getLoads() {
        return loads;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public ReceiveMethod getReceiveMethod() {
        return receiveMethod;
    }

    public void setReceiveMethod(ReceiveMethod receiveMethod) {
        this.receiveMethod = receiveMethod;
    }

    public ReturnMethod getReturnMethod() {
        return returnMethod;
    }

    public void setReturnMethod(ReturnMethod returnMethod) {
        this.returnMethod = returnMethod;
    }

    public LocalTime getDeliveryDeadline() {
        return deliveryDeadline;
    }

    public void setDeliveryDeadline(LocalTime deliveryDeadline) {
        this.deliveryDeadline = deliveryDeadline;
    }

    public LaundryStatus getLaundryStatus() {
        return laundryStatus;
    }

    public void setLaundryStatus(LaundryStatus laundryStatus) {
        this.laundryStatus = laundryStatus;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getTimeReceived() {
        return timeReceived;
    }

    public void setTimeReceived(LocalDateTime timeReceived) {
        this.timeReceived = timeReceived;
    }

    public LocalDateTime getTimeReady() {
        return timeReady;
    }

    public void setTimeReady(LocalDateTime timeReady) {
        this.timeReady = timeReady;
    }

    @Override
    public String toString() {
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String deadlineStr = (deliveryDeadline != null) ? deliveryDeadline.format(timeFmt) : "N/A";
        String readyStr = (timeReady != null) ? timeReady.format(dtFmt) : "N/A";
        String recvStr = (timeReceived != null) ? timeReceived.format(dtFmt) : "N/A";

        return String.format("[%s] %s | %.1f kg (%d loads) | Php %.2f | %s -> %s (Deadline: %s) | Status: %s | Payment: %s (%s) | Recv: %s | Ready: %s",
                orderId,
                (customer != null ? customer.getName() + " (" + customer.getDorm() + " R" + customer.getRoomNumber() + ")" : "Unknown"),
                weightKg, loads, totalAmount,
                receiveMethod, returnMethod, deadlineStr,
                laundryStatus,
                paymentStatus, (paymentMethod != null ? paymentMethod : "None"),
                recvStr, readyStr);
    }
}
