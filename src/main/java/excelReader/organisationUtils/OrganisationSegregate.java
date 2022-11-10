package excelReader.organisationUtils;

import excelReader.jsonutils.CreateJsonFileFromData;
import excelReader.jsonutils.GetDataFromJsonAsList;
import excelReader.organisation.Address;
import excelReader.organisation.Organisation;
import excelReader.physician.Physician;
import excelReader.toAdd.Contact;
import excelReader.toAdd.OrganisationToAdd;

import java.util.ArrayList;
import java.util.List;

public class OrganisationSegregate {

    public static void checkByPracticeName(){
        List<Organisation> organisations = GetDataFromJsonAsList.jsonDataAsObjectList("/home/maksym/maksStart/organisation.json", Organisation[].class);
        List<Physician> physicians = GetDataFromJsonAsList.jsonDataAsObjectList("/home/maksym/maksStart/physician.json", Physician[].class);

        List<String> organisationName = new ArrayList<>();
        for (Organisation o : organisations){
            organisationName.add(o.getName().trim());
        }

        List<String> organisationFromPhysicians = new ArrayList<>();
        for(Physician p : physicians){
            organisationFromPhysicians.add(p.getPracticeName().trim());
        }

        List<String> difference = new ArrayList<>();
        List<String> dataToUpdate = new ArrayList<>();
        for(String s : organisationFromPhysicians){
            if (!(organisationName.contains(s))){
                difference.add(s);
            }
            else{
                dataToUpdate.add(s);
            }
        }
        List<Physician> physicianDoubleCheck = getPhysicianFromPracticeName(dataToUpdate, physicians);
        List<Organisation> organisationDoubleCheck = getOrganisationFromPracticeName(dataToUpdate, organisations);
        checkByAddress(physicianDoubleCheck, organisationDoubleCheck);

        ArrayList<OrganisationToAdd> dataToAdd = new ArrayList<>();
        for(Physician p : physicians){
            if(difference.contains(p.getPracticeName().trim())){
                Address address = new Address();
                address.setAddress1(p.getAddress());
                address.setCity(p.getCity());
                address.setState(p.getState());
                address.setZip(p.getZip());
                if(p.getAddress2().trim().length() > 0){
                    address.setAddress2(p.getAddress2());
                }
                ArrayList<String> linkedImagine = new ArrayList<>();
                linkedImagine.add("62e0fb0ae399f10007cde128");
                List<Contact> contacts = new ArrayList<>();
                Contact contact = new Contact();
                contact.setTel(p.getTelephone());
                contacts.add(contact);
                OrganisationToAdd data = new OrganisationToAdd();
                data.setName( p.getPracticeName());
                data.setAddress(address);
                data.setLinkedImagingIds(linkedImagine);
                data.setContacts(contacts);
                data.setType("practice");
                data.setFax(p.getFax());
                dataToAdd.add(data);
            }
        }

        List<Organisation> updated = new ArrayList<>();
        for(String s : dataToUpdate){
            String current = s;
            for(Organisation o : organisations){
                if(o.getName().trim().equals(s)){
                    if(o.getType().equals("practice")){
                        ArrayList<String> currentLinked = o.getLinkedImagingIds();
                        currentLinked.add("62e0fb0ae399f10007cde128");
                        o.setLinkedImagingIds(currentLinked);
                        updated.add(o);
                    }
                }
            }
        }
        //        CreateJsonFileFromData.createJsonFileFromList("/home/maksym/maksStart/newPractice.txt", dataToAdd);
//        CreateJsonFileFromData.createJsonFileFromList("/home/maksym/maksStart/updated.txt", updated);
        System.out.println("total: " + organisationFromPhysicians.size());
        System.out.println("need to add as new: " + difference.size());
        System.out.println("add data: " + dataToAdd.size());
        System.out.println("need to update: " + dataToUpdate.size());
        System.out.println("updated: " + updated.size());
        System.out.println("total operation: " +(updated.size()+ dataToAdd.size()));
    }



    private static List<Physician> getPhysicianFromPracticeName(List<String> data, List<Physician> physicians){
        List<Physician> returnData =  new ArrayList<>();
        for(Physician p: physicians){
            if (data.contains(p.getPracticeName().trim())){
                returnData.add(p);
            }
        }
        return returnData;
    }

    private static List<Organisation> getOrganisationFromPracticeName(List<String> data, List<Organisation> organisations){
        List<Organisation> returnData  = new ArrayList<>();
       for(Organisation o : organisations){
           if (data.contains(o.getName().trim())){
               returnData.add(o);
           }
       }
       return returnData;
    }

    private static void checkByAddress(List<Physician> physicians, List<Organisation> organisations){
        for( Physician p : physicians){
            String practiceName = p.getPracticeName().trim();

        }
    }

}
