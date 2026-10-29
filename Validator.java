import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Utility class providing static input validation methods for the system.
 * Enforces business rules for customer information, laundry weights, dorm locations,
 * and delivery deadlines.
 */
public class Validator {

    // Private constructor to prevent instantiation of utility class
    private Validator() {}

    /**
     * Validates that a customer name is non-null and not empty after trimming.
     *
     * @param name customer name
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        return name != null && !name.trim().isEmpty();
    }

    /**
     * Validates that a contact number is exactly 11 digits starting with "09".
     *
     * @param contact contact number string
     * @return true if valid, false otherwise
     */
    public static boolean isValidContact(String contact) {
        if (contact == null) {
            return false;
        }
        String trimmed = contact.trim();
        return trimmed.matches("^09\\d{9}$");
    }

    /**
     * Validates that a custom dormitory name is non-null and not empty after trimming.
     * Allows staff to add and use custom dormitories freely.
     *
     * @param dorm dormitory name entered by staff
     * @return true if non-empty, false otherwise
     */
    public static boolean isValidDorm(String dorm) {
        return dorm != null && !dorm.trim().isEmpty();
    }

    /**
     * Validates that a dormitory name exists in the provided array of dorms.
     *
     * @param dorm      dormitory name to check
     * @param dormList  array of valid dorm names
     * @return true if found, false otherwise
     */
    public static boolean isValidDorm(String dorm, String[] dormList) {
        if (dorm == null || dormList == null) {
            return false;
        }
        String trimmed = dorm.trim();
        for (String d : dormList) {
            if (d != null && d.equalsIgnoreCase(trimmed)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates that a dormitory name exists in the provided list of dorms.
     *
     * @param dorm      dormitory name to check
     * @param dormList  list of valid dorm names
     * @return true if found, false otherwise
     */
    public static boolean isValidDorm(String dorm, List<String> dormList) {
        if (dorm == null || dormList == null) {
            return false;
        }
        String trimmed = dorm.trim();
        for (String d : dormList) {
            if (d != null && d.equalsIgnoreCase(trimmed)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Validates that a room number is non-null and not empty after trimming.
     *
     * @param room room number string
     * @return true if valid, false otherwise
     */
    public static boolean isValidRoom(String room) {
        return room != null && !room.trim().isEmpty();
    }

    public static final double MAX_WEIGHT = 1000.0; // Maximum allowed laundry weight in kg

    /**
     * Validates that a laundry weight is strictly greater than 0, is finite,
     * and does not exceed the upper bound (MAX_WEIGHT).
     *
     * @param weight weight in kilograms
     * @return true if weight > 0, finite, and <= MAX_WEIGHT; false otherwise
     */
    public static boolean isValidWeight(double weight) {
        return Double.isFinite(weight) && weight > 0.0 && weight <= MAX_WEIGHT;
    }

    /**
     * Validates a string representation of weight against a strict plain-number format
     * and ensures the parsed value is finite, positive, and within the upper bound.
     *
     * Rejects nonsense inputs like "Infinity", "NaN", exponents ("1e300"),
     * suffixes ("5d"), and hex floats ("0x1p4").
     *
     * @param weightStr raw string input for weight
     * @return true if valid plain number > 0 and <= MAX_WEIGHT, false otherwise
     */
    public static boolean isValidWeight(String weightStr) {
        if (weightStr == null) {
            return false;
        }
        String trimmed = weightStr.trim();
        // Plain positive integer or decimal format only (e.g., "5", "5.5", "0.5")
        if (!trimmed.matches("^\\d+(\\.\\d+)?$")) {
            return false;
        }
        try {
            double w = Double.parseDouble(trimmed);
            return isValidWeight(w);
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validates an optional delivery deadline in HH:mm format.
     * Catches DateTimeParseException when format or time range is invalid.
     *
     * @param deadline string representation of time (e.g. "14:30" or "9:30")
     * @return true if valid time format, false otherwise
     */
    public static boolean isValidDeadline(String deadline) {
        if (deadline == null || deadline.trim().isEmpty()) {
            return false;
        }
        String trimmed = deadline.trim();
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalTime.parse(trimmed, formatter);
            return true;
        } catch (DateTimeParseException e) {
            try {
                // Also accept single-digit hour like "9:00"
                DateTimeFormatter flexible = DateTimeFormatter.ofPattern("H:mm");
                LocalTime.parse(trimmed, flexible);
                return true;
            } catch (DateTimeParseException e2) {
                return false;
            }
        }
    }
}
