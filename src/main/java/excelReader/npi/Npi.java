package excelReader.npi;

import excelReader.ProviderUSer;
import excelReader.jsonutils.GetDataFromJsonAsList;
import excelReader.physician.Physician;
import excelReader.toAdd.OrganisationToAdd;

import java.util.ArrayList;
import java.util.List;

public class Npi {
    public static void main(String[] args) {
        NPIMethod();
    }

    public static void NPIMethod(){
        List<Physician> physicians = GetDataFromJsonAsList.jsonDataAsObjectList("E:/physician.json", Physician[].class);
//        List<OrganisationToAdd> organisationToAdds = GetDataFromJsonAsList("E:/")
        List<ProviderUSer> providerUsers = new ArrayList<>();
        for (Physician p : physicians){
            ProviderUSer data = new ProviderUSer();
            data.setNpi(p.getNpi().trim());
            data.setFirstName(p.getName().trim());
            data.setLastName(p.getSurname().trim());
            if(p.getMiddleName().trim().length() == 1){
                data.setFullName(p.getName().trim()+ " "+ p.getMiddleName().trim() + " "+ p.getSurname());
            } else {
                data.setFullName(p.getName().trim()+" "+ p.getSurname());
            }
            data.setPrimaryRoleId("203");
            data.setPracticeName(p.getPracticeName().trim());
            data.setTel(p.getTelephone().trim());


        }
    }

//        private String practiceId;
}
