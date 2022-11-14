package excelReader.organisationUtils;

import excelReader.ProviderUSer;
import excelReader.facility.Facility;
import excelReader.jsonutils.CreateJsonFileFromData;
import excelReader.jsonutils.GetDataFromJsonAsList;
import excelReader.organisation.Address;
import excelReader.organisation.Organisation;
import excelReader.physician.Physician;
import excelReader.practiceNameUtils.PracticeNameChanger;
import excelReader.toAdd.Contact;
import excelReader.toAdd.OrganisationToAdd;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.STSourceType;

import java.util.*;

public class OrganisationSegregate {

    public static void checkByPracticeName(){
        List<Organisation> organisations = GetDataFromJsonAsList.jsonDataAsObjectList("E:/organisation.json", Organisation[].class);
        List<Physician> physicians = GetDataFromJsonAsList.jsonDataAsObjectList("E:/physician.json", Physician[].class);

        // step 1 correct practice name for all physician;
        List<Physician> correctedNamePracticePhysicians = correctPracticeName(physicians);

        List<String> organisationNameList = new ArrayList<>();
        for (Organisation o : organisations){
            organisationNameList.add(o.getName().trim());
        }

        // step 2 segregate to new and existing practice
        List<Physician> dataToAddAsNewPractice = new ArrayList<>();   //TODO dataToAddAsNewPractice later use this one to create Physician
        List<Physician> dataToUpdatePractice = new ArrayList<>();
           for(Physician p : correctedNamePracticePhysicians){
               if(!(organisationNameList.contains(p.getPracticeName()))){
                   dataToAddAsNewPractice.add(p);
               } else {
                   dataToUpdatePractice.add(p);
               }
           }

        //  step 3 prepare data to create new organisation  to link it to ForestCity
        List<Physician> dataAsDuplicate = new ArrayList<>();
        List<Physician> dataToAddAsPracticeWithOutDuplicate = new ArrayList<>();

        for(Physician p : dataToAddAsNewPractice){
            if(dataToAddAsPracticeWithOutDuplicate.isEmpty()){
                dataToAddAsPracticeWithOutDuplicate.add(p);
                continue;
            }
            boolean check = false;
            for(Physician physician : dataToAddAsPracticeWithOutDuplicate){
                if(physician.getPracticeName().trim().equals(p.getPracticeName().trim())){
                    check = false;
                    break;
                } else {
                    check = true;
                }
            }
            if(check){
                dataToAddAsPracticeWithOutDuplicate.add(p);
            } else {
                dataAsDuplicate.add(p);
            }
        }

        // step 4 create new practice and store it in list(later we use it list to create Json file to add data in db)
        List<OrganisationToAdd> dataToAddAsNewOrganisation = createOrganisationFromPhysician(dataToAddAsPracticeWithOutDuplicate);


        // step 5 create file in json to add
//        CreateJsonFileFromData.createJsonFileFromList("E:/ForestCityNewPracticeDev.json", dataToAddAsNewOrganisation);

        // step 6 after add data to db take all organisathion with Forest City linkeds be ready to delete exist-early data before use.

        // step 7 create list<Organisation> from new file from DB;
        List<Organisation> newOrgListForestCity = GetDataFromJsonAsList.jsonDataAsObjectList("E:/CreatedOrg.json", Organisation[].class);

        List<Facility> newLocationForEach = new ArrayList<>();
        for(Organisation o : newOrgListForestCity){
            Facility facility = new Facility();
            Map<String, String> ids = (Map<String, String>) o.get_id();
            String id = ids.get("$oid");
            facility.setPracticeId(id);
            facility.setPracticeName(o.getName());
            String name = o.getAddress().getAddress1() + " "+o.getAddress().getCity() +" "+ o.getAddress().getState()+ " "+ o.getAddress().getZip();
            facility.setName(name);
//            facility.setDescription(o.getName() + " location");
            facility.setDescription("set description when you check info");
            facility.setTimeZoneName("");
            facility.setTimeZoneId("");
            Address address = o.getAddress();
            facility.setAddress(address);
            facility.setTel(o.getTel());
            facility.setFax(o.getFax());
            facility.setStatus("PENDING");
            newLocationForEach.add(facility);
        }
//        TODO here we create file and use it to add location to new practice.
//        CreateJsonFileFromData.createJsonFileFromList("E:/FacilityForNewPractice.json", newLocationForEach);
//        Add  to db and after get json file from db and create list<Facilyty>
        List<Facility> createdFacilityFromDB = GetDataFromJsonAsList.jsonDataAsObjectList("E:/facilityCreatedToNewLocations.json", Facility[].class);
        List<ProviderUSer> userToCreate = new ArrayList<>();
        for(Physician p : dataToAddAsPracticeWithOutDuplicate){
            for(Organisation o : newOrgListForestCity){
                if (o.getName().equals(p.getPracticeName())){
                    Map<String, String> ids = (Map<String, String>) o.get_id();
                    String id = ids.get("$oid");

                    for(Facility f : createdFacilityFromDB){
                        Map<String, String> facilityIds = (Map<String, String>) f.get_id();
                        String fId = facilityIds.get("$oid");
                        if(f.getPracticeId().equals(id)){
                            ProviderUSer providerUSer = new ProviderUSer();
                            providerUSer.setNpi(p.getNpi());
                            providerUSer.setFirstName(p.getName());
                            providerUSer.setLastName(p.getSurname());
                            if(p.getMiddleName().trim().length() > 0){
                                providerUSer.setMiddleName(p.getMiddleName());
                                providerUSer.setFullName(p.getName() +" "+p.getMiddleName()+" "+p.getSurname());
                            } else{
                                providerUSer.setFullName(p.getName() +" "+p.getSurname());
                            }
                            providerUSer.setPrimaryRoleId("203");
                            providerUSer.setPracticeId(f.getPracticeId());
                            providerUSer.setPracticeName(o.getName());
                            providerUSer.setTel(p.getTelephone());
                            providerUSer.setStatus("PENDING");
                            providerUSer.setLinkedFacilityId(new String[]{fId});
//                            TODO here id from prod forest city
                            providerUSer.setImagingOrgIds(new String[]{"636db81417ec830007c8ab32"});
                            userToCreate.add(providerUSer);
                            break;
                        }
                    }

                }
            }
        }
//        step 8 create json and add user to forest city
//        CreateJsonFileFromData.createJsonFileFromList("E:/newUserToTest.json", userToCreate);
//        TODO Physician or admin must to end data of each physician to made it show up in practice because its no full data
//
//
//        step 9 get data from db all created user in prev step.
        List<ProviderUSer> createdUsers = GetDataFromJsonAsList.jsonDataAsObjectList("E:/createdProviderUsers.json", ProviderUSer[].class);
        for(Physician p : dataAsDuplicate){
            String practiceName = p.getPracticeName();
            String City = p.getCity();
            String ZIP = p.getZip();

        }







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


        List<Physician> dataToWork = getPhysicianFromPracticeName(difference, physicians);
        List<Physician> repeated = new ArrayList<>();
        List<Physician> dataToAddAsPracticeWithOutDuplicate1 = new ArrayList<>();
        for(Physician p : dataToWork){
            if(dataToAddAsPracticeWithOutDuplicate1.isEmpty()){
                dataToAddAsPracticeWithOutDuplicate1.add(p);
                continue;
            }
            boolean check = false;
            for(Physician physician : dataToAddAsPracticeWithOutDuplicate1){
                if(physician.getPracticeName().trim().equalsIgnoreCase(p.getPracticeName().trim())){
                    check = false;
                    break;
                } else {
                    check = true;
                }
            }
            if(check){
                dataToAddAsPracticeWithOutDuplicate1.add(p);
            } else {
                repeated.add(p);
            }
        }

//        TODO to check if there are no duplicate
//        CreateJsonFileFromData.createJsonFileFromList("E:/checkJson.json", dataToAddAsPracticeWithOutDuplicate1);
//        CreateJsonFileFromData.createJsonFileFromList("E:/checkRepeatedJson.json", repeated);
//        TODO dataToAddAsPracticeWithOutDuplicate1: 1-t stepp adda practice to db, 2-d get all add practice to create location for them, 3) if we found second instance of this practice add new location facility also in this
//         time we add physician to this location (check name and address in physician data, and found practice and location to practice)



        Map<OrganisationToAdd, Map<Facility, Physician>>  dataMap = new HashMap<>();

        for(Physician p : dataToWork){
            OrganisationToAdd organisationToAdd = createOrganisationToADd(p);

        }




        List<Physician> toSort = new ArrayList<>();
        for(Physician p : physicians){
            if(difference.contains(p.getPracticeName().trim())){
                toSort.add(p);
            }
        }

        List<Physician> physicianDoubleCheck = getPhysicianFromPracticeName(dataToUpdate, physicians);
        List<Organisation> organisationDoubleCheck = getOrganisationFromPracticeName(dataToUpdate, organisations);
//        checkByAddress(physicianDoubleCheck, organisationDoubleCheck);

        ArrayList<OrganisationToAdd> dataToAdd = new ArrayList<>();
        for(Physician p : physicians){
            if(difference.contains(p.getPracticeName().trim())){
                Address address = new Address();
                address.setAddress1(p.getAddress().trim());
                address.setCity(p.getCity().trim());
                address.setState(p.getState().trim());
                address.setZip(p.getZip().trim());
                if(p.getAddress2().trim().length() > 0){
                    address.setAddress2(p.getAddress2().trim());
                }
                ArrayList<String> linkedImagine = new ArrayList<>();
//                TODO here you need put current Imagine Center ID -> 62e0fb0ae399f10007cde128 -> forest city prod db
                linkedImagine.add("636db81417ec830007c8ab32");
                List<Contact> contacts = new ArrayList<>();
                Contact contact = new Contact();
                contact.setTel(p.getTelephone());
                contacts.add(contact);
                OrganisationToAdd data = new OrganisationToAdd();
                data.setName(p.getPracticeName().trim());
                data.setAddress(address);
                data.setLinkedImagingIds(linkedImagine);
                data.setContacts(contacts);
                data.setType("practice");
                data.setFax(p.getFax());
                dataToAdd.add(data);
            }
        }

        List<OrganisationToAdd> withOutDuplicate = new ArrayList<>();
        List<OrganisationToAdd> duplicated = new ArrayList<>();
        for (OrganisationToAdd o : dataToAdd){
            if(withOutDuplicate.isEmpty()){
                withOutDuplicate.add(o);
                continue;
            }
            if(!checkForDuplicate(withOutDuplicate, o)){
                withOutDuplicate.add(o);
            } else{
                duplicated.add(o);
            }
        }

        List<Organisation> updated = new ArrayList<>();
        List<Organisation> toUpdateWithoutDuplicate = new ArrayList<>();
        for(Organisation o : organisationDoubleCheck){
            if (toUpdateWithoutDuplicate.isEmpty()){
                toUpdateWithoutDuplicate.add(o);
                 continue;
            }
            if(!checkForDuplicate(toUpdateWithoutDuplicate, o)){
                toUpdateWithoutDuplicate.add(o);
            }
        }

        for(Organisation o  :toUpdateWithoutDuplicate){
            if(o.getType().equalsIgnoreCase("practice")){
                ArrayList<String> currentLinked = o.getLinkedImagingIds();
                currentLinked.add("62e0fb0ae399f10007cde128");
                o.setLinkedImagingIds(currentLinked);
                updated.add(o);
            }
        }
//        TODO latter we can add endpoint to use this 2 method to create file on your local machine in json format
//          CreateJsonFileFromData.createJsonFileFromList("E:/newPracticeForestCity.json", withOutDuplicate);
//        CreateJsonFileFromData.createJsonFileFromList("E:/toUpdate.json", updated);
        System.out.println("total: " + organisationFromPhysicians.size());
        System.out.println("need to add as new: " + difference.size());
        System.out.println("add data: " + dataToAdd.size());
        System.out.println("need to update: " + dataToUpdate.size());
        System.out.println("total operation: " +(updated.size()+ dataToAdd.size()));
        System.out.println("withOutDuplicateNew: " + withOutDuplicate.size());
        System.out.println("updated: " + updated.size());
        System.out.println("WithOutDuplicateUpdate: " + toUpdateWithoutDuplicate.size());
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

//   private List<OrganisationToAdd> removeDuplicate(List<OrganisationToAdd> list){
//        List<OrganisationToAdd> resultList = new ArrayList<>(list);
//        int count = 0;
//        for(OrganisationToAdd o : list){
//            if()
//        }
//   }
//    private Map<String, Map<String, Physician>> sortedByName(List<Physician> physicians){
//
//    }
    private static <T> boolean  checkForDuplicate(List<T> list, T o){
        for (T t : list){
            if (t.equals(o)){
                return true;
            }
        }
        return false;
    }

    private static OrganisationToAdd createOrganisationToADd(Physician p){
        Address address = new Address();
        address.setAddress1(p.getAddress().trim());
        address.setCity(p.getCity().trim());
        address.setState(p.getState().trim());
        address.setZip(p.getZip().trim());
        if(p.getAddress2().trim().length() > 0){
            address.setAddress2(p.getAddress2().trim());
        }
        ArrayList<String> linkedImagine = new ArrayList<>();
//      TODO here you need put current Imagine Center ID -> 62e0fb0ae399f10007cde128 -> forest city prod db
        linkedImagine.add("636db81417ec830007c8ab32");
        List<Contact> contacts = new ArrayList<>();
        Contact contact = new Contact();
        contact.setTel(p.getTelephone());
        contacts.add(contact);
        OrganisationToAdd data = new OrganisationToAdd();
        data.setName(p.getPracticeName().trim());
        data.setAddress(address);
        data.setLinkedImagingIds(linkedImagine);
        data.setContacts(contacts);
        data.setType("practice");
        data.setFax(p.getFax());
        return data;
    }

//    private static  Facility createFacilityFromPhysician(Physician p, Organisation organisation){
//        Facility facility = new Facility();
//        facility.setPracticeId(organisation.getId());
//        facility.setPracticeName(organisation.getName());
//
//    }
    private static List<Physician> correctPracticeName(List<Physician> data){

        List<Physician> returnData = new ArrayList<>();
//        List<String> newPracticeNameList = new ArrayList<>();
        for(Physician p : data){
            String newPracticeName = PracticeNameChanger.changePractice(p.getPracticeName().trim());
            Physician newP = p;
            newP.setPracticeName(newPracticeName);
//            newPracticeNameList.add(newP.getPracticeName());
            returnData.add(newP);
        }
//        TODO check how its work in json format
//        CreateJsonFileFromData.createJsonFileFromList("E:/practiceName.json", newPracticeNameList);
        return returnData;
    }

    private static List<OrganisationToAdd> createOrganisationFromPhysician(List<Physician> physicians){
        ArrayList<OrganisationToAdd> dataToAddAsNewOrganisation = new ArrayList<>();
        for(Physician p : physicians){
            Address address = new Address();
            address.setAddress1(p.getAddress().trim());
            address.setCity(p.getCity().trim());
            address.setState(p.getState().trim());
            address.setZip(p.getZip().trim());
            if(p.getAddress2().trim().length() > 0){
                address.setAddress2(p.getAddress2().trim());
            }
            ArrayList<String> linkedImagine = new ArrayList<>();
            //  TODO here you need put current Imagine Center ID -> 62e0fb0ae399f10007cde128 -> forest city prod db
            linkedImagine.add("636db81417ec830007c8ab32");
            List<Contact> contacts = new ArrayList<>();
            Contact contact = new Contact();
            contact.setTel(p.getTelephone());
            contacts.add(contact);
            OrganisationToAdd data = new OrganisationToAdd();
            data.setName(p.getPracticeName().trim());
            data.setAddress(address);
            data.setLinkedImagingIds(linkedImagine);
            data.setContacts(contacts);
            data.setType("practice");
            data.setFax(p.getFax());
            dataToAddAsNewOrganisation.add(data);
        }
        return dataToAddAsNewOrganisation;
    }

}
