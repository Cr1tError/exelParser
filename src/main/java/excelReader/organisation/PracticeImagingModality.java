package excelReader.organisation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PracticeImagingModality {
    @Field("id")
    private String id;

    private String AET;
    private String ip;
    private Integer port;
    private String type;
    private String route;
    private String filterString;
    private String rate;
    private Date dateFrom;
    private Date dateTo;

    private TitleType titleType;
    private String facilityId;

    public enum TitleType {
        LOCATION,
        IMAGING_CENTER;
    }
}
