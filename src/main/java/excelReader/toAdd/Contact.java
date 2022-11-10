package excelReader.toAdd;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Contact {
    private String purpose;
    private String name;
    private String tel;
    private String fax;
    private String email;
    private Boolean marketingContact;
}
