package excelReader.physician;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.lang.annotation.Documented;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Physician {
    String name;
    String surname;
    String middleName;

    String suffix;
    String practiceName;
    String address;
    String address2;
    String city;
    String state;
    String zip;
    String telephone;
    String fax;
    String npi;



    @Override
    public String toString() {
        return "Physician{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", middleName='" + middleName + '\'' +
                ", suffix='" + suffix + '\'' +
                ", practiceName='" + practiceName + '\'' +
                ", address='" + address + '\'' +
                ", address2='" + address2 + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zip='" + zip + '\'' +
                ", telephone='" + telephone + '\'' +
                ", fax='" + fax + '\'' +
                ", npi='" + npi + '\'' +
                '}';
    }
}
