import java.util.*;
//operation headers
import java.io.File;
import java.io.FileWriter; // header to write the file
import java.io.FileReader; // header to read the file
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;


// File read and write operations
class Data_read {
   
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

// hashmap class for formating 
class Mapping_task{
    // HashMap for key-value pairs
    private HashMap<String, HashMap<String, String>> Main_mapping = new HashMap<>();

    // Constructor initialization
    // public Mapping_task(){
    //     this.Main_mapping = new HashMap<>();
    // }
    //same task can also be done by simply making an object // direct initiallization
    // HashMap<String, HashMap<String, String>> Main_mapping = new HashMap<>();
    
    // String naming(File currentFile) throws IOException {
    //     String baseName = "data_file";  
    //     String extension = ".txt";      
    //     long sizeLimit = 700 * 1024;    
    //     int fileCounter = 1;            
        
    //     // Check if the current file exists and its size
    //     if (currentFile.exists()) {
    //         // If the file exceeds the size limit, generate a new file name
    //         while (currentFile.length() > sizeLimit) {
    //             fileCounter++;  // Increment the counter for a new file version
    //             currentFile = new File(baseName + "_" + fileCounter + extension);  // New file
    //         }
    //     }
        
    //     // Return the new file name (or current if within limit)
    //     return currentFile.getName();
    // }


    
    void writeDataToFile(String filename) {
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

                } 
                else if (line.startsWith("sub-Key:") && currentMainKey != null) {
                    String[] parts = line.split(", Value:");
                    String subKey = parts[0].substring("sub-Key:".length()).trim();
                    String value = parts.length > 1 ? parts[1].trim() : "";
                    existingData.get(currentMainKey).put(subKey, value);
                }
            }
        } 
        catch (IOException e) {
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
        } 
        catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }


    // collection of data 
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
                System.out.println("Enter the filename to save the data:");
                String outputFile = s1.nextLine();
                writeDataToFile(outputFile);
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

    void addSubKeysLater() {

        Scanner s2 = new Scanner(System.in);
        String main_Key, sub_Key;
        String data;

        System.out.println("add more sub-keys to existing main keys");
        System.out.println("(type 'exit' to stop):");
        while (true) {
            System.out.print("enter Existing Main Key: ");
            main_Key = s2.nextLine();

            if (main_Key.equalsIgnoreCase("exit")) {
                System.out.println("Enter the filename to save the data:");
                String outputFile = s2.nextLine();
                writeDataToFile(outputFile);
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

    

}

public class dbms {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        Data_read dr = new Data_read(); 
        Mapping_task mt = new Mapping_task(); 

        System.out.println("\t\t<<===== no-SQL dbms =====>> ");

        String path = "dbms/files";
        int n = 1;

        while(true){
            
            System.out.println("\t\t Press 1 for data reading");
            System.out.println("\t\t Press 2 for adding key-value pairs");
            System.out.println("\t\t Press 3 for exit");

            int i = sc.nextInt();
            sc.nextLine(); // Consume the newline left by nextInt

            switch (i) {
                case 1: 
                    //dr.data_read(f_add);

                    break;

                case 2:
                        mt.data_collection();
                        break;

                case 3: 
                        System.out.println("<<===== Thanks =====>>");
                        sc.close();
                        System.exit(0);

                default:
                        System.out.println("\t\t<<===== Invalid choice =====>>\n\n");
            }



        }
    }

}