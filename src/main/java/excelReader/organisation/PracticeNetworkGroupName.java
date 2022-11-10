package excelReader.organisation;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PracticeNetworkGroupName {
    public static enum PartOfType {
        NETWORK,
        GROUP
    };

    @Id
    private String id;
    private String name;
    private PartOfType partOfType;
}