import java.util.*;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;

// File creation and checking whether the file is created or not
class File_operations {

    void create_file(String address, String file_name) {
        // Check if directory exists
        File dir = new File(address);
        if (!dir.exists()) {
            // Create the directory if it doesn't exist
            dir.mkdirs();
        }

        // Create the specific file
        File FILE = new File(dir, file_name);
        try {
            if (FILE.createNewFile()) {
                System.out.println("\nFile created successfully: \n" + FILE.getAbsolutePath());
            } else {
                System.out.println("\nFile already exists: \n" + FILE.getAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("\nError in creating file.\n");
            e.printStackTrace();
        }
    }

    String file_path(String F_name) {
        File f = new File(F_name);
        return f.getAbsolutePath();
    }

    // New function for deleting file
    void delete_file(String file_path) {
        File file = new File(file_path);
        if (file.exists() && file.delete()) {
            System.out.println("File deleted: " + file.getAbsolutePath());
        } else {
            System.out.println("File deletion failed or file does not exist.");
        }
    }
}

// File read and write operations
class Data_write_read {
    void data_write(String f_path, String data) {
        try {
            // Object of FileWriter class
            FileWriter F_write = new FileWriter(f_path);
            BufferedWriter B_Write = new BufferedWriter(F_write);

            B_Write.write(data); // Data is written
            B_Write.close(); // Ensure BufferedWriter is closed
            F_write.close(); // Close FileWriter as well

            System.out.println("Data entered in the file: \t" + f_path);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    void data_read(String f_path) {
        try { // FileReader object
            FileReader F_Read = new FileReader(f_path);
            BufferedReader B_read = new BufferedReader(F_Read);

            String str;
            System.out.println("Reading data from file: " + f_path);

            while ((str = B_read.readLine()) != null) {
                System.out.println(str);
            }
            B_read.close(); // Close BufferedReader
            F_Read.close(); // Close FileReader
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    // Function to append data into the file
    void insert_into_file(String file_path, String data) {
        try {
            FileWriter writer = new FileWriter(file_path, true); // Append mode
            BufferedWriter bufferedWriter = new BufferedWriter(writer);
            bufferedWriter.write(data);
            bufferedWriter.newLine();
            bufferedWriter.close();
            writer.close();
            System.out.println("Data inserted into file: " + file_path);
        } catch (IOException e) {
            System.out.println("Error writing to file.");
            e.printStackTrace();
        }
    }
}

// Query Parser and Execution
class QueryProcessor {

    File_operations fileOps = new File_operations();
    Data_write_read dataOps = new Data_write_read();

    void processQuery(String query) {
        query = query.trim().toUpperCase();
        if (query.startsWith("CREATE FILE")) {
            String filePath = extractFilePath(query);
            fileOps.create_file(filePath, "");
        } else if (query.startsWith("DELETE FILE")) {
            String filePath = extractFilePath(query);
            fileOps.delete_file(filePath);
        } else if (query.startsWith("INSERT INTO")) {
            String[] parts = query.split(" VALUES ");
            if (parts.length == 2) {
                String filePath = extractFilePath(parts[0]);
                String data = extractValues(parts[1]);
                dataOps.insert_into_file(filePath, data);
            } else {
                System.out.println("Invalid INSERT query.");
            }
        } else if (query.startsWith("SELECT * FROM")) {
            String filePath = extractFilePath(query);
            dataOps.data_read(filePath);
        } else {
            System.out.println("Invalid query.");
        }
    }

    // Helper function to extract file path
    private String extractFilePath(String query) {
        int startIndex = query.indexOf("'") + 1;
        int endIndex = query.lastIndexOf("'");
        return query.substring(startIndex, endIndex);
    }

    // Helper function to extract values
    private String extractValues(String query) {
        int startIndex = query.indexOf("(") + 1;
        int endIndex = query.lastIndexOf(")");
        return query.substring(startIndex, endIndex).trim();
    }
}

// no-SQL like data structure handling class
class Mapping_task {
    private HashMap<String, HashMap<String, String>> Main_mapping;

    public Mapping_task() {
        this.Main_mapping = new HashMap<>();
    }

    void data_collection() {
        Scanner s1 = new Scanner(System.in);
        String Main_key, Sub_key;
        String data;

        System.out.println("enter the primary key first.");
        System.out.println("later, enter the secondary key for data sub-categorization.");
        System.out.println("type 'quit' after all the sub keys are entered.\n\n");

        while (true) {
            System.out.println("Enter the main key: ");
            Main_key = s1.nextLine();

            if (Main_key.equalsIgnoreCase("quit")) {
                break;
            }

            Main_mapping.putIfAbsent(Main_key, new HashMap<>()); // Ensure the main key exists

            while (true) {
                System.out.println("Enter the Sub-key ");
                System.out.println("(or type 'done' to finish)");
                Sub_key = s1.nextLine();

                if (Sub_key.equalsIgnoreCase("done")) {
                    break;
                }

                System.out.print("Enter Value: ");
                data = s1.nextLine();

                Main_mapping.get(Main_key).put(Sub_key, data);
            }
        }
    }

    public void addSubKeysLater() {
        Scanner s2 = new Scanner(System.in);
        String main_Key, sub_Key;
        String data;

        System.out.println("add more sub-keys to existing main keys");
        System.out.println("(type 'exit' to stop):");
        while (true) {
            System.out.print("enter Existing Main Key: ");
            main_Key = s2.nextLine();

            if (main_Key.equalsIgnoreCase("exit")) {
                break;
            }

            if (Main_mapping.containsKey(main_Key)) {
                while (true) {
                    System.out.print("Enter Sub-Key ");
                    System.out.println("type 'done' to finish adding sub-keys to this main key: ");
                    sub_Key = s2.nextLine();

                    if (sub_Key.equalsIgnoreCase("done")) {
                        break;
                    }

                    System.out.print("Enter Value: ");
                    data = s2.nextLine();

                    Main_mapping.get(main_Key).put(sub_Key, data);
                }
            } else {
                System.out.println("main Key does not exist\t please enter a valid Main Key.");
            }
        }
    }

    public void writeDataToFile(String filename) {
        HashMap<String, HashMap<String, String>> existingData = new HashMap<>();
    
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            String currentMainKey = null;
    
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("main Key:")) {
                    currentMainKey = line.substring("main Key:".length()).trim();
                    existingData.putIfAbsent(currentMainKey, new HashMap<>());
                } else if (line.startsWith("sub-Key:") && currentMainKey != null) {
                    String[] parts = line.split(", Value:");
                    String subKey = parts[0].substring("sub-Key:".length()).trim();
                    String value = parts.length > 1 ? parts[1].trim() : "";
                    existingData.get(currentMainKey).put(subKey, value);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file.");
            e.printStackTrace();
        }
    
        for (Map.Entry<String, HashMap<String, String>> entry : Main_mapping.entrySet()) {
            String mainKey = entry.getKey();
            existingData.putIfAbsent(mainKey, new HashMap<>());
    
            HashMap<String, String> subMap = entry.getValue();
            for (Map.Entry<String, String> subEntry : subMap.entrySet()) { // Corrected line
                existingData.get(mainKey).put(subEntry.getKey(), subEntry.getValue());
            }
        }
    
        try (FileWriter writer = new FileWriter(filename, false)) {
            for (Map.Entry<String, HashMap<String, String>> entry : existingData.entrySet()) {
                String mainKey = entry.getKey();
                writer.write("main Key: " + mainKey + "\n");
    
                HashMap<String, String> subMap = entry.getValue();
                for (Map.Entry<String, String> subEntry : subMap.entrySet()) {
                    writer.write("sub-Key: " + subEntry.getKey() + ", Value: " + subEntry.getValue() + "\n");
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while writing the file.");
            e.printStackTrace();
        }
    }
    
}

public class pf_update{
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        File_operations fileOps = new File_operations();
        Data_write_read dataOps = new Data_write_read();
        Mapping_task mappingTask = new Mapping_task();
        QueryProcessor queryProcessor = new QueryProcessor();

        while (true) {
            System.out.println("\nSelect an operation:");
            System.out.println("1. Create File");
            System.out.println("2. Delete File");
            System.out.println("3. Write Data");
            System.out.println("4. Read Data");
            System.out.println("5. Insert Data into File");
            System.out.println("6. Query Processing");
            System.out.println("7. Data Collection (no-SQL style)");
            System.out.println("8. Add Sub-keys");
            System.out.println("9. Write Mapping to File");
            System.out.println("10. Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter directory: ");
                    String dir = scanner.nextLine();
                    System.out.print("Enter file name: ");
                    String fileName = scanner.nextLine();
                    fileOps.create_file(dir, fileName);
                    break;
                case 2:
                    System.out.print("Enter file path to delete: ");
                    String delPath = scanner.nextLine();
                    fileOps.delete_file(delPath);
                    break;
                case 3:
                    System.out.print("Enter file path to write data: ");
                    String writePath = scanner.nextLine();
                    System.out.print("Enter data: ");
                    String data = scanner.nextLine();
                    dataOps.data_write(writePath, data);
                    break;
                case 4:
                    System.out.print("Enter file path to read data: ");
                    String readPath = scanner.nextLine();
                    dataOps.data_read(readPath);
                    break;
                case 5:
                    System.out.print("Enter file path to insert data: ");
                    String insertPath = scanner.nextLine();
                    System.out.print("Enter data to insert: ");
                    String insertData = scanner.nextLine();
                    dataOps.insert_into_file(insertPath, insertData);
                    break;
                case 6:
                    System.out.print("Enter query: ");
                    String query = scanner.nextLine();
                    queryProcessor.processQuery(query);
                    break;
                case 7:
                    mappingTask.data_collection();
                    break;
                case 8:
                    mappingTask.addSubKeysLater();
                    break;
                case 9:
                    System.out.print("Enter file path to write mapping: ");
                    String mapPath = scanner.nextLine();
                    mappingTask.writeDataToFile(mapPath);
                    break;
                case 10:
                    System.out.println("Exiting program.");
                    return;
                default:
                    System.out.println("Invalid choice.");
            }
        }
    }
}
