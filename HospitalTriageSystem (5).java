
import java.util.*;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Collectors;

public class Main {

    static final int MAX = 20;

    static class Patient {
        String name;
        int age;
        double temperature;
        String complaint;
        String level;

        Patient(String name, int age, double temperature,
                String complaint) {
            this.name = name;
            this.age = age;
            this.temperature = temperature;
            this.complaint = complaint;
            this.level = findLevel(complaint, age, temperature);
        }

        static String findLevel(String complaint, int age,
                                double temperature) {
            if (temperature >= 104 ||
                complaint.equals("Chest pain") ||
                complaint.equals("Difficulty breathing") ||
                complaint.equals("Severe bleeding")) {
                return "Critical";
            } else if (complaint.equals("Broken bone") ||
                       complaint.equals("High fever") ||
                       age < 5 || age >= 65) {
                return "Serious";
            } else {
                return "Normal";
            }
        }

        @Override
        public String toString() {
            return name + " | Age: " + age
                    + " | Temp: " + temperature
                    + " F | Complaint: " + complaint
                    + " | Level: " + level;
        }
    }

    static Scanner sc = new Scanner(System.in);
    static ArrayList<Patient> patients = new ArrayList<>();

    static final String FILE = "patients.txt";

    static void registerPatient() {
        System.out.print("Enter patient name: ");
        String name = sc.nextLine().trim();

        System.out.print("Enter age: ");
        int age = Integer.parseInt(sc.nextLine());

        System.out.print("Enter temperature in Fahrenheit: ");
        double temp = Double.parseDouble(sc.nextLine());

        String[] complaints = {
            "Chest pain",
            "Difficulty breathing",
            "Severe bleeding",
            "Broken bone",
            "High fever",
            "Minor injury",
            "General checkup"
        };

        for (int i = 0; i < complaints.length; i++) {
            System.out.println((i + 1) + ". " + complaints[i]);
        }

        System.out.print("Select complaint (1-7): ");
        int choice = Integer.parseInt(sc.nextLine());

        if (name.isEmpty() || age < 0 || age > 120 ||
            temp < 90 || temp > 110 ||
            choice < 1 || choice > 7) {
            System.out.println("Invalid patient details.");
            return;
        }

        Patient p = new Patient(name, age, temp,
                                complaints[choice - 1]);
        patients.add(p);

        System.out.println("Patient registered successfully!");
        System.out.println("Emergency level: " + p.level);
    }

    static void displayPatients() {
        if (patients.isEmpty()) {
            System.out.println("No patients registered.");
            return;
        }

        patients.stream()
            .sorted(Comparator.comparingInt(p ->
                p.level.equals("Critical") ? 1 :
                p.level.equals("Serious") ? 2 : 3))
            .forEach(System.out::println);
    }

    static void showSummary() {
        Map<String, Long> counts = patients.stream()
            .collect(Collectors.groupingBy(
                p -> p.level, Collectors.counting()));

        System.out.println("\n--- Emergency Summary ---");
        System.out.println("Total patients: " + patients.size());
        System.out.println("Critical: " +
            counts.getOrDefault("Critical", 0L));
        System.out.println("Serious: " +
            counts.getOrDefault("Serious", 0L));
        System.out.println("Normal: " +
            counts.getOrDefault("Normal", 0L));

        double average = patients.stream()
            .mapToDouble(p -> p.temperature)
            .average().orElse(0.0);

        System.out.printf("Average temperature: %.1f F%n",
                          average);
    }

    static void savePatients() {
        List<String> lines = patients.stream()
            .map(p -> p.name + "|" + p.age + "|"
                + p.temperature + "|" + p.complaint
                + "|" + p.level)
            .collect(Collectors.toList());

        try {
            Files.write(Paths.get(FILE), lines);
            System.out.println("Patient records saved to " + FILE);
        } catch (IOException e) {
            System.out.println("Unable to save patient records.");
        }
    }

    static void readPatients() {
        Path path = Paths.get(FILE);

        try {
            if (!Files.exists(path)) {
                System.out.println("No saved patient file found.");
                return;
            }

            List<String> lines = Files.readAllLines(path);
            System.out.println("\n--- Saved Patient Records ---");

            lines.forEach(System.out::println);

        } catch (IOException e) {
            System.out.println("Unable to read patient file.");
        }
    }

    public static void main(String[] args) {
        int choice;

        do {
            System.out.println("\n===== HOSPITAL TRIAGE SYSTEM =====");
            System.out.println("1. Register Patient");
            System.out.println("2. Display Patients by Priority");
            System.out.println("3. Show Summary");
            System.out.println("4. Save Patient Records");
            System.out.println("5. Read Saved Records");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine());

                switch (choice) {
                    case 1:
                        if (patients.size() < MAX) {
                            registerPatient();
                        } else {
                            System.out.println("Patient limit reached.");
                        }
                        break;

                    case 2:
                        displayPatients();
                        break;

                    case 3:
                        showSummary();
                        break;

                    case 4:
                        savePatients();
                        break;

                    case 5:
                        readPatients();
                        break;

                    case 6:
                        System.out.println("Program ended.");
                        break;

                    default:
                        System.out.println("Choose from 1 to 6.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                choice = 0;
            }

        } while (choice != 6);

        sc.close();
    }
}
