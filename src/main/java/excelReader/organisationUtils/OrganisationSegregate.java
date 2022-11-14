package excelReader.organisationUtils;

import excelReader.ProviderUser;
import excelReader.PublicId.PublicId;
import excelReader.facility.Facility;
import excelReader.jsonutils.CreateJsonFileFromData;
import excelReader.jsonutils.GetDataFromJsonAsList;
import excelReader.organisation.Address;
import excelReader.organisation.Organisation;
import excelReader.physician.Physician;
import excelReader.practiceNameUtils.PracticeNameChanger;
import excelReader.toAdd.Contact;
import excelReader.toAdd.OrganisationToAdd;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class OrganisationSegregate {


    public static void checkByPracticeName(){
        List<Organisation> organisations = GetDataFromJsonAsList.jsonDataAsObjectList("/home/maksym/PRODSCRIPT/organisation.json", Organisation[].class);
        List<Physician> physicians = GetDataFromJsonAsList.jsonDataAsObjectList("/home/maksym/PRODSCRIPT/physician.json", Physician[].class);



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
        //        TODO check before
        PublicId publicIdGeneretor = new PublicId();
        publicIdGeneretor.setCount(36611);

        // step 4 create new practice and store it in list(later we use it list to create Json file to add data in db)
        List<OrganisationToAdd> dataToAddAsNewOrganisation = createOrganisationFromPhysician(dataToAddAsPracticeWithOutDuplicate, publicIdGeneretor);
        int a = 0;

        // step 5 create file in json to add
        CreateJsonFileFromData.createJsonFileFromList("/home/maksym/PRODSCRIPT/ForestCityNewPracticeDev.json", dataToAddAsNewOrganisation);
        int a1 = 1;
        // step 6 after add data to db take all organisathion with Forest City linkeds be ready to delete exist-early data before use.

        // step 7 create list<Organisation> from new file from DB;
        int a2 = 0;
        List<Organisation> newOrgListForestCity = GetDataFromJsonAsList.jsonDataAsObjectList("/home/maksym/PRODSCRIPT/CreatedOrg.json", Organisation[].class);

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
        CreateJsonFileFromData.createJsonFileFromList("/home/maksym/PRODSCRIPT/FacilityForNewPractice.json", newLocationForEach);
        int a4= 0;
//        Add  to db and after get json file from db and create list<Facilyty>
        List<Facility> createdFacilityFromDB = GetDataFromJsonAsList.jsonDataAsObjectList("/home/maksym/PRODSCRIPT/facilityCreatedToNewLocations.json", Facility[].class);
        List<ProviderUser> userToCreate = new ArrayList<>();
        for(Physician p : dataToAddAsPracticeWithOutDuplicate){
            for(Organisation o : newOrgListForestCity){
                if (o.getName().equals(p.getPracticeName())){
                    Map<String, String> ids = (Map<String, String>) o.get_id();
                    String id = ids.get("$oid");

                    for(Facility f : createdFacilityFromDB){
                        Map<String, String> facilityIds = (Map<String, String>) f.get_id();
                        String fId = facilityIds.get("$oid");
                        if(f.getPracticeId().equals(id)){
                            ProviderUser providerUSer = new ProviderUser();
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
                            providerUSer.setSuffix(p.getSuffix());
                            providerUSer.setStatus("PENDING");
                            providerUSer.setLinkedFacilityId(new String[]{fId});
//                            TODO here id from prod forest city
                            providerUSer.setImagingOrgIds(new String[]{"62e0fb0ae399f10007cde128"});
                            userToCreate.add(providerUSer);
                            break;
                        }
                    }

                }
            }
        }
//        step 8 create json and add user to forest city
        CreateJsonFileFromData.createJsonFileFromList("/home/maksym/PRODSCRIPT/newUserToForestCity.json", userToCreate);
        int a12 = 0;
//        TODO Physician or admin must to end data of each physician to made it show up in practice because its no full data
//
//
//        step 9 get data from db all created user in prev step.
        List<ProviderUser> createdUsers = GetDataFromJsonAsList.jsonDataAsObjectList("/home/maksym/PRODSCRIPT/createdProviderUsers.json", ProviderUser[].class);

        List<Facility> newFacilityToCreate = new ArrayList<>();
        List<ProviderUser> newUserToCreate = new ArrayList<>();
        List<ProviderUser> userToUpdateFromExist = new ArrayList<>();
        Map<Integer, Facility> mapTOFutureUserCreate = new HashMap<>();
        Map<Integer, ProviderUser> mapProviderUSer = new HashMap<>();
        int count = 1;
        for(Physician p : dataAsDuplicate){

            ProviderUser newUser = new ProviderUser();
            Facility newFacility = new Facility();

            boolean checkUser = false;
            boolean checkFacility = false;

            for(ProviderUser user : createdUsers){
                if(user.getNpi().equals(p.getNpi())){
                    checkUser = true;
                    newUser = user;
                }
            }
            for(Facility facility : createdFacilityFromDB){
                if(facility.getPracticeName().equals(p.getPracticeName())){
                    if((facility.getAddress().getCity().equals(p.getCity())) &&(facility.getAddress().getZip().equals(p.getZip()))){
                        checkFacility = true;
                        newFacility = facility;
                    }
                }
            }
            if(checkUser && checkFacility){
                String[] userFacility = newUser.getLinkedFacilityId();
                Map<String, String> fIds = (Map<String, String>) newFacility.get_id();
                String fId = fIds.get("$oid");
                for(String s : userFacility){
                    if(!(s.equals(fId))){
                        String[] updatedUserFacility = new String[userFacility.length+1];
                        for(int i = 0; i < updatedUserFacility.length; i++){
                            if(i == updatedUserFacility.length - 1){
                                updatedUserFacility[i] = fId;
                                continue;
                            }
                            updatedUserFacility[i] = userFacility[i];
                        }
                        newUser.setLinkedFacilityId(updatedUserFacility);
                    }
                }
                userToUpdateFromExist.add(newUser);
            }

            if(checkUser && !checkFacility){
                Organisation org = new Organisation();
                Facility facility = new Facility();
                String pId;
                for(Organisation o : newOrgListForestCity){
                    if(o.getName().equals(newUser.getPracticeName())){
                        org = o;
                    }
                }
                Map<String, String> pIds = (Map<String, String>) org.get_id();
                pId = pIds.get("$oid");
                facility.setPracticeId(pId);
                facility.setPracticeName(org.getName());
                String name = org.getAddress().getAddress1() + " "+org.getAddress().getCity() +" "+ org.getAddress().getState()+ " "+ org.getAddress().getZip();
                facility.setName(name);
//            facility.setDescription(o.getName() + " location");
                facility.setDescription("set description when you check info");
                facility.setTimeZoneName("");
                facility.setTimeZoneId("");
                Address address = org.getAddress();
                facility.setAddress(address);
                facility.setTel(org.getTel());
                facility.setFax(org.getFax());
                facility.setStatus("PENDING");
                newFacilityToCreate.add(facility);
//                TODO here you have 2 maps to check witch facility linked to witch user.
                mapTOFutureUserCreate.put(count, facility);
                mapProviderUSer.put(count, newUser);
                count++;
            }


            if(!checkUser && checkFacility){
                ProviderUser user = new ProviderUser();
                user.setNpi(p.getNpi());
                user.setFirstName(p.getName());
                user.setLastName(p.getSurname());
                if(p.getMiddleName().trim().length() > 0){
                    user.setMiddleName(p.getMiddleName());
                    user.setFullName(p.getName() +" "+p.getMiddleName()+" "+p.getSurname());
                } else{
                    user.setFullName(p.getName() +" "+p.getSurname());
                }
                user.setPrimaryRoleId("203");
                user.setPracticeId(newFacility.getPracticeId());
                user.setPracticeName(p.getPracticeName());
                user.setTel(p.getTelephone());
                user.setSuffix(p.getSuffix());
                user.setStatus("PENDING");
                Map<String, String> fIds = (Map<String, String>) newFacility.get_id();
                String fId = fIds.get("$oid");
                user.setLinkedFacilityId(new String[]{fId});
//                            TODO here id from prod forest city
                user.setImagingOrgIds(new String[]{"62e0fb0ae399f10007cde128"});
                newUserToCreate.add(user);
            }

            if(!checkUser && !checkFacility){
                Organisation org = new Organisation();
                Facility facility = new Facility();
                String pId;
                for(Organisation o : newOrgListForestCity){
                    if(o.getName().equals(p.getPracticeName())){
                        org = o;
                    }
                }
                Map<String, String> pIds = (Map<String, String>) org.get_id();
                pId = pIds.get("$oid");
                facility.setPracticeId(pId);
                facility.setPracticeName(org.getName());
                String name = org.getAddress().getAddress1() + " "+org.getAddress().getCity() +" "+ org.getAddress().getState()+ " "+ org.getAddress().getZip();
                facility.setName(name);
//            facility.setDescription(o.getName() + " location");
                facility.setDescription("set description when you check info");
                facility.setTimeZoneName("");
                facility.setTimeZoneId("");
                Address address = org.getAddress();
                facility.setAddress(address);
                facility.setTel(org.getTel());
                facility.setFax(org.getFax());
                facility.setStatus("PENDING");
                newFacilityToCreate.add(facility);
                mapTOFutureUserCreate.put(count, facility);

                ProviderUser user = new ProviderUser();
                user.setNpi(p.getNpi());
                user.setFirstName(p.getName());
                user.setLastName(p.getSurname());
                if(p.getMiddleName().trim().length() > 0){
                    user.setMiddleName(p.getMiddleName());
                    user.setFullName(p.getName() +" "+p.getMiddleName()+" "+p.getSurname());
                } else{
                    user.setFullName(p.getName() +" "+p.getSurname());
                }
                user.setPrimaryRoleId("203");
                user.setPracticeId(facility.getPracticeId());
                user.setPracticeName(p.getPracticeName());
                user.setTel(p.getTelephone());
                user.setSuffix(p.getSuffix());
                user.setStatus("PENDING");
//                Map<String, String> fIds = (Map<String, String>) facility.get_id();
//                String fId = fIds.get("$oid");
//                user.setLinkedFacilityId(new String[]{fId});
//                            TODO here id from prod forest city
                user.setImagingOrgIds(new String[]{"62e0fb0ae399f10007cde128"});
//                TODO here you have 2 maps to check witch facility linked to witch user.
                mapProviderUSer.put(count, user);
                count++;
            }

        }


//        step 9 delete duplicate in new facility and create new maps to know witch user linked with facility
        List<Facility> newFacilityWithOutDuplicate = new ArrayList<>();
        Map<Integer, ProviderUser> newProviderUserMap = new HashMap<>();
        Map<Integer, Facility> newFacilityMap = new HashMap<>();
        int a6 = 0;

       for(Integer i : mapTOFutureUserCreate.keySet()){
           if(newFacilityWithOutDuplicate.isEmpty()){
               newFacilityWithOutDuplicate.add(mapTOFutureUserCreate.get(i));
               newProviderUserMap.put(i, mapProviderUSer.get(i));
               newFacilityMap.put(i, mapTOFutureUserCreate.get(i));
               continue;
           }
           boolean check = false;
           for(Facility f : newFacilityWithOutDuplicate){
               if((f.getPracticeName().equals(mapTOFutureUserCreate.get(i).getPracticeName())) &&
                 ((f.getAddress().getCity().equals(mapTOFutureUserCreate.get(i).getAddress().getCity()))) &&
                 ((f.getAddress().getZip().equals(mapTOFutureUserCreate.get(i).getAddress().getZip())))){
                newFacilityMap.put(i, f);
                newProviderUserMap.put(i, mapProviderUSer.get(i));
                check = false;
                break;
               } else{
                   check = true;
               }
           }
           if(check){
               newFacilityWithOutDuplicate.add(mapTOFutureUserCreate.get(i));
               newProviderUserMap.put(i, mapProviderUSer.get(i));
               newFacilityMap.put(i, mapTOFutureUserCreate.get(i));
           }
       }

//       Step 10 create new facility from createdFile in DB
       CreateJsonFileFromData.createJsonFileFromList("/home/maksym/PRODSCRIPT/newFacilityStage9.json", newFacilityWithOutDuplicate);
        int a7 = 0;

//        Step 11 get all createdFacility from DB
        List<Facility> facilityStage11  = GetDataFromJsonAsList.jsonDataAsObjectList("/home/maksym/maksStart/JsonDAta/CreatedfacilityAfterStage10.json", Facility[].class);
        int a8 = 0;
//
//       Step 12  create list of id to delete create file to update users linked imagine in DB;

        List<String> idToDelete = new ArrayList<>();
        for(ProviderUser u : userToUpdateFromExist){
            Map<String, String> uIds= (Map<String, String>) u.get_id();
            idToDelete.add(uIds.get("$oid"));
        }
        for(ProviderUser u : userToUpdateFromExist){
            u.set_id(null);
        }
        writeSQLToDelete(idToDelete);
        CreateJsonFileFromData.createJsonFileFromList("/home/maksym/PRODSCRIPT/UpdateExistingUserStage12.json", userToUpdateFromExist);
        int a9 = 0;


//        step 13 use script to delete users and use json to add users with new data

//        step 14 add facility ID to user without facility id
        List<ProviderUser> withFacilityLast = new ArrayList<>();
        for(int i = 1 ;  i <= newProviderUserMap.size(); i++){
            ProviderUser providerUser = newProviderUserMap.get(i);
            Facility facility = newFacilityMap.get(i);
            for(Facility f : facilityStage11){
                if(facility.getPracticeName().equals(f.getPracticeName()) &&
                   facility.getAddress().getAddress1().equals(f.getAddress().getAddress1())&&
                   facility.getAddress().getZip().equals(f.getAddress().getZip())){
                    Map<String, String> fIds = (Map<String, String>) f.get_id();
                    String id = fIds.get("$oid");
                    providerUser.setLinkedFacilityId(new String[]{id});
                    withFacilityLast.add(providerUser);
                    break;
                }
            }
        }

        List<ProviderUser> lastStand = new ArrayList<>();
        List<ProviderUser> repeatAbel =new ArrayList<>();
        for(ProviderUser u : withFacilityLast){
            if(lastStand.isEmpty()){
                lastStand.add(u);
                continue;
            }
            boolean check = false;
            for(ProviderUser user : lastStand){
                if(user.getNpi().equals(u.getNpi()) &&
                   user.getLinkedFacilityId().equals(u.getLinkedFacilityId())){
                    check = false;
                    break;
                } else {
                    check = true;
                }
            }
            if(check){
                lastStand.add(u);
            } else{
                repeatAbel.add(u);
            }
        }

//        step 15 create json and add last part of physician
//        check with current and delete duplicate TODO
//        before do add once again chewck pls
        CreateJsonFileFromData.createJsonFileFromList("/home/maksym/PRODSCRIPT/lastAddingStage15.json", withFacilityLast);
        CreateJsonFileFromData.createJsonFileFromList("/home/maksym/PRODSCRIPT/reapetStage15.json", repeatAbel);
        CreateJsonFileFromData.createJsonFileFromList("/home/maksym/PRODSCRIPT/lastStand.json", lastStand);
        int a11 = 0;
   //128 dont add







        int a10 = 0;
//        newOrgListForestCity mapTOFutureUserCreate mapProviderUSer

























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

    private static List<OrganisationToAdd> createOrganisationFromPhysician(List<Physician> physicians, PublicId publicId){
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
            linkedImagine.add("62e0fb0ae399f10007cde128");
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
            data.setPublicId(publicId.generateOrganizationPubId());
            dataToAddAsNewOrganisation.add(data);
        }
        return dataToAddAsNewOrganisation;
    }
    private static void writeSQLToDelete(List<String> list){
        list =deleteDuplicate(list);
        try(BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream("/home/maksym/PRODSCRIPT/deleteSQL"))){
            for(String s : list){
                String query = "db.getCollection(\"providerUser\").deleteOne({\"_id\" : ObjectId(\""+ s + "\")});";
                outputStream.write(query.getBytes());
                outputStream.write("\n".getBytes());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    private static List<String> deleteDuplicate(List<String> data){
      return data.stream().distinct().collect(Collectors.toList());
    }

}
