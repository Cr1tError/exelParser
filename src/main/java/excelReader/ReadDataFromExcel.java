package excelReader;

import com.google.gson.Gson;
import excelReader.physician.Physician;
import excelReader.physician.Suffix;
import excelReader.physician.Validator;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ReadDataFromExcel {
    private  static Integer count = 2;
    private  static Map<Integer, String> errorLog = new TreeMap<>();
    private  static Map<Integer, String> data  = new TreeMap<>();
    private  static List<Physician> physicians = new ArrayList<>();

    public static void readANdWriteToFileInJsonFormat(String pathInput, String pathOutput) {
        try (FileInputStream inputStream = new FileInputStream(pathInput);
             FileOutputStream outputStream = new FileOutputStream(new File(pathOutput))) {
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rows = sheet.getLastRowNum();
            int cols = sheet.getRow(1).getLastCellNum();
            DataFormatter dataFormatter = new DataFormatter();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            for(int r = 1; r<= rows; r++){
                String practiceName = null, mainAddress = null, secondAddress = null, city = null, state = null , zip = null, telephone = null, fax = null, npi = null;
                String name = "", surname = "", middleName = null;
                String suffix = null, prefix = null;
                XSSFRow row = sheet.getRow(r);
                for(int c = 0; c< cols; c++){
                    if(c == 1 ){
                        practiceName = row.getCell(c).getStringCellValue();
                        continue;
                    }
                    if (c == 2){
                        mainAddress = row.getCell(c).getStringCellValue();
                        continue;
                    }
                    if (c == 3 ){
                        try{
                            switch(row.getCell(c).getCellType())
                            {
                                case STRING: secondAddress = row.getCell(c).getStringCellValue(); break;
                                case NUMERIC:secondAddress = dataFormatter.formatCellValue(row.getCell(c), formulaEvaluator); break;
                            }
                            continue;
                        } catch (NullPointerException e){
                            continue;
                        }
                    }
                    if (c == 4){
                        try{
                            city = row.getCell(c).getStringCellValue();
                            continue;
                        } catch (NullPointerException e){
                            Validator.cityExistError(errorLog, count);
                            continue;
                        }

                    }
                    if (c == 5){
                        state = row.getCell(c).getStringCellValue();
                        continue;
                    }
                    if(c == 6){
                        zip = dataFormatter.formatCellValue(row.getCell(c), formulaEvaluator);
                        continue;
                    }
                    if(c == 7){
                        telephone = dataFormatter.formatCellValue(row.getCell(c), formulaEvaluator);
                        continue;
                    }
                    if(c == 8 ){
                        fax = dataFormatter.formatCellValue(row.getCell(c), formulaEvaluator);
                        if(fax.length() == 0){
                            fax = null;
                        }
                        continue;
                    }
                    if (c == 9){
                        npi = dataFormatter.formatCellValue(row.getCell(c), formulaEvaluator);
                        continue;
                    }
                    XSSFCell cell = row.getCell(c);
                    if(count == 2){
                        int a = 0;
                    }
                    if(Validator.surnameComaCheck(cell.getStringCellValue(). trim())){
                        String cellValue = cell.getStringCellValue();
                        int comaForSplitIndex = cellValue.trim().indexOf(",");
                        String nameValue = cellValue.trim().substring(0, comaForSplitIndex);
                        String[] nameSurname =  new String[2];
                        nameSurname[0] = nameValue;
                        nameSurname[1] = cellValue.trim().substring(comaForSplitIndex+1).trim();
                        if(!Validator.nameComaCheck(nameSurname[1])){
                            surname = nameSurname[0];
                            String[] otherData = nameSurname[1].trim().split(" ");
                            for(int i = 0; i < otherData.length; i++){
                                if(otherData.length == 1){
                                    name = otherData[0];
                                    continue;
                                }

                                if (otherData.length == 2){
                                    if (Suffix.isExist(otherData[1])){
                                        name = otherData[0];
                                        suffix = otherData[1];
                                        break;
                                    } else if(otherData[1].length() == 1){
                                        name = otherData[0];
                                        middleName = otherData[1];
                                        break;
                                    } else if (otherData[1].length() > 1 && !(Suffix.isExist(otherData[1]))) { /* TODO add prefix check*/
                                        name = otherData[0] + " " + otherData[1];
                                        break;
                                    }
                                }
                                if (otherData.length == 3){
                                    if(Suffix.isExist(otherData[2])){
                                        if(!(otherData[1].length() == 1)){ /* TODO add prefix check*/
                                            name = otherData[0] + " " + otherData[1];
                                            suffix = otherData[2];
                                            continue;
                                        }
                                        name = otherData[0];
                                        middleName =otherData[1];
                                        suffix = otherData[2];
                                    }
                                }

                                if ( otherData.length > 2){
                                    for (int  k = 1; k < otherData.length; k++){
                                        StringBuilder nameBuilder = new StringBuilder(otherData[0]);
                                        if(k != (otherData.length -1)){
                                            if (otherData[k].length() == 1){
                                                middleName = otherData[k];
                                            } else if(!Suffix.isExist(otherData[k]) && otherData[k].length() > 1){ /* TODO add prefix check*/
                                                nameBuilder.append(" ").append(otherData[k]);
                                            }
                                        }
                                        suffix = otherData[k];
                                        name = nameBuilder.toString();
                                    }
                                }
                            }
                        } else {
                            errorLog.put(count, ("extra coma exist in row number: " + count));
                        }
                    }
                    else {
                        errorLog.put(count, ("Coma for surname for split is missing at row: " + count));
                    }

                }
                if(!(Validator.validateName(name))){
                    errorLog.put(count, ("name: " + name + " invalid at row number: " + count));
                }
                StringBuilder stringBuilder = new StringBuilder(String.valueOf(count)).append(") ").append("Name: ").append(name).append(", ");

                if (middleName != null){
                    stringBuilder.append("middle name: ").append(middleName).append(", ");
                }
                stringBuilder.append("surname: ").append(surname).append(", ");
                if( prefix != null){
                    stringBuilder.append("prefix: ").append(prefix).append(", ");
                }
                if( suffix != null){
                    stringBuilder.append("suffix: ").append(suffix).append(", ");
                }
//                String practiceName = null, mainAddress = null, secondAddress = null, city = null, state = null , zip = null, telephone = null, fax = null, npi = null;
                if ( practiceName != null){
                    stringBuilder.append("Practice name: ").append(practiceName.trim()).append(", ");
                }
                if (mainAddress != null){
                    stringBuilder.append("main address: ").append(mainAddress.trim()).append(", ");
                }
                if( secondAddress != null){
                    stringBuilder.append("second address: ").append(secondAddress.trim()).append(", ");
                }
                if (city != null){
                    stringBuilder.append("city: ").append(city.trim()).append(", ");
                }
                if (state != null){
                    stringBuilder.append("state: ").append(state.trim()).append(", ");
                }
                if ( zip != null ){
                    stringBuilder.append("zip: ").append(zip.trim()).append(", ");
                }
                if ( telephone != null){
                    stringBuilder.append("telephone: ").append(telephone.trim()).append(", ");
                }
                if ( fax != null){
                    stringBuilder.append("fax: ").append(fax.trim()).append(", ");
                }
                if ( npi != null){
                    stringBuilder.append("npi: ").append(npi.trim()).append(".");
                }
                Physician physician = createfromExcel(name, surname, middleName, suffix, practiceName, mainAddress, secondAddress, city, state, zip, telephone, fax, npi);
                physicians.add(physician);
                data.put(count, stringBuilder.toString());
                count++;
            }
            // write to file in json format
            for(Physician p : physicians){
                String json = new Gson().toJson(p);
                byte[] b = json.getBytes();
                outputStream.write(b);
                outputStream.write("\n".getBytes());
            }

        } catch (IOException e){
            System.err.println("File not found at path: " + pathInput);
        }
    }

    private static Physician createfromExcel(String name, String surname, String middleName, String suffix, String practiceName, String address, String address2, String city, String state, String zip, String telephone, String fax, String npi){
        if(middleName == null){
            middleName = "";
        }
        if(address2 == null){
            address2 = "";
        }
        if (zip == null){
            zip = "";
        }
        if (fax == null){
            fax = "";
        }
        return new Physician(name, surname, middleName, suffix, practiceName, address, address2, city, state, zip, telephone, fax, npi);
    }
}

