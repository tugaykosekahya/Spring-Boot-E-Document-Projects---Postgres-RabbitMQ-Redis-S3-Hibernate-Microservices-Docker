package tr.gov.gib.evdbelge.evdbelgegoruntuleme.message;

public enum Messages {
    HATA("Bir hata oluştu. Hata detayı: %s"),
    SISTEM_HATASI("Bir sistem hatası oluştu."),
    BEKLENMEYEN_HATA("Beklenmeyen bir hata oluştu."),
    BEKLENMEYEN_HATA_DETAYLI("Beklenmeyen bir hata oluştu. Hata detayı: %s"),
    VERI_TABANI_KAYIT_HATASI("Veri tabanına kaydederken hata oluştu. Hata detayı: %s"),
    VERI_TABANI_HATASI("Veri tabanına hatası."),
    PARAMETRE_DONUSUM_HATASI("Parametre dönüşümünde hata oluştu."),
    REDIS_ERISIM_HATASI("Redise erişimde bir hata oluştu."),
    S3_HATA("S3 object storage okuma/yazma sirasinda hata oluştu. Hata detayı: %s"),
    REDIS_KEY_HATASI("%s anahtarı bulunamadı."),
    RESP_OBJ_HATA("Sonuç objesi oluştururken hata oluştu. Hata detayı: %s"),

    BAKIM_MESAJ("Sistemlerde yapılan bakım çalışması nedeniyle geçici olarak hizmet verilemiyor. Lütfen daha sonra tekrar deneyeniz."),
    YOGUNLUK_MESAJ("Sistemlerde yaşanan yoğunluk nedeniyle geçici olarak hizmet verilemiyor. Lütfen daha sonra tekrar deneyeniz."),
    REDIS_SESSION_BULUNAMADI("Rediste session bilgisi bulunamadı. Sessionı kontrol ediniz."),
    TASLAK_EKLENEMEDI("Pdfe taslak eklenirken hata oluştu."),
    ;

    private final String message;

    Messages(String message) {
        this.message = message;
    }

    public String message() {
        return this.message;
    }

    public String message(final Object... o) {
        return String.format(message(), o);
    }
}
