import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Main {
    private final Scanner scanner = new Scanner(System.in);
    private final RentalSystem rentalSystem = new RentalSystem();

    public static void main(String[] args) {
        new Main().run();
    }

    private void run() {
        System.out.println("Vehicle Rental Management System");
        boolean running = true;
        while (running) {
            printMenu();
            String choice;
            try {
                choice = readLine("Select an option: ");
            } catch (NoSuchElementException ex) {
                System.out.println("Input ended. Goodbye.");
                break;
            }
            if (choice.equals("0")) {
                running = false;
                System.out.println("Goodbye.");
                continue;
            }
            try {
                handleChoice(choice);
            } catch (IllegalArgumentException | IllegalStateException ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
        scanner.close();
    }

    private void printMenu() {
        System.out.println("\n1. Add New Vehicle");
        System.out.println("2. Display All Vehicles");
        System.out.println("3. Search for Vehicle");
        System.out.println("4. Display Available Vehicles");
        System.out.println("5. Register New Customer");
        System.out.println("6. Display All Customers");
        System.out.println("7. Rent a Vehicle");
        System.out.println("8. Return a Vehicle");
        System.out.println("9. Display All Rentals");
        System.out.println("10. Display Customer Rental History");
        System.out.println("11. Display Total Revenue");
        System.out.println("12. Flag Vehicle for Maintenance");
        System.out.println("13. Display Vehicles Requiring Maintenance");
        System.out.println("14. Perform Vehicle Maintenance");
        System.out.println("15. Update Daily Rental Price");
        System.out.println("16. Update Customer Phone or Email");
        System.out.println("17. Display Vehicles Currently Rented by a Customer");
        System.out.println("0. Exit");
    }

    private void handleChoice(String choice) {
        switch (choice) {
            case "1" -> addVehicle();
            case "2" -> printVehicles(rentalSystem.getVehicles());
            case "3" -> searchVehicle();
            case "4" -> printVehicles(rentalSystem.getAvailableVehicles());
            case "5" -> addCustomer();
            case "6" -> printCustomers();
            case "7" -> rentVehicle();
            case "8" -> returnVehicle();
            case "9" -> printRentals(rentalSystem.getRentals());
            case "10" -> printCustomerHistory();
            case "11" -> System.out.printf("Completed-rental revenue: $%.2f%n", rentalSystem.getCompletedRentalRevenue());
            case "12" -> flagVehicleForMaintenance();
            case "13" -> printVehicles(rentalSystem.getVehiclesNeedingMaintenance());
            case "14" -> performMaintenance();
            case "15" -> updateVehiclePrice();
            case "16" -> updateCustomerContact();
            case "17" -> printRentals(rentalSystem.getActiveRentalsForCustomer(readNonEmpty("Customer ID: ")));
            default -> System.out.println("Choose one of the listed options.");
        }
    }

    private void addVehicle() {
        String type = readNonEmpty("Vehicle type (car/motorcycle/truck): ").toLowerCase();
        String id = readNonEmpty("Vehicle ID: ");
        String brand = readNonEmpty("Brand: ");
        String model = readNonEmpty("Model: ");
        int year = readInt("Manufacturing year: ");
        BigDecimal price = readPositiveMoney("Daily rental price: ");
        Vehicle vehicle = switch (type) {
            case "car" -> new Car(id, brand, model, year, price,
                    readPositiveInt("Number of seats: "), readNonEmpty("Transmission type: "));
            case "motorcycle" -> new Motorcycle(id, brand, model, year, price,
                    readPositiveInt("Engine capacity (cc): "));
            case "truck" -> new Truck(id, brand, model, year, price,
                    readPositiveDecimal("Maximum load capacity (tons): "));
            default -> throw new IllegalArgumentException("Vehicle type must be car, motorcycle, or truck.");
        };
        rentalSystem.addVehicle(vehicle);
        System.out.println("Vehicle added: " + vehicle.displayDetails());
    }

    private void searchVehicle() {
        System.out.println("1. Search by vehicle ID");
        System.out.println("2. Search by brand/model text");
        String mode = readLine("Search option: ");
        if (mode.equals("1")) {
            Vehicle vehicle = rentalSystem.searchVehicle(readNonEmpty("Vehicle ID: "));
            if (vehicle == null) System.out.println("No vehicle found.");
            else System.out.println(vehicle.displayDetails());
        } else if (mode.equals("2")) {
            printVehicles(rentalSystem.searchVehicle(readNonEmpty("Search text: "), false));
        } else {
            System.out.println("Choose 1 or 2.");
        }
    }

    private void addCustomer() {
        Customer customer = new Customer(readNonEmpty("Customer ID: "),
                readNonEmpty("Full name: "), readNonEmpty("Phone: "),
                readNonEmpty("Email: "), readNonEmpty("Driving license number: "));
        rentalSystem.addCustomer(customer);
        System.out.println("Customer registered: " + customer);
    }

    private void rentVehicle() {
        String customerId = readNonEmpty("Customer ID: ");
        String vehicleId = readNonEmpty("Vehicle ID: ");
        int days = readPositiveInt("Rental days: ");
        Vehicle vehicle = rentalSystem.searchVehicle(vehicleId);
        if (vehicle == null) throw new IllegalArgumentException("Vehicle ID was not found.");
        String servicePrompt = vehicle.getOptionalServicePrompt();
        boolean optionalService = !servicePrompt.isBlank() && readYesNo(servicePrompt);
        Rental rental = rentalSystem.rentVehicle(customerId, vehicleId, days, optionalService);
        System.out.println("Rental created: " + rental);
        System.out.println("The vehicle is now unavailable until it is returned.");
    }

    private void returnVehicle() {
        Rental rental = rentalSystem.returnRental(readNonEmpty("Rental ID: "));
        System.out.println("Rental completed: " + rental.getId() + ". Vehicle " + rental.getVehicle().getId() + " is available again unless it needs maintenance.");
    }

    private void printCustomerHistory() {
        String id = readNonEmpty("Customer ID: ");
        printRentals(rentalSystem.getCustomerRentalHistory(id));
    }

    private void flagVehicleForMaintenance() {
        String id = readNonEmpty("Vehicle ID: ");
        rentalSystem.flagVehicleForMaintenance(id);
        System.out.println("Vehicle " + id + " is flagged for maintenance and cannot be rented.");
    }

    private void performMaintenance() {
        String id = readNonEmpty("Vehicle ID: ");
        rentalSystem.performVehicleMaintenance(id);
        Vehicle vehicle = rentalSystem.searchVehicle(id);
        System.out.println("Maintenance completed: " + vehicle.displayDetails());
    }

    private void updateVehiclePrice() {
        String id = readNonEmpty("Vehicle ID: ");
        BigDecimal price = readPositiveMoney("New daily rental price: ");
        rentalSystem.updateVehiclePrice(id, price);
        System.out.println("Daily rental price updated.");
    }

    private void updateCustomerContact() {
        String id = readNonEmpty("Customer ID: ");
        Customer customer = rentalSystem.searchCustomer(id);
        if (customer == null) throw new IllegalArgumentException("Customer ID was not found.");
        customer.updateContactInformation(readNonEmpty("New phone: "), readNonEmpty("New email: "));
        System.out.println("Customer contact information updated: " + customer);
    }

    private void printVehicles(List<Vehicle> vehicles) {
        if (vehicles.isEmpty()) {
            System.out.println("No vehicles to display.");
            return;
        }
        for (Vehicle vehicle : vehicles) System.out.println(vehicle.displayDetails());
    }

    private void printCustomers() {
        List<Customer> customers = rentalSystem.getCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers registered.");
            return;
        }
        for (Customer customer : customers) System.out.println(customer);
    }

    private void printRentals(List<Rental> rentals) {
        if (rentals.isEmpty()) {
            System.out.println("No rentals to display.");
            return;
        }
        for (Rental rental : rentals) System.out.println(rental);
    }

    private String readNonEmpty(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isBlank()) return value.trim();
            System.out.println("This value cannot be empty.");
        }
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private int readInt(String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(prompt));
            } catch (NumberFormatException ex) {
                System.out.println("Enter a whole number.");
            }
        }
    }

    private int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value > 0) return value;
            System.out.println("Enter a value greater than zero.");
        }
    }

    private BigDecimal readPositiveMoney(String prompt) {
        while (true) {
            try {
                BigDecimal value = new BigDecimal(readLine(prompt));
                if (value.compareTo(BigDecimal.ZERO) > 0) return value;
            } catch (NumberFormatException ignored) {
                // Display the same concise validation prompt below.
            }
            System.out.println("Enter a valid amount greater than zero.");
        }
    }

    private BigDecimal readPositiveDecimal(String prompt) {
        return readPositiveMoney(prompt);
    }

    private boolean readYesNo(String prompt) {
        while (true) {
            String value = readLine(prompt).toLowerCase();
            if (value.equals("y") || value.equals("yes")) return true;
            if (value.equals("n") || value.equals("no")) return false;
            System.out.println("Enter y or n.");
        }
    }
}
