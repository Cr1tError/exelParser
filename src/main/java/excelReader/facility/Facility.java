package excelReader.facility;

import excelReader.organisation.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Facility {
    @Id
    private Object _id;
    private String practiceId;
    private String practiceName;
    private String name;
    private String description;
    private String timeZoneId;
    private String timeZoneName;
    private Address address;
    private String tel;
    private String fax;
    private String status;


}
// adress + city + zip + state of  practice = name