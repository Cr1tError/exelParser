package excelReader.organisation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;


import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Organisation {

    public enum Type {
        practice,
        lab,
        imaging,
        therapy,
        hospital,
        senior_care,
        teleradiology
    };

    @Id
    private Object _id;
    private String publicId;
    private String name;

    @Accessors(fluent = true)
    private boolean skipQA = false;

    private List<PracticeImagingModality> modalities;
    private PaymentType paymentType;

    private String type;

    private ArrayList<String> linkedImagingIds = new ArrayList<>();
    private List<String> linkedTeleradIds = new ArrayList<>();

    private Address address;

    private String orthancId;

    private String abbr;
    private String npi;
    private String einTin;
    private String medicaid;
    private String medicare;
    private String upin;
    private String tel;
    private String email;
    private String fax;
    private String sop;
    private Type types;

    private PracticeNetworkGroupName.PartOfType partOfType;
    private String partOfName;

    // Organisation homepage
    private String url;
    private String facebook;
    private String twitter;
    private String googleplus;
    private String yelp;

    private Boolean onlineBooking;
    private String logoPos;
    private Status status;
    private List<Contact> contacts;

//  public enum PartOfType {
//    NETWORK,
//    GROUP
//  }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Contact {
        private String purpose;
        private String name;
        private String tel;
        private String fax;
        private String email;
        private Boolean marketingContact;
    }


}
