import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;



public class Our_db {
    private HashMap<String, String> database;

    //this code is based on java 8 there might be small chnages if you are using higher versions
    public Our_db() {
        database = new HashMap<>();
    }
    
    public void put(String key, String value) {
        database.put(key, value);
    }

 
    public void save_to_file(String filePath) {
        try (FileWriter writer = new FileWriter(filePath,true)) {
            
            for (Map.Entry<String, String> entry : database.entrySet()) {
                         writer.write(entry.getKey() + ":" + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Our_db db = new Our_db();
        try (Scanner s1 = new Scanner(System.in)) {
            System.out.println("Enter key:");
            String key = s1.nextLine();
            System.out.println("Key entered: " + key); 

            System.out.println("Enter value:");
            String value = s1.nextLine();
            System.out.println("Value entered: " + value); 

            db.put(key, value);

            // Save data to the specified file 
            db.save_to_file("C:\\Users\\ACER\\OneDrive\\Desktop\\minor\\db_data.txt");

            System.out.println("Data saved to file");
        }
    }
}
       
        