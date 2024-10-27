import java.io.*;
import java.util.*;

public class persistence {
    private static final String FILE_PATH = "C:\\Users\\ACER\\OneDrive\\Desktop\\db replica\\sql\\db_data.txt";

    public static void main(String[] args) {
        Map<String, Map<String, String>> dataStore = new HashMap<>();
        CRUD crud = new CRUD(dataStore);
        
        // Load data from file on startup
        loadData(FILE_PATH, dataStore);

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter command (create, read, update, delete, exit): ");
            String command = scanner.nextLine().trim();
            if ("exit".equalsIgnoreCase(command)) break;

            switch (command.toLowerCase()) {
                case "create":
                    crud.create(scanner);
                    break;
                case "read":
                    crud.read(scanner);
                    break;
                case "update":
                    crud.update(scanner);
                    break;
                case "delete":
                    crud.delete(scanner);
                    break;
                default:
                    System.out.println("Unknown command: " + command);
            }
            
            // Save data after each CRUD operation
            saveData(FILE_PATH, dataStore);
        }
        scanner.close();
    }

    private static void loadData(String filePath, Map<String, Map<String, String>> dataStore) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line, currentMainKey = null;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("main Key:")) {
                    currentMainKey = line.substring(9).trim();
                    dataStore.put(currentMainKey, new HashMap<>());
                } else if (line.startsWith("sub-Key:") && currentMainKey != null) {
                    String[] parts = line.split(", Value:");
                    if (parts.length == 2) {
                        dataStore.get(currentMainKey).put(parts[0].substring(9).trim(), parts[1].trim());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    private static void saveData(String filePath, Map<String, Map<String, String>> dataStore) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
            for (String mainKey : dataStore.keySet()) {
                bw.write("main Key: " + mainKey);
                bw.newLine();
                Map<String, String> subMap = dataStore.get(mainKey);
                for (String subKey : subMap.keySet()) {
                    bw.write("sub-Key: " + subKey + ", Value: " + subMap.get(subKey));
                    bw.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
}

class CRUD {
    private final Map<String, Map<String, String>> dataStore;

    public CRUD(Map<String, Map<String, String>> dataStore) {
        this.dataStore = dataStore;
    }

    public void create(Scanner scanner) {
        System.out.print("Enter main key, sub key, and value (comma separated): ");
        String input = scanner.nextLine().trim();
        String[] parts = input.split(",");

        if (parts.length == 3) {
            String mainKey = parts[0].trim();
            String subKey = parts[1].trim();
            String value = parts[2].trim();
            Map<String, String> subMap = dataStore.computeIfAbsent(mainKey, k -> new HashMap<>());
            subMap.put(subKey, value);
            System.out.println("Inserted: " + subKey + " = " + value + " under main key: " + mainKey);
        } else {
            System.out.println("Invalid input format. Expected format: mainKey, subKey, value");
        }
    }

    public void read(Scanner scanner) {
        System.out.print("Enter main key: ");
        String mainKey = scanner.nextLine().trim();

        if (dataStore.containsKey(mainKey)) {
            Map<String, String> subMap = dataStore.get(mainKey);
            System.out.println("Sub-keys and values under main key '" + mainKey + "':");
            subMap.forEach((subKey, value) -> System.out.println(subKey + ": " + value));
        } else {
            System.out.println("Main key '" + mainKey + "' not found.");
        }
    }

    public void update(Scanner scanner) {
        System.out.print("Enter main key, sub key, and new value (comma separated): ");
        String input = scanner.nextLine().trim();
        String[] parts = input.split(",");

        if (parts.length == 3) {
            String mainKey = parts[0].trim();
            String subKey = parts[1].trim();
            String newValue = parts[2].trim();

            if (dataStore.containsKey(mainKey) && dataStore.get(mainKey).containsKey(subKey)) {
                dataStore.get(mainKey).put(subKey, newValue);
                System.out.println("Updated: " + subKey + " = " + newValue + " under main key: " + mainKey);
            } else {
                System.out.println("Main key or sub key not found.");
            }
        } else {
            System.out.println("Invalid input format. Expected format: mainKey, subKey, newValue");
        }
    }

    public void delete(Scanner scanner) {
        System.out.print("Enter main key and sub key (comma separated): ");
        String input = scanner.nextLine().trim();
        String[] parts = input.split(",");

        if (parts.length == 2) {
            String mainKey = parts[0].trim();
            String subKey = parts[1].trim();

            if (dataStore.containsKey(mainKey)) {
                Map<String, String> subMap = dataStore.get(mainKey);
                if (subMap.remove(subKey) != null) {
                    System.out.println("Deleted sub key '" + subKey + "' from main key '" + mainKey + "'.");
                    // Remove the main key if it has no sub-keys left
                    if (subMap.isEmpty()) {
                        dataStore.remove(mainKey);
                    }
                } else {
                    System.out.println("Sub key '" + subKey + "' not found under main key '" + mainKey + "'.");
                }
            } else {
                System.out.println("Main key '" + mainKey + "' not found.");
            }
        } else {
            System.out.println("Invalid input format. Expected format: mainKey, subKey");
        }
    }
}
