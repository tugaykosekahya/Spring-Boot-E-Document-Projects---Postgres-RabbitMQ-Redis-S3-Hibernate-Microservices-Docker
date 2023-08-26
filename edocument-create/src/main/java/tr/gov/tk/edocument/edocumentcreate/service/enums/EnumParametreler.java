package tr.gov.gib.evdbelge.evdbelgehazirlama.service.enums;

public enum EnumParametreler {

    BAKIM("", "bakim"), // BAKIM_YOK = "0"; BAKIM_VAR = "1"; YOGUNLUK_VAR = "2";
    BELGE_OKUMA_LIMIT("belge_okuma_limit"),
    THREAD_SIZE("thread_size"),
    ;

    private final String appName;
    private final String parametre;

    EnumParametreler(String appName, String parametre) {
        this.appName = appName;
        this.parametre = parametre;
    }

    EnumParametreler(String parametre) {
        this.parametre = parametre;
        appName = null;
    }

    public String parametre() {
        if(appName != null)
            return appname + "/" + parametre;
        else
            return this.parametre;
    }

    public static void setAppName(String appName) {
        appname = appName;
    }

    private static String appname;

    /*public String parametre(final Object... o) {
        return String.format(parametre(), o);
    }*/
}

