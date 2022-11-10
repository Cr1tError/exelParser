package excelReader.physician;

public enum Suffix {
    MD("MD"),
    DDS("DDS"),
    PA_C("PA-C"),
    APN("APN"),
    FNP("FNP"),
    OPA_C("OPA-C"),
    NP("NP"),
    DO("DO"),
    DPM("DPM"),
    APRN("APRN"),
    CNP("CNP"),
    MMBS("MMBS"),
    FNP_C("FNP-C"),
    DC("DC"),
    DMD("DMD"),
    MPH("MPH"),
    APNP("APNP"),
    APN_CNP("APN-CNP"),
    NP_C("NP-C"),
    FNP_BC("FNP-BC"),
    ANP("ANP"),
    ARNP("ARNP"),
    WHNP("WHNP"),
    DNP("DNP"),
    APRN_NP("APRN-NP"),
    NP_BC("NP-BC"),
    PMHNP("PMHNP"),
    LPN("LPN"),
    PA("PA"),
    PAC("PAC"),
    CPN("CPN"),
    RN("RN"),
    ATC("ATC"),
    PT("PT"),
    PSYD("PSYD"),
    OD("OD"),
    DMP("DMP"),
    MA("MA"),
    OTD("OTD"),
    DR("DR"),
    AUD("AUD"),
    PHD("PHD"),
    PNP("PNP");
    final String value;
    Suffix(String value ) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
    public static boolean isExist(String s){
        for ( Suffix a : Suffix.values() ){
            if(a.getValue().equals(s)){
                return true;
            }
        }
        return false;
    }
}
//FNP,BC