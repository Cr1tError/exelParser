package excelReader.toAdd;

import excelReader.organisation.Address;
import excelReader.organisation.Organisation;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    private String publicId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrganisationToAdd that = (OrganisationToAdd) o;
        return getName().trim().equalsIgnoreCase(that.getName().trim()) && getAddress().equals(that.getAddress());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getAddress());
    }

    @Override
    public String toString() {
        return "OrganisationToAdd{" +
                "name='" + name + '\'' +
                ", linkedImagingIds=" + linkedImagingIds.toString() +
                ", address=" + address.getAddress1() +
                ", type='" + type + '\'' +
                ", fax='" + fax + '\'' +
                '}';
    }
}
