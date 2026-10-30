import java.math.BigDecimal;
import java.math.RoundingMode;

public abstract class Vehicle implements Maintainable {
    public static final BigDecimal TRUCK_SERVICE_FEE = new BigDecimal("50.00");
    private static int vehiclesCreated;

    private final String id;
    private final String brand;
    private final String model;
    private final int manufacturingYear;
    private BigDecimal dailyRentalPrice;
    private boolean currentlyRented;
    private boolean maintenanceRequired;
    private String lastMaintenanceRecord = "None";

    protected Vehicle(String id, String brand, String model, int manufacturingYear,
                      BigDecimal dailyRentalPrice) {
        this.id = requireText(id, "Vehicle ID");
        this.brand = requireText(brand, "Brand");
        this.model = requireText(model, "Model");
        if (manufacturingYear < 1886 || manufacturingYear > 2100) {
            throw new IllegalArgumentException("Manufacturing year must be between 1886 and 2100.");
        }
        this.manufacturingYear = manufacturingYear;
        setDailyRentalPrice(dailyRentalPrice);
        vehiclesCreated++;
    }

    protected static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty.");
        }
        return value.trim();
    }

    public final String getId() { return id; }
    public final String getBrand() { return brand; }
    public final String getModel() { return model; }
    public final int getManufacturingYear() { return manufacturingYear; }
    public final BigDecimal getDailyRentalPrice() { return dailyRentalPrice; }
    public final boolean isCurrentlyRented() { return currentlyRented; }
    public final boolean isMaintenanceRequired() { return maintenanceRequired; }
    public final String getLastMaintenanceRecord() { return lastMaintenanceRecord; }

    public final boolean isAvailable() {
        return !currentlyRented && !maintenanceRequired;
    }

    public final void setDailyRentalPrice(BigDecimal dailyRentalPrice) {
        if (dailyRentalPrice == null || dailyRentalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Daily rental price must be greater than zero.");
        }
        this.dailyRentalPrice = dailyRentalPrice.setScale(2, RoundingMode.HALF_UP);
    }

    public final void markRented() {
        if (!isAvailable()) {
            throw new IllegalStateException("This vehicle is not available for rental.");
        }
        currentlyRented = true;
    }

    public final void completeRental() {
        if (!currentlyRented) {
            throw new IllegalStateException("This vehicle has no active rental.");
        }
        currentlyRented = false;
    }

    public final void flagForMaintenance() {
        maintenanceRequired = true;
    }

    protected final boolean hasMaintenanceFlag() {
        return maintenanceRequired;
    }

    protected final BigDecimal baseRentalCost(int rentalDays) {
        if (rentalDays <= 0) {
            throw new IllegalArgumentException("Rental days must be positive.");
        }
        return dailyRentalPrice.multiply(BigDecimal.valueOf(rentalDays));
    }

    protected final void finishMaintenance(String workDescription) {
        if (currentlyRented) {
            throw new IllegalStateException("Maintenance cannot be performed during an active rental.");
        }
        if (!maintenanceRequired) {
            throw new IllegalStateException("Flag this vehicle for maintenance first.");
        }
        lastMaintenanceRecord = requireText(workDescription, "Maintenance record");
        maintenanceRequired = false;
    }

    public static int getVehiclesCreated() {
        return vehiclesCreated;
    }

    public final String getAvailabilityDescription() {
        if (currentlyRented) return "Rented";
        if (maintenanceRequired) return "Maintenance required";
        return "Available";
    }

    public final String formatMoney(BigDecimal amount) {
        return "$" + amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    public abstract BigDecimal calculateRentalCost(int rentalDays, boolean optionalService);

    public BigDecimal calculateRentalCost(int rentalDays) {
        return calculateRentalCost(rentalDays, false);
    }

    public abstract String getVehicleType();
    public abstract String displayDetails();
    public abstract String getOptionalServicePrompt();
    public abstract String describeOptionalService(boolean selected);
}
