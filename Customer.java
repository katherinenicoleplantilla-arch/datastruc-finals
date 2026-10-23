/**
 * Represents a registered customer (student) of QMAR Laundry Shop.
 * Encapsulates student information including dormitory address and contact details.
 */
public class Customer {
    private String customerId;     // C followed by 3 digits, auto-generated (e.g. C001)
    private String name;           // Student's full name (non-empty)
    private String contactNumber;  // 11 digits starting with 09
    private String dorm;           // Student's dormitory or residence hall name
    private String roomNumber;     // Room number (non-empty)

    /**
     * Constructs a new Customer instance.
     *
     * @param customerId    the unique auto-generated customer ID
     * @param name          the customer's name
     * @param contactNumber the customer's 11-digit mobile number
     * @param dorm          the dormitory or residence hall name
     * @param roomNumber    the room number within the dormitory
     */
    public Customer(String customerId, String name, String contactNumber, String dorm, String roomNumber) {
        this.customerId = customerId;
        this.name = name;
        this.contactNumber = contactNumber;
        this.dorm = dorm;
        this.roomNumber = roomNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getDorm() {
        return dorm;
    }

    public void setDorm(String dorm) {
        this.dorm = dorm;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId='" + customerId + '\'' +
                ", name='" + name + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                ", dorm='" + dorm + '\'' +
                ", roomNumber='" + roomNumber + '\'' +
                '}';
    }
}
