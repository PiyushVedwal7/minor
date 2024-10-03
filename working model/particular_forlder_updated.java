import java.util.*;
import java.io.File;
import java.io.FileWriter; // header to write the file
import java.io.FileReader; // header to read the file
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
            } 
            else {
                System.out.println("\nFile already exists: \n" + FILE.getAbsolutePath());
            }
        } 
        catch (IOException e) {
            System.out.println("\nError in creating file.\n");
            e.printStackTrace();
        }
    }

    String file_path(String F_name){
        File f = new File(F_name);
        return f.getAbsolutePath();
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
        } 
        catch (IOException e) {
            System.out.println(e);
        }
    }
}

class Mapping_task{
    // HashMap for key-value pairs
    private HashMap<String, HashMap<String, String>> Main_mapping;

    // Constructor initialization
    public Mapping_task(){
        this.Main_mapping = new HashMap<>();
    }
    //same task can also be done by simply making an object // direct initiallization
    // HashMap<String, HashMap<String, String>> Main_mapping = new HashMap<>();


    void data_collection(){
        Scanner s1 = new Scanner(System.in);

        String Main_key, Sub_key;
        String data;

        System.out.println("enter the primary key first.");
        System.out.println("later, enter the secondary key for data sub-categorization.");
        System.out.println("type 'quit' after all the sub keys are entered.\n\n");

        while (true) {
            System.out.println("Enter the main key: ");
            Main_key = s1.nextLine();

            if(Main_key.equalsIgnoreCase("quit")){
                break;
            }

            Main_mapping.putIfAbsent(Main_key, new HashMap<>()); // Ensure the main key exists

            while (true) {
                System.out.println("Enter the Sub-key ");
                System.out.println("(or type 'done' to finish)");
                Sub_key = s1.nextLine();

                if(Sub_key.equalsIgnoreCase("done")){
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
            }
            else {
                System.out.println("main Key does not exist\t please enter a valid Main Key.");
            }
        }
    }

    public void writeDataToFile(String filename) {
        HashMap<String, HashMap<String, String>> existingData = new HashMap<>();
        
        // Read the file to capture existing data
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
    
        // Merge the new data with the existing data
        for (Map.Entry<String, HashMap<String, String>> entry : Main_mapping.entrySet()) {
            String mainKey = entry.getKey();
            existingData.putIfAbsent(mainKey, new HashMap<>());
    
            HashMap<String, String> subMap = entry.getValue();
            for (Map.Entry<String, String> subEntry : subMap.entrySet()) {
                existingData.get(mainKey).put(subEntry.getKey(), subEntry.getValue());
            }
        }
    
        // Write the updated data back to the file
        try (FileWriter writer = new FileWriter(filename, false)) {  // false to overwrite
            for (Map.Entry<String, HashMap<String, String>> entry : existingData.entrySet()) {
                String mainKey = entry.getKey();
                writer.write("main Key: " + mainKey + "\n");
    
                HashMap<String, String> subMap = entry.getValue();
                for (Map.Entry<String, String> subEntry : subMap.entrySet()) {
                    writer.write("    sub-Key: " + subEntry.getKey() + ", Value: " + subEntry.getValue() + "\n");
                }
            }
            System.out.println("Data successfully updated in " + filename);
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }
    

}

public class particular_folder_updated {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        File_operations fs = new File_operations(); // File system class

        Data_write_read dwr = new Data_write_read(); // Operations

        Mapping_task mt = new Mapping_task(); // Mapping task for no-SQL like data structure
        
        System.out.println("\t\t<<===== no-SQL =====>> ");
        String Address = "code"; // Relative path
        String File_name = "hello.txt"; 

        while (true) {
            System.out.println("\t\t Press 1 for file creation");
            System.out.println("\t\t Press 2 for data insertion");
            System.out.println("\t\t Press 3 for data reading");
            System.out.println("\t\t Press 4 for adding key-value pairs");
            System.out.println("\t\t Press 5 for writing data to file");
            System.out.println("\t\t Press 6 for exit");
            
            int i = sc.nextInt();
            sc.nextLine(); // Consume the newline left by nextInt
            String f_add = fs.file_path(Address + File.separator + File_name);

            switch (i) {
                case 1: 
                    System.out.println("The file address: \t" + Address); // Enter the relative path
                    System.out.println("Enter the file name: \t" + File_name);
    
                    fs.create_file(Address, File_name);
                    break;
    
                case 2: 
                    System.out.println("The file address: \t" + Address); // Enter the relative path
                    System.out.println("The file name: \t" + File_name);

                    System.out.println("The file path: \t" + f_add);

                    System.out.println("Enter the data: \t");
                    String data = sc.nextLine(); // Use nextLine() to capture full data input
                
                    dwr.data_write(f_add, data);
                    break;

                case 3: 
                    System.out.println("The file address: \t" + Address); // Enter the relative path
                    System.out.println("The file name: \t" + File_name); 

                    System.out.println("The file path: \t" + f_add); 
                    System.out.println("Data from file:\n");
                    dwr.data_read(f_add);

                    break;

                case 4:
                    mt.data_collection();
                    break;

                case 5:
                    System.out.println("Enter the filename to save the data:");
                    String outputFile = sc.nextLine();
                    mt.writeDataToFile(outputFile);
                    break;

                case 6: 
                    System.out.println("<<===== Thanks =====>>");
                    sc.close();
                    System.exit(0);

                default:
                    System.out.println("\t\t<<===== Invalid choice =====>>\n\n");
            }
        }
    }
}
