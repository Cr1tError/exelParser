package excelReader.toAdd;

import excelReader.organisation.Address;
import excelReader.organisation.Organisation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrganisationToAdd {

    private String name;
    private ArrayList<String> linkedImagingIds = new ArrayList<>();
    private Address address;
    private String type;
    private String fax;
    private List<Contact> contacts;

}
