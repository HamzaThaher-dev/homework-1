import java.math.BigDecimal;
import java.time.LocalDate;

public class Rental {
    public enum Status { ACTIVE, COMPLETED }

    private final String id;
    private final Customer customer;
    private final Vehicle vehicle;
    private final int rentalDays;
    private final BigDecimal totalCost;
    private final String serviceDescription;
    private final LocalDate startDate;
    private Status status;
    private LocalDate returnDate;

    public Rental(String id, Customer customer, Vehicle vehicle, int rentalDays,
                  BigDecimal totalCost, String serviceDescription) {
        this.id = requireText(id, "Rental ID");
        if (customer == null || vehicle == null) throw new IllegalArgumentException("Customer and vehicle are required.");
        if (rentalDays <= 0) throw new IllegalArgumentException("Rental days must be positive.");
        if (totalCost == null || totalCost.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Rental cost cannot be negative.");
        this.customer = customer;
        this.vehicle = vehicle;
        this.rentalDays = rentalDays;
        this.totalCost = totalCost;
        this.serviceDescription = serviceDescription == null || serviceDescription.isBlank() ? "None" : serviceDescription.trim();
        this.startDate = LocalDate.now();
        this.status = Status.ACTIVE;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(fieldName + " cannot be empty.");
        return value.trim();
    }

    public String getId() { return id; }
    public Customer getCustomer() { return customer; }
    public Vehicle getVehicle() { return vehicle; }
    public int getRentalDays() { return rentalDays; }
    public BigDecimal getTotalCost() { return totalCost; }
    public String getServiceDescription() { return serviceDescription; }
    public LocalDate getStartDate() { return startDate; }
    public Status getStatus() { return status; }
    public LocalDate getReturnDate() { return returnDate; }

    public void complete(LocalDate completedOn) {
        if (status != Status.ACTIVE) throw new IllegalStateException("This rental has already been completed.");
        status = Status.COMPLETED;
        returnDate = completedOn == null ? LocalDate.now() : completedOn;
    }

    @Override
    public String toString() {
        return String.format("Rental [ID=%s, customer=%s (%s), vehicle=%s (%s), days=%d, service=%s, cost=$%.2f, status=%s, start=%s, return=%s]",
                id, customer.getFullName(), customer.getId(), vehicle.getId(), vehicle.getVehicleType(),
                rentalDays, serviceDescription, totalCost, status, startDate,
                returnDate == null ? "—" : returnDate);
    }
}
