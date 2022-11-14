package excelReader.jsonutils;


import com.google.gson.Gson;
import excelReader.organisation.Organisation;
import org.apache.poi.hssf.record.ObjRecord;


import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class GetDataFromJsonAsList {
    public  static <T> List<T> jsonDataAsObjectList(String path, Class<T[]> tClass){
        List<T> data = new ArrayList<>();
        Gson gson = new Gson();
        try(Reader reader = new FileReader(path)){
            T[] dataAsArray = gson.fromJson(reader, tClass);
            data = List.of(dataAsArray);
        }catch (IOException e ){
            e.printStackTrace();
        }
        return data;
    }

}

