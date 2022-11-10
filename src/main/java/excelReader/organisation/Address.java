package excelReader.organisation;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Address {

    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zip;
    private String countryCode;
}
