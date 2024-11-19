import java.util.*;
import java.io.*;

class Data_read {
    static final String FILE_NAME = "dbms_data";

    void data_read() {
        try (BufferedReader B_read = new BufferedReader(new FileReader(FILE_NAME))) {
            String str;
            System.out.println("Reading data from file: " + FILE_NAME);

            while ((str = B_read.readLine()) != null) {
                System.out.println(str);
            }
        } 
        catch (IOException e) {
            System.out.println(e);
        }
    }
}

// HashMap class for formatting 
class Mapping_task {
    static final String FILE_NAME = "dbms_data";
    private HashMap<String, HashMap<String, String>> Main_mapping = new HashMap<>();

    Mapping_task() {
        // Load data from the file into Main_mapping
        loadExistingData();
    }

    void loadExistingData() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            String currentMainKey = null;

            while ((line = reader.readLine()) != null) {
                if (line.startsWith("main Key:")) {
                    currentMainKey = line.substring(line.indexOf(":") + 1).trim();
                    Main_mapping.put(currentMainKey, new HashMap<>());
                } 
                else if (line.startsWith("    sub-Key:") && currentMainKey != null) {
                    String[] parts = line.split(",");
                    String subKey = parts[0].split(":")[1].trim();
                    String value = parts[1].split(":")[1].trim();
                    Main_mapping.get(currentMainKey).put(subKey, value);
                }
            }
            System.out.println("Loaded existing data from file.");
        } 
        catch (FileNotFoundException e) {
            System.out.println("File not found. Starting with an empty database.");
        } 
        catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
        }
    }

    void deleteMainKey(String mainKey) {
        if (Main_mapping.containsKey(mainKey)) {
            Main_mapping.remove(mainKey);
            System.out.println("Main key '" + mainKey + "' deleted.");
        } 
        else {
            System.out.println("Main key '" + mainKey + "' does not exist.");
        }
    }

    void deleteSubKey(String mainKey, List<String> subKeys) {
        if (Main_mapping.containsKey(mainKey)) {
            HashMap<String, String> subMap = Main_mapping.get(mainKey);
            for (String subKey : subKeys) {
                if (subMap.containsKey(subKey)) {
                    subMap.remove(subKey);
                    System.out.println("Sub-key '" + subKey + "' deleted from main key '" + mainKey + "'.");
                } else {
                    System.out.println("Sub-key '" + subKey + "' does not exist under main key '" + mainKey + "'.");
                }
            }
            if (subMap.isEmpty()) {
                Main_mapping.remove(mainKey);
                System.out.println("Main key '" + mainKey + "' deleted as it has no sub-keys left.");
            }
        } else {
            System.out.println("Main key '" + mainKey + "' does not exist.");
        }
    }

    void processQuery(String query) {
        query = query.trim();
        if (query.toLowerCase().startsWith("delete")) {
            if (query.toLowerCase().startsWith("delete from")) {
                String mainKeyPart = query.substring(query.indexOf(":") + 1, query.indexOf("{")).trim().replace("\"", "").replace("delete from ", "").trim();
                String valuesPart = query.substring(query.indexOf("{") + 1, query.indexOf("}")).trim();
                String[] subKeys = valuesPart.split(",");

                List<String> subKeysList = new ArrayList<>();
                for (String key : subKeys) {
                    String[] keyParts = key.split(":");
                    if (keyParts.length > 1) {
                        subKeysList.add(keyParts[1].trim());
                    } 
                    else {
                        System.out.println("Invalid sub-key format: " + key);
                    }
                }

                deleteSubKey(mainKeyPart, subKeysList);
            } 
            else {
                String mainKey = query.substring(query.indexOf(":") + 1).trim().replace("\"", "");
                deleteMainKey(mainKey);
            }
        } 
        else if (query.toLowerCase().startsWith("create")) {
            String mainKey = query.substring(query.indexOf(":") + 1).trim().replace("\"", "");
            Main_mapping.putIfAbsent(mainKey, new HashMap<>());
            System.out.println("Main key '" + mainKey + "' created.");
        } 
        else if (query.toLowerCase().startsWith("insert into")) {
            String[] parts = query.split("values", 2);
            if (parts.length < 2) {
                System.out.println("Invalid query format for insert.");
                return;
            }
            String mainKeyPart = parts[0].substring(query.indexOf(":") + 1).trim().replace("\"", "").replace("insert into ", "").trim();
            String valuesPart = parts[1].trim();

            if (!Main_mapping.containsKey(mainKeyPart)) {
                System.out.println("Main key '" + mainKeyPart + "' does not exist. Use CREATE first.");
                return;
            }

            valuesPart = valuesPart.substring(1, valuesPart.length() - 1);  
            String[] keyValuePairs = valuesPart.split("},\\s*\\{");  

            for (String pair : keyValuePairs) {
                pair = pair.replace("{", "").replace("}", "").trim();
                String[] keyValue = pair.split(",");
                if (keyValue.length != 2) {
                    System.out.println("Invalid key-value format: " + pair);
                    continue;
                }
                String[] subKeyParts = keyValue[0].split(":");
                String[] valueParts = keyValue[1].split(":");
                if (subKeyParts.length < 2 || valueParts.length < 2) {
                    System.out.println("Invalid key or value format in: " + pair);
                    continue;
                }
                String subKey = subKeyParts[1].trim();
                String value = valueParts[1].trim();
                Main_mapping.get(mainKeyPart).put(subKey, value);
            }
            System.out.println("\nInserted values into '" + mainKeyPart + "'.");
        }
        else if (query.toLowerCase().startsWith("update ")) {
            if (query.contains("to")) {
                // Update main key
                String[] parts = query.split("to", 2);
                if (parts.length < 2) {
                    System.out.println("Invalid update format for main key.");
                    return;
                }
                String oldMainKey = parts[0].substring(query.indexOf(":") + 1).trim().replace("\"", "").replace("update ", "").trim();
                String newMainKey = parts[1].trim().replace("\"", "");
                
                if (Main_mapping.containsKey(oldMainKey)) {
                    if (!Main_mapping.containsKey(newMainKey)) {
                        Main_mapping.put(newMainKey, Main_mapping.remove(oldMainKey));
                        System.out.println("Main key '" + oldMainKey + "' updated to '" + newMainKey + "'.");
                    } 
                    else {
                        System.out.println("Main key '" + newMainKey + "' already exists. Merging entries.");
                        Main_mapping.get(newMainKey).putAll(Main_mapping.remove(oldMainKey));
                    }
                } 
                else {
                    System.out.println("Main key '" + oldMainKey + "' does not exist.");
                }
            } 
            else {
                // Update sub-keys
                String[] parts = query.split("values", 2);
                if (parts.length < 2) {
                    System.out.println("Invalid update format for sub-keys.");
                    return;
                }
                String mainKey = parts[0].substring(query.indexOf(":") + 1).trim().replace("\"", "").replace("update ", "").trim();
                String valuesPart = parts[1].trim();
    
                if (!Main_mapping.containsKey(mainKey)) {
                    System.out.println("Main key '" + mainKey + "' does not exist. Use CREATE first.");
                    return;
                }
    
                valuesPart = valuesPart.substring(1, valuesPart.length() - 1);  
                String[] keyValuePairs = valuesPart.split("},\\s*\\{");  
    
                for (String pair : keyValuePairs) {
                    pair = pair.replace("{", "").replace("}", "").trim();
                    String[] keyValue = pair.split(",");
                    if (keyValue.length != 2) {
                        System.out.println("Invalid key-value format: " + pair);
                        continue;
                    }
                    String[] subKeyParts = keyValue[0].split(":");
                    String[] valueParts = keyValue[1].split(":");
                    if (subKeyParts.length < 2 || valueParts.length < 2) {
                        System.out.println("Invalid key or value format in: " + pair);
                        continue;
                    }
                    String subKey = subKeyParts[1].trim();
                    String value = valueParts[1].trim();
                    Main_mapping.get(mainKey).put(subKey, value);
                }
                System.out.println("\nUpdated values in '" + mainKey + "'.");
            }
        } 
        else {
            System.out.println("\nInvalid query.");
        }
        writeDataToFile();
    }

    void writeDataToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, false))) {
            for (Map.Entry<String, HashMap<String, String>> entry : Main_mapping.entrySet()) {
                String mainKey = entry.getKey();
                writer.write("main Key: " + mainKey + "\n");

                HashMap<String, String> subMap = entry.getValue();
                for (Map.Entry<String, String> subEntry : subMap.entrySet()) {
                    writer.write("    sub-Key: " + subEntry.getKey() + ", Value: " + subEntry.getValue() + "\n");
                }
            }
            System.out.println("\nData successfully updated in " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("\nAn error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    void data_collection() {
        Scanner s1 = new Scanner(System.in);
        while (true) {
            System.out.println("\nEnter SQL-like query or type 'quit' to exit:");
            String query = s1.nextLine();

            if (query.equalsIgnoreCase("quit")) {
                writeDataToFile();
                break;
            }
            processQuery(query);
        }
    }
}


public class AverageAgeCalculator {
    private Map<String, Integer> ageMap = new HashMap<>();
    private Map<String, Integer> countMap = new HashMap<>();
    private Map<String, Integer> batchAgeMap = new HashMap<>();
    private Map<String, Integer> batchCountMap = new HashMap<>();

    public void loadData(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        String currentMainKey = null;
        String currentBatch = null;

        while ((line = reader.readLine()) != null) {
            line = line.trim();

            if (line.startsWith("main Key:")) {
                String[] parts = line.split(":", 2);
                if (parts.length == 2) {
                    currentMainKey = parts[1].trim();
                }
            } else if (line.startsWith("sub-Key: batch, Value:") && currentMainKey != null) {
                String[] parts = line.split(":", 3);
                if (parts.length == 3) {
                    currentBatch = parts[2].replace("\"", "").trim();
                }
            } else if (line.startsWith("sub-Key: age, Value:") && currentMainKey != null && currentBatch != null) {
                String[] parts = line.split(":", 3);
                if (parts.length == 3) {
                    try {
                        int age = Integer.parseInt(parts[2].replace("\"", "").trim());

                        ageMap.put(currentMainKey, ageMap.getOrDefault(currentMainKey, 0) + age);
                        countMap.put(currentMainKey, countMap.getOrDefault(currentMainKey, 0) + 1);

                        batchAgeMap.put(currentBatch, batchAgeMap.getOrDefault(currentBatch, 0) + age);
                        batchCountMap.put(currentBatch, batchCountMap.getOrDefault(currentBatch, 0) + 1);
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid age format in line: " + line);
                    }
                }
            }
        }
        reader.close();
    }

    public double calculateAverageAge(String... mainKeys) {
        int totalAge = 0;
        int totalCount = 0;

        for (String key : mainKeys) {
            if (ageMap.containsKey(key)) {
                totalAge += ageMap.get(key);
                totalCount += countMap.get(key);
            }
        }

        return totalCount > 0 ? (double) totalAge / totalCount : 0.0;
    }
}

public class dbms {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Data_read dr = new Data_read();
        Mapping_task mt = new Mapping_task();
        AverageAgeCalculator calculator = new AverageAgeCalculator();

        while (true) {
            System.out.println("\n\n\t\t<<===== no-SQL dbms =====>> ");
            System.out.println("\t\t Press 1 for data reading");
            System.out.println("\t\t Press 2 for dbms tasks");
            System.out.println("\t\t Press 3 for average age calculation");
            System.out.println("\t\t Press 4 for exit\n");

            int choice = sc.nextInt();
            sc.nextLine(); 

            switch (choice) {
                case 1:
                    dr.data_read();
                    break;

                case 2:
                    mt.data_collection();
                    break;

                case 3:
                try {
                    calculator.loadData("/Users/harvijaysingh/btech cse/3rd year/minor project/final code /dbms_data");
        
                    while (true) {
                        System.out.println("Enter query (or type 'exit' to quit):");
                        String query = sc.nextLine().trim();
        
                        if (query.equalsIgnoreCase("exit")) {
                            System.out.println("Exiting program.");
                            break;
                        }
        
                        if (query.endsWith("average age")) {
                            String[] parts = query.split("\"");
                            if (parts.length < 2) {
                                System.out.println("Invalid query format. Please specify main keys in quotes.");
                                continue;
                            }
        
                            // Extract keys by removing the "main Key: " prefix
                            String[] selectedKeys = new String[(parts.length - 1) / 2];
                            int index = 0;
                            for (int i = 1; i < parts.length; i += 2) {
                                String key = parts[i].trim().replace("main Key: ", "");
                                selectedKeys[index++] = key;
                            }
        
                            double averageAge = calculator.calculateAverageAge(selectedKeys);
                            System.out.println("Average age of selected keys: " + averageAge);
        
                        } else {
                            System.out.println("Invalid query format. Please try again.");
                        }
                    }
        
                } catch (IOException e) {
                    System.err.println("Error reading the file: " + e.getMessage());
                }
                    break;

                case 4:
                    System.out.println("<<===== Thanks for using the system =====>>");
                    sc.close();
                    System.exit(0);
                    break;

                default:
                    System.out.println("<<===== Invalid choice =====>>");
            }
        }
    }
}
