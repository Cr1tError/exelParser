package excelReader.practiceNameUtils;

public class PracticeNameChanger {

    public static String changePractice(String str){
        String s = str;
        // no ignore case here pls.
        if(s.contains("KISHWAUKEE " )){
            return "Kishwaukee Health System";
        }
        // No ignore case here pls.
        if(s.contains("ROA ")){
            return "ROA";
        }
        // No ignore case here pls.
        if(s.contains(" ROA-ELGIN")){
            return "ROA";
        }
        // No ignore case here pls.
        if(s.contains("OSF ")){
            return "OSF Medical Group";
        }
        // No ignore case here pls.
        if(s.contains("Osf ")){
            return "OSF Medical Group";
        }
        // No ignore case here pls.
        if(s.contains("RMH ")){
            return "RMH";
        }
        // No ignore case here pls.
        if(s.contains("FHN ")){
            return "FHN Group";
        }
        // No ignore case here pls.
        if(s.contains("UIC ")) {
            return "UIC";
        }
        // No ignore case here pls.
        if(s.contains("KSB ")){
            return "KSB Medical Group";
        }
       // No ignore case here pls.
        if(s.contains("Ksb ")){
            return "KSB Medical Group";
        }
        // No ignore case here pls.
        if(s.contains("RHP")){
            return "RHP";
        }
        // No ignore case here pls.
        if(s.contains("CGH ")){
            return "CGH Medical Center";
        }
        // No ignore case here pls.
        if(s.contains("SSM ")){
            return "SSM Health";
        }
        // No ignore case here pls.
        if(s.contains("Ssm ")){
            return "SSM Health";
        }
        // No ignore case here pls.
        if(s.contains("Ssmheath ")){
            return "SSM Health";
        }


        s = s.toLowerCase();

        if(s.contains("illinois bone & joint institute")){
            return "Illinois Bone & Joint Institute";
        }
        if(s.contains("illinois bone and joint institute")){
            return "Illinois Bone & Joint Institute"; //TODO ask about this one
        }
        if(s.contains("northwestern ")){
            return "Northwestern Medicine";
        }
        if(s.contains("iu health ")){
            return "IU Health Group";
        }
        if(s.contains("mercy health")){
            return "Mercy Health";
        }
        if(s.contains("crusader ")){
            return "Crusader Community Health";
        }
        if(s.contains("swedish american ")){
            return "Swedish American MG";
        }
        if(s.contains("u w health")){
            return "UW Health";
        }
        if(s.contains("uw health")){
            return "UW Health";
        }
        if(s.contains("physicias immediate ")){
            return "Physicias Immediate Care";
        }
        if(s.contains("loyola ")){
            return "Loyola Medicine";
        }
        if(s.contains("rockford ")){
            return "Rockford Health";
        }
        if(s.contains("amita health")){
            return "Amita Health";
        }
        if(s.contains("duly health")){
            return "Duly Health and Care";
        }
        if(s.contains("advocate health")){
            return "Advocate Health Care";
        }
        if(s.contains("health first wellness center ")){
            return "Health First Wellness Center";
        }
        if(s.contains("kish ")){
            return "Kish Health System";
        }
        if(s.contains("beloit")){
            return "Beloit Health System";
        }
        if(s.contains("froedtert ")){
            return "Froedtert Health";
        }
        if(s.contains("centegra ")){
            return "Centegra Health System";
        }
        if(s.contains("ascension ")) {
            return "Ascension Medical Group";
        }
        if(s.contains("mayo ")){
            return "Mayo Clinic";
        }
        if(s.contains("premier health")){
            return "Premier Health";
        }
        if(s.contains("monroe ")){
            return "Monroe Clinic";
        }
        if(s.contains("physicians immediate care")){
            return "Physicians Immediate Care";
        }
        if(s.contains("creekside medical center")){
            return "Creekside Medical Center";
        }
        if(s.contains("orthropaedic surgery& rehab")){
            return "Orthropaedic surgery& Rehab";
        }
        if(s.contains("south barrington office center ")){
            return "South Barrington Office Center";
        }
        if(s.contains("ortho illinois")){
            return "Ortho Illinois";
        }
        if(s.contains("janesville medical center ")){
            return "Janesville Medical Center";
        }
        if(s.contains("hulsebus ")){
            return "Hulsebus";
        }
        if(s.contains("hulsebus-")){
            return "Hulsebus";
        }
        if(s.contains("alexian brothers ")){
            return "Alexian Brothers";
        }
        if(s.contains("fox valley")){
            return "Fox Valley";
        }
        return str;
    }
}
