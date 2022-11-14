package excelReader.jsonutils;


import com.google.gson.Gson;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class CreateJsonFileFromData {

        public static <T> void createJsonFileFromList(String path, List<T> data){
            try(FileOutputStream outputStream = new FileOutputStream((path))){
                for(T t : data){
                    String json = new Gson().toJson(t);
                    byte[] b = json.getBytes();
                    outputStream.write(b);
                    outputStream.write("\n".getBytes());
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
}
