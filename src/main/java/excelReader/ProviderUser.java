package excelReader;

import lombok.*;
import org.springframework.data.annotation.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProviderUser {

    @Id
    private Object _id;
    private String fullName;
    private String firstName;
    private String lastName;
    private String middleName;
    private String primaryRoleId;
    private String practiceId;
    private String practiceName;
    private String npi;
    private String suffix;
    private String tel;
    private String status;
    private String[] linkedFacilityId;
    private String[] imagingOrgIds;
}
