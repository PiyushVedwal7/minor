import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



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
                writer.write(entry.getKey() + "=" + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Our_db db = new Our_db();
        db.put("age", "20");
        // Use double backslash or use r"here path of file in local system "
        db.save_to_file("C:\\Users\\ACER\\OneDrive\\Desktop\\minor\\db_data.txt");
    }
}
