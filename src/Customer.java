public class Customer {
    private final String id;
    private final String fullName;
    private String phone;
    private String email;
    private final String drivingLicenseNumber;

    public Customer(String id, String fullName, String phone, String email, String drivingLicenseNumber) {
        this.id = requireText(id, "Customer ID");
        this.fullName = requireText(fullName, "Full name");
        this.drivingLicenseNumber = requireText(drivingLicenseNumber, "Driving license number");
        updateContactInformation(phone, email);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(fieldName + " cannot be empty.");
        return value.trim();
    }

    public String getId() { return id; }
    public String getFullName() { return fullName; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getDrivingLicenseNumber() { return drivingLicenseNumber; }

    public void updateContactInformation(String phone, String email) {
        String newPhone = requireText(phone, "Phone");
        String newEmail = requireText(email, "Email");
        if (!newEmail.contains("@") || newEmail.startsWith("@") || newEmail.endsWith("@")) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
        this.phone = newPhone;
        this.email = newEmail;
    }

    @Override
    public String toString() {
        return String.format("Customer [ID=%s, name=%s, phone=%s, email=%s, license=%s]",
                id, fullName, phone, email, drivingLicenseNumber);
    }
}
