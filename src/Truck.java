import java.math.BigDecimal;

public class Truck extends Vehicle implements Maintainable {
    private final BigDecimal maximumLoadTons;

    public Truck(String id, String brand, String model, int manufacturingYear,
                 BigDecimal dailyRentalPrice, BigDecimal maximumLoadTons) {
        super(id, brand, model, manufacturingYear, dailyRentalPrice);
        if (maximumLoadTons == null || maximumLoadTons.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Maximum load capacity must be positive.");
        }
        this.maximumLoadTons = maximumLoadTons;
    }

    public Truck(String id, String brand, String model, int manufacturingYear, BigDecimal dailyRentalPrice) {
        this(id, brand, model, manufacturingYear, dailyRentalPrice, new BigDecimal("5.00"));
    }

    public BigDecimal getMaximumLoadTons() { return maximumLoadTons; }

    @Override
    public String getOptionalServicePrompt() { return ""; }

    @Override
    public String describeOptionalService(boolean selected) { return "Fixed service fee ($50)"; }

    @Override
    public BigDecimal calculateRentalCost(int rentalDays, boolean optionalService) {
        if (optionalService) {
            throw new IllegalArgumentException("Trucks have a fixed service fee and no optional daily add-on.");
        }
        return baseRentalCost(rentalDays).add(TRUCK_SERVICE_FEE);
    }

    @Override
    public boolean needsMaintenance() { return hasMaintenanceFlag(); }

    @Override
    public void performMaintenance() {
        finishMaintenance("Load-bed, tires, brakes, and engine inspection completed.");
    }

    @Override
    public String getVehicleType() { return "Truck"; }

    @Override
    public String displayDetails() {
        return String.format("Truck [ID=%s, %s %s, year=%d, max load=%s tons, daily=%s, service fee=%s, status=%s, last maintenance=%s]",
                getId(), getBrand(), getModel(), getManufacturingYear(), maximumLoadTons.toPlainString(),
                formatMoney(getDailyRentalPrice()), formatMoney(TRUCK_SERVICE_FEE),
                getAvailabilityDescription(), getLastMaintenanceRecord());
    }
}
