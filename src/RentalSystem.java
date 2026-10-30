import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RentalSystem {
    private final ArrayList<Vehicle> vehicles = new ArrayList<>();
    private final ArrayList<Customer> customers = new ArrayList<>();
    private final ArrayList<Rental> rentals = new ArrayList<>();
    private int nextRentalNumber = 1;

    public void addVehicle(Vehicle vehicle) {
        if (vehicle == null) throw new IllegalArgumentException("Vehicle is required.");
        if (searchVehicle(vehicle.getId()) != null) throw new IllegalArgumentException("A vehicle with that ID already exists.");
        vehicles.add(vehicle);
    }

    public void addCustomer(Customer customer) {
        if (customer == null) throw new IllegalArgumentException("Customer is required.");
        if (searchCustomer(customer.getId()) != null) throw new IllegalArgumentException("A customer with that ID already exists.");
        customers.add(customer);
    }

    public Vehicle searchVehicle(String id) {
        if (id == null) return null;
        for (Vehicle vehicle : vehicles) {
            if (vehicle.getId().equalsIgnoreCase(id.trim())) return vehicle;
        }
        return null;
    }

    public List<Vehicle> searchVehicle(String query, boolean availableOnly) {
        String needle = query == null ? "" : query.trim().toLowerCase();
        ArrayList<Vehicle> matches = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            boolean matchesText = needle.isEmpty()
                    || vehicle.getId().toLowerCase().contains(needle)
                    || vehicle.getBrand().toLowerCase().contains(needle)
                    || vehicle.getModel().toLowerCase().contains(needle);
            if (matchesText && (!availableOnly || vehicle.isAvailable())) matches.add(vehicle);
        }
        return List.copyOf(matches);
    }

    public Customer searchCustomer(String id) {
        if (id == null) return null;
        for (Customer customer : customers) {
            if (customer.getId().equalsIgnoreCase(id.trim())) return customer;
        }
        return null;
    }

    public void updateVehiclePrice(String vehicleId, BigDecimal newPrice) {
        Vehicle vehicle = requireVehicle(vehicleId);
        vehicle.setDailyRentalPrice(newPrice);
    }

    public Rental rentVehicle(String customerId, String vehicleId, int days, boolean optionalService) {
        Customer customer = searchCustomer(customerId);
        if (customer == null) throw new IllegalArgumentException("Customer ID was not found.");
        Vehicle vehicle = requireVehicle(vehicleId);
        if (!vehicle.isAvailable()) {
            throw new IllegalStateException(vehicle.isMaintenanceRequired()
                    ? "This vehicle requires maintenance and cannot be rented."
                    : "This vehicle already has an active rental.");
        }
        BigDecimal cost = vehicle.calculateRentalCost(days, optionalService);
        String service = vehicle.describeOptionalService(optionalService);
        vehicle.markRented();
        Rental rental = new Rental(String.format("R%04d", nextRentalNumber++), customer, vehicle, days, cost, service);
        rentals.add(rental);
        return rental;
    }

    public Rental returnRental(String rentalId) {
        Rental rental = requireRental(rentalId);
        if (rental.getStatus() != Rental.Status.ACTIVE) throw new IllegalStateException("This rental has already been completed.");
        rental.complete(LocalDate.now());
        rental.getVehicle().completeRental();
        return rental;
    }

    public void flagVehicleForMaintenance(String vehicleId) {
        requireVehicle(vehicleId).flagForMaintenance();
    }

    public List<Vehicle> getVehiclesNeedingMaintenance() {
        ArrayList<Vehicle> result = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            Maintainable maintainable = vehicle;
            if (maintainable.needsMaintenance()) result.add(vehicle);
        }
        return List.copyOf(result);
    }

    public void performVehicleMaintenance(String vehicleId) {
        Vehicle vehicle = requireVehicle(vehicleId);
        if (vehicle.isCurrentlyRented()) throw new IllegalStateException("Return the active rental before performing maintenance.");
        Maintainable maintainable = vehicle;
        if (!maintainable.needsMaintenance()) throw new IllegalStateException("Flag this vehicle for maintenance first.");
        maintainable.performMaintenance();
    }

    public List<Vehicle> getVehicles() { return List.copyOf(vehicles); }
    public List<Customer> getCustomers() { return List.copyOf(customers); }
    public List<Rental> getRentals() { return List.copyOf(rentals); }

    public List<Vehicle> getAvailableVehicles() {
        return searchVehicle("", true);
    }

    public List<Rental> getActiveRentals() {
        ArrayList<Rental> result = new ArrayList<>();
        for (Rental rental : rentals) if (rental.getStatus() == Rental.Status.ACTIVE) result.add(rental);
        return List.copyOf(result);
    }

    public List<Rental> getActiveRentalsForCustomer(String customerId) {
        if (searchCustomer(customerId) == null) throw new IllegalArgumentException("Customer ID was not found.");
        ArrayList<Rental> result = new ArrayList<>();
        for (Rental rental : rentals) {
            if (rental.getStatus() == Rental.Status.ACTIVE
                    && rental.getCustomer().getId().equalsIgnoreCase(customerId.trim())) result.add(rental);
        }
        return List.copyOf(result);
    }

    public List<Rental> getCustomerRentalHistory(String customerId) {
        if (searchCustomer(customerId) == null) throw new IllegalArgumentException("Customer ID was not found.");
        ArrayList<Rental> result = new ArrayList<>();
        for (Rental rental : rentals) {
            if (rental.getCustomer().getId().equalsIgnoreCase(customerId.trim())) result.add(rental);
        }
        return List.copyOf(result);
    }

    public BigDecimal getCompletedRentalRevenue() {
        BigDecimal revenue = BigDecimal.ZERO;
        for (Rental rental : rentals) {
            if (rental.getStatus() == Rental.Status.COMPLETED) revenue = revenue.add(rental.getTotalCost());
        }
        return revenue;
    }

    private Vehicle requireVehicle(String vehicleId) {
        Vehicle vehicle = searchVehicle(vehicleId);
        if (vehicle == null) throw new IllegalArgumentException("Vehicle ID was not found.");
        return vehicle;
    }

    private Rental requireRental(String rentalId) {
        if (rentalId != null) {
            for (Rental rental : rentals) {
                if (rental.getId().equalsIgnoreCase(rentalId.trim())) return rental;
            }
        }
        throw new IllegalArgumentException("Rental ID was not found.");
    }
}
