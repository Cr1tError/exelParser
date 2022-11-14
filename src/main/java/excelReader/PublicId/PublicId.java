package excelReader.PublicId;

import excelReader.organisation.Organisation;
import jdk.javadoc.doclet.Taglet;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Getter
@Setter
public class PublicId{


    private final DateFormat df = new SimpleDateFormat("yy");
    private  int count = 0;
    //        TODO check the max count before start

    private String location = "1";


    public  String generateOrganizationPubId(){
        String orgType = "C";

        String orgCount = String.valueOf(count);
        String paddedCount = StringUtils.leftPad(orgCount, 6, "0");
        String year = df.format(new Date());
        String publicIp = orgType + location + year + paddedCount;
        count++;
        return publicIp;
    }


}

