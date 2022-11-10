package excelReader.physician;

import excelReader.physician.Suffix;

import java.util.Map;

public class Validator {

    public static boolean validateName(String s){
        String[] nameParts = s. split(" ");
        boolean result = false;
        if(nameParts.length == 1 && !(Suffix.isExist(nameParts[0]))){
            return true;
        }
        if (nameParts.length > 1){
            result = true;
           for(String str : nameParts){
                if (Suffix.isExist(str)){
                    result = false;
                }
                if (str.contains(".")){
                    result = false;
               }
            }
           return result;
        }
        return result;
    }

    public static boolean surnameComaCheck(String s){
        return s.contains(",");
    }
    public static  boolean nameComaCheck(String s){
        return s.contains(",");
    }
    public static void  cityExistError(Map<Integer, String> list, int count){
        list.put(count, ("City doesnt exist at row number: " + count));
    }

}
