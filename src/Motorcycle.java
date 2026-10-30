import java.math.BigDecimal;

public class Motorcycle extends Vehicle implements Maintainable {
    public static final BigDecimal HELMET_PER_DAY = new BigDecimal("5.00");
    private final int engineCapacityCc;

    public Motorcycle(String id, String brand, String model, int manufacturingYear,
                      BigDecimal dailyRentalPrice, int engineCapacityCc) {
        super(id, brand, model, manufacturingYear, dailyRentalPrice);
        if (engineCapacityCc <= 0) throw new IllegalArgumentException("Engine capacity must be positive.");
        this.engineCapacityCc = engineCapacityCc;
    }

    public Motorcycle(String id, String brand, String model, int manufacturingYear, BigDecimal dailyRentalPrice) {
        this(id, brand, model, manufacturingYear, dailyRentalPrice, 150);
    }

    public int getEngineCapacityCc() { return engineCapacityCc; }

    @Override
    public String getOptionalServicePrompt() { return "Include helmet for $5/day? (y/n): "; }

    @Override
    public String describeOptionalService(boolean selected) { return selected ? "Helmet" : "None"; }

    @Override
    public BigDecimal calculateRentalCost(int rentalDays, boolean helmetIncluded) {
        BigDecimal total = baseRentalCost(rentalDays);
        return helmetIncluded ? total.add(HELMET_PER_DAY.multiply(BigDecimal.valueOf(rentalDays))) : total;
    }

    @Override
    public boolean needsMaintenance() { return hasMaintenanceFlag(); }

    @Override
    public void performMaintenance() {
        finishMaintenance("Chain, brakes, tires, and engine inspection completed.");
    }

    @Override
    public String getVehicleType() { return "Motorcycle"; }

    @Override
    public String displayDetails() {
        return String.format("Motorcycle [ID=%s, %s %s, year=%d, engine=%dcc, daily=%s, status=%s, last maintenance=%s]",
                getId(), getBrand(), getModel(), getManufacturingYear(), engineCapacityCc,
                formatMoney(getDailyRentalPrice()), getAvailabilityDescription(), getLastMaintenanceRecord());
    }
}
