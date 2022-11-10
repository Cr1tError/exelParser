package excelReader;

import excelReader.jsonutils.CreateJsonFileFromData;
import excelReader.jsonutils.GetDataFromJsonAsList;
import excelReader.organisation.Address;
import excelReader.organisation.Organisation;
import excelReader.organisationUtils.OrganisationSegregate;
import excelReader.physician.Physician;
import excelReader.toAdd.Contact;
import excelReader.toAdd.OrganisationToAdd;

import java.util.*;

public class ExcelReader {

    public static void main(String[] args) {
        OrganisationSegregate.checkByPracticeName();



       // Read data from excel and write into file i json format
       /*
       String pathInput = "/home/maksym/maksStart/data.xlsx";
       String pathOutput = "/home/maksym/maksStart/dataJson.txt";
       "/home/maksym/maksStart/organisation.json"
       ReadDataFromExcel.readANdWriteToFileInJsonFormat(pathInput, pathOutput);
       */

    }
}
