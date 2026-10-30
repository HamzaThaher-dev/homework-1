import java.math.BigDecimal;

public class Car extends Vehicle implements Maintainable {
    public static final BigDecimal INSURANCE_PER_DAY = new BigDecimal("15.00");
    private final int seatCount;
    private final String transmissionType;

    public Car(String id, String brand, String model, int manufacturingYear,
               BigDecimal dailyRentalPrice, int seatCount, String transmissionType) {
        super(id, brand, model, manufacturingYear, dailyRentalPrice);
        if (seatCount < 1) throw new IllegalArgumentException("A car must have at least one seat.");
        this.seatCount = seatCount;
        this.transmissionType = requireText(transmissionType, "Transmission type");
    }

    public Car(String id, String brand, String model, int manufacturingYear, BigDecimal dailyRentalPrice) {
        this(id, brand, model, manufacturingYear, dailyRentalPrice, 5, "Automatic");
    }

    public int getSeatCount() { return seatCount; }
    public String getTransmissionType() { return transmissionType; }

    @Override
    public String getOptionalServicePrompt() { return "Add insurance for $15/day? (y/n): "; }

    @Override
    public String describeOptionalService(boolean selected) { return selected ? "Insurance" : "None"; }

    @Override
    public BigDecimal calculateRentalCost(int rentalDays, boolean insurance) {
        BigDecimal total = baseRentalCost(rentalDays);
        return insurance ? total.add(INSURANCE_PER_DAY.multiply(BigDecimal.valueOf(rentalDays))) : total;
    }

    @Override
    public boolean needsMaintenance() { return hasMaintenanceFlag(); }

    @Override
    public void performMaintenance() {
        finishMaintenance("Oil, brake, tire, and safety inspection completed.");
    }

    @Override
    public String getVehicleType() { return "Car"; }

    @Override
    public String displayDetails() {
        return String.format("Car [ID=%s, %s %s, year=%d, seats=%d, transmission=%s, daily=%s, status=%s, last maintenance=%s]",
                getId(), getBrand(), getModel(), getManufacturingYear(), seatCount, transmissionType,
                formatMoney(getDailyRentalPrice()), getAvailabilityDescription(), getLastMaintenanceRecord());
    }
}
