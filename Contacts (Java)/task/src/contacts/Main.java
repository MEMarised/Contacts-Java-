package contacts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

abstract class Record implements Serializable {

    protected String phoneNumber;
    protected LocalDateTime timeCreated;
    protected LocalDateTime timeLastEdit;

    public Record(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.timeCreated = LocalDateTime.now();
        this.timeLastEdit = this.timeCreated;
    }

    public abstract void displayInfo();

    public abstract List<String> getEditableFields();

    public abstract void editField(String fieldName, String value);

    public abstract String getFieldValue(String fieldName);

    public void editPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.timeLastEdit = LocalDateTime.now();
    }

    public boolean matches(String query) {
        return getEditableFields().stream()
                                  .map(this::getFieldValue)
                                  .anyMatch(value -> value != null && value.toLowerCase().contains(query.toLowerCase()));
    }
}

class Person extends Record {

    private String firstName;
    private String lastName;
    private String birthDate;
    private String gender;

    public Person(String firstName, String lastName, String birthDate, String gender, String phoneNumber) {
        super(phoneNumber);
        this.firstName = firstName;
        this.lastName = lastName;
        setBirthDate(birthDate);
        setGender(gender);
    }

    @Override
    public void displayInfo() {
        System.out.println("Name: " + firstName);
        System.out.println("Surname: " + lastName);
        System.out.println("Birth date: " + (birthDate != null ? birthDate : "[no data]"));
        System.out.println("Gender: " + (gender != null ? gender : "[no data]"));
        System.out.println("Number: " + phoneNumber);
        System.out.println("Time created: " + timeCreated.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        System.out.println("Time last edit: " + timeLastEdit.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
    }

    @Override
    public List<String> getEditableFields() {
        List<String> fields = new ArrayList<>();
        fields.add("name");
        fields.add("surname");
        fields.add("birth");
        fields.add("gender");
        fields.add("number");
        return fields;
    }

    @Override
    public void editField(String fieldName, String value) {
        switch (fieldName) {
            case "name":
                editFirstName(value);
                break;
            case "surname":
                editLastName(value);
                break;
            case "birth":
                setBirthDate(value);
                break;
            case "gender":
                setGender(value);
                break;
            case "number":
                editPhoneNumber(value);
                break;
            default:
                System.out.println("Unknown field!");
        }
    }

    @Override
    public String getFieldValue(String fieldName) {
        return switch (fieldName) {
            case "name" -> firstName;
            case "surname" -> lastName;
            case "birth" -> birthDate;
            case "gender" -> gender;
            case "number" -> phoneNumber;
            default -> null;
        };
    }

    public void editFirstName(String firstName) {
        this.firstName = firstName;
        this.timeLastEdit = LocalDateTime.now();
    }

    public void editLastName(String lastName) {
        this.lastName = lastName;
        this.timeLastEdit = LocalDateTime.now();
    }

    public void setBirthDate(String birthDate) {
        if (isValidBirthDate(birthDate)) {
            this.birthDate = birthDate;
            this.timeLastEdit = LocalDateTime.now();
        } else {
            System.out.println("Bad birth date!");
        }
    }

    public void setGender(String gender) {
        if (isValidGender(gender)) {
            this.gender = gender;
            this.timeLastEdit = LocalDateTime.now();
        } else {
            System.out.println("Bad gender!");
        }
    }

    private boolean isValidBirthDate(String birthDate) {
        if (birthDate == null || birthDate.isEmpty()) {
            return false;
        }
        try {
            LocalDateTime.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean isValidGender(String gender) {
        return "M".equals(gender) || "F".equals(gender);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}

class Organization extends Record {

    private String organizationName;
    private String address;

    public Organization(String organizationName, String address, String phoneNumber) {
        super(phoneNumber);
        this.organizationName = organizationName;
        this.address = address;
    }

    @Override
    public void displayInfo() {
        System.out.println("Organization name: " + organizationName);
        System.out.println("Address: " + address);
        System.out.println("Number: " + phoneNumber);
        System.out.println("Time created: " + timeCreated.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
        System.out.println("Time last edit: " + timeLastEdit.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")));
    }

    @Override
    public List<String> getEditableFields() {
        List<String> fields = new ArrayList<>();
        fields.add("organization name");
        fields.add("address");
        fields.add("number");
        return fields;
    }

    @Override
    public void editField(String fieldName, String value) {
        switch (fieldName) {
            case "organization name":
                editOrganizationName(value);
                break;
            case "address":
                editAddress(value);
                break;
            case "number":
                editPhoneNumber(value);
                break;
            default:
                System.out.println("Unknown field!");
        }
    }

    @Override
    public String getFieldValue(String fieldName) {
        return switch (fieldName) {
            case "organization name" -> organizationName;
            case "address" -> address;
            case "number" -> phoneNumber;
            default -> null;
        };
    }

    public void editOrganizationName(String organizationName) {
        this.organizationName = organizationName;
        this.timeLastEdit = LocalDateTime.now();
    }

    public void editAddress(String address) {
        this.address = address;
        this.timeLastEdit = LocalDateTime.now();
    }

    public String getOrganizationName() {
        return organizationName;
    }
}

public class Main {

    private static List<Record> records = new ArrayList<>();
    private static File file;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        if (args.length > 0) {
            file = new File(args[0]);
            if (file.exists()) {
                loadRecords();
            } else {
                saveRecords();  // Ensure the file is created if it doesn't exist
            }
        } else {
            file = null;  // Set file to null if no argument is provided
        }

        while (true) {
            System.out.print("[menu] Enter action (add, list, search, count, exit): ");
            String action = scanner.nextLine().trim();

            switch (action) {
                case "add":
                    addRecord(scanner);
                    break;
                case "list":
                    listRecords(scanner);
                    break;
                case "search":
                    searchRecords(scanner);
                    break;
                case "count":
                    countRecords();
                    break;
                case "exit":
                    saveRecords();
                    return;
                default:
                    System.out.println("Unknown action!");
            }
            System.out.println();  // Separate actions with an empty line
        }
    }

    private static void addRecord(Scanner scanner) {
        System.out.print("Enter the type (person, organization): ");
        String type = scanner.nextLine().trim();

        switch (type) {
            case "person":
                addPerson(scanner);
                break;
            case "organization":
                addOrganization(scanner);
                break;
            default:
                System.out.println("Unknown type!");
        }
        saveRecords();
        System.out.println(); // Ensure separation with an empty line
    }

    private static void addPerson(Scanner scanner) {
        System.out.print("Enter the name: ");
        String firstName = scanner.nextLine().trim();

        System.out.print("Enter the surname: ");
        String lastName = scanner.nextLine().trim();

        System.out.print("Enter the birth date (yyyy-MM-dd): ");
        String birthDate = scanner.nextLine().trim();
        if (!isValidBirthDate(birthDate)) {
            System.out.println("Bad birth date!");
            birthDate = null;
        }

        System.out.print("Enter the gender (M, F): ");
        String gender = scanner.nextLine().trim();
        if (!isValidGender(gender)) {
            System.out.println("Bad gender!");
            gender = null;
        }

        System.out.print("Enter the number: ");
        String phoneNumber = scanner.nextLine().trim();

        records.add(new Person(firstName, lastName, birthDate, gender, phoneNumber));
        System.out.println("The record added.");
        System.out.println(); // Ensure separation with an empty line
    }

    private static void addOrganization(Scanner scanner) {
        System.out.print("Enter the organization name: ");
        String organizationName = scanner.nextLine().trim();

        System.out.print("Enter the address: ");
        String address = scanner.nextLine().trim();

        System.out.print("Enter the number: ");
        String phoneNumber = scanner.nextLine().trim();

        records.add(new Organization(organizationName, address, phoneNumber));
        System.out.println("The record added.");
        System.out.println(); // Ensure separation with an empty line
    }

    private static void removeRecord(Scanner scanner) {
        if (records.isEmpty()) {
            System.out.println("No records to remove!");
            return;
        }

        displaySummary();

        System.out.print("Enter index to remove: ");
        int index = Integer.parseInt(scanner.nextLine().trim());

        if (index > 0 && index <= records.size()) {
            records.remove(index - 1);
            System.out.println("The record removed.");
            System.out.println(); // Ensure separation with an empty line
        } else {
            System.out.println("Invalid index!");
        }
        saveRecords();
    }

    private static void editRecord(Scanner scanner, Record record) {
        List<String> fields = record.getEditableFields();
        System.out.print("Select a field (" + String.join(", ", fields) + "): ");
        String field = scanner.nextLine().trim();

        if (fields.contains(field)) {
            System.out.print("Enter " + field + ": ");
            String value = scanner.nextLine().trim();
            record.editField(field, value);
            System.out.println("The record updated!");
        } else {
            System.out.println("Unknown field!");
        }
        saveRecords();
        System.out.println(); // Ensure separation with an empty line
    }

    private static void countRecords() {
        System.out.println("The Phone Book has " + records.size() + " records.");
        System.out.println(); // Ensure separation with an empty line
    }

    private static void displaySummary() {
        resultingChecker(records);
    }

    private static void listRecords(Scanner scanner) {
        if (records.isEmpty()) {
            System.out.println("No records to list!");
            return;
        }

        displaySummary();

        System.out.print("[list] Enter action ([number], back): ");
        String action = scanner.nextLine().trim();

        if ("back".equals(action)) {
            System.out.println(); // Ensure separation with an empty line
            return;
        }

        try {
            int index = Integer.parseInt(action);
            if (index > 0 && index <= records.size()) {
                Record record = records.get(index - 1);
                record.displayInfo();
                System.out.println(); // Ensure separation with an empty line
                System.out.print("[record] Enter action (edit, delete, menu): ");
                String recordAction = scanner.nextLine().trim();

                switch (recordAction) {
                    case "edit":
                        editRecord(scanner, record); // Pass the record to editRecord
                        break;
                    case "delete":
                        removeRecord(scanner);
                        break;
                    case "menu":
                        System.out.println(); // Ensure separation with an empty line
                        return;
                    default:
                        System.out.println("Unknown action!");
                }
            } else {
                System.out.println("Invalid index!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid action!");
        }
    }

    private static void searchRecords(Scanner scanner) {
        System.out.print("Enter search query: ");
        String query = scanner.nextLine().trim();

        List<Record> results = new ArrayList<>();
        for (Record record : records) {
            if (record.matches(query)) {
                results.add(record);
            }
        }

        if (results.isEmpty()) {
            System.out.println("No results found.");
        } else {
            System.out.println("Found " + results.size() + " results:");
            resultingChecker(results);

            while (true) {
                System.out.print("[search] Enter action ([number], back, again): ");
                String action = scanner.nextLine().trim();

                if ("back".equals(action)) {
                    System.out.println(); // Ensure separation with an empty line
                    return;
                } else if ("again".equals(action)) {
                    searchRecords(scanner);
                    return;
                }

                try {
                    int index = Integer.parseInt(action);
                    if (index > 0 && index <= results.size()) {
                        Record record = results.get(index - 1);
                        record.displayInfo();
                        System.out.println(); // Ensure separation with an empty line
                        System.out.print("[record] Enter action (edit, delete, menu): ");
                        String recordAction = scanner.nextLine().trim();

                        switch (recordAction) {
                            case "edit":
                                editRecord(scanner, record); // Pass the record to editRecord
                                break;
                            case "delete":
                                removeRecord(scanner);
                                break;
                            case "menu":
                                System.out.println(); // Ensure separation with an empty line
                                return;
                            default:
                                System.out.println("Unknown action!");
                        }
                    } else {
                        System.out.println("Invalid index!");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid action!");
                }
            }
        }
    }

    private static void resultingChecker(List<Record> results) {
        for (int i = 0; i < results.size(); i++) {
            Record record = results.get(i);
            String summary = (record instanceof Person) ?
                             ((Person) record).getFirstName() + " " + ((Person) record).getLastName() :
                             ((Organization) record).getOrganizationName();
            System.out.println((i + 1) + ". " + summary);
        }
    }

    private static boolean isValidBirthDate(String birthDate) {
        if (birthDate == null || birthDate.isEmpty()) {
            return false;
        }
        try {
            LocalDateTime.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private static boolean isValidGender(String gender) {
        return "M".equals(gender) || "F".equals(gender);
    }

    private static void loadRecords() {
        if (file == null) return;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            records = (List<Record>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void saveRecords() {
        if (file == null) return;
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(records);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}