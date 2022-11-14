package excelReader;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProviderUSer {

    private String fullName;
    private String firstName;
    private String lastName;
    private String middleName;
    private String primaryRoleId;
    private String practiceId;
    private String practiceName;
    private String npi;
    private String tel;
    private String status;
    private String[] linkedFacilityId;
    private String[] imagingOrgIds;
}
