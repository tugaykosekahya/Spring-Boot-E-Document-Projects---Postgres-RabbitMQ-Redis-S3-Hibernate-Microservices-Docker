package tr.gov.gib.evdbelge.evdbelgesorgulama.object.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tr.gov.gib.tahsilat.thsbaseobject.BaseDTO;
import tr.gov.gib.tahsilat.thsvalidator.annotations.GibValidator;
import tr.gov.gib.tahsilat.thsvalidator.annotations.Required;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@GibValidator
@Schema(name = "BelgeAkisGuncelle")
public class CreateBelgeAkisGuncelle extends BaseDTO {
    @Schema(description = "Belge id bilgisi", example = "2256790")
    @Required
    private long belgeid;
    @Schema(description = "Vergi dairesi orgoid bilgisi", example = "00000000000864")
    @Required
    private String orgoid;
    @Schema(description = "Belge türü bilgisi", example = "66")
    @Required
    private short belgeturu;
    @Schema(description = "Belge numarası bilgisi", example = "2022010101J3B0000005")
    @Required
    private String belgeno;
    @Schema(description = "İşlem yapan kullanıcı kodu bilgisi", example = "123")
    @Required
    private String kullaniciKodu;
    @Schema(description = "İşlem yapan kullanıcı nodeid bilgisi", example = "112233")
    @Required
    private String usernodeid;
    @Schema(description = "İşlem yapan kullanıcı ip bilgisi", example = "10.251.55.21")
    @Required
    private String userip;
    @Schema(description = "İşlem açıklama bilgisi", example = "sevk")
    private String aciklama;
    @Schema(description = "Belge durum bilgisi", example = "1")
    @Required
    private short durum;

    @Schema(description = """
            Yapılacak hedef işlem bilgisi.
            1 : etebligat 2 : Yazdırma""", example = "1")
    private short hedef;
    @Schema(description = "Metadata bilgisi", example = """
            {
                "uygulamaAdi": "EVDO",
                "orgOid": "00000000002703",
                "orgAdi": "ESKİŞEHİR",
                "evrakNo": "2022112166A0809000",
                "kaynakBelgeOid": "0qjto26wz614sle",
                "belgeTuru": "66",
                "belgeTuruAciklama": "Ödeme Emri",
                "tebligeHazirlayan": "EREN GENÇ",
                "tebligeHazirlayanIp": "1.1.1.1",
                "tebligeHazirlamaOptime": "20221123140000",
                "vergiNo": "0680076916",
                "tckn": "11111111111",
                "mukellefUnvan": "EVDO_WS TEST",
                "olusturan": "SULEYMANB",
                "olusturmaOptime": "20221123111608",
                "olusturanIp": "0.6.98.107",
                "dizin": "00000000002703_66_2022112166A0809000",
                "dosyaadi": "ODEME EMRI",
                "imzalayan": "EREN GENÇ",
                "imzalamaOptime": "20221123150000",
                "imzalayanIp": "10.251.55.22",
                "konu": "EVDO_WS TEST KONU",
                "aciklama": "EVDO_WS TEST ACIKLAMA"
            }""")
    private Map<String, String> metadata;

    @Schema(description = "Memur (düzenleyen) bilgisi", example = "TEST TEST1")
    private String memur;
    @Schema(description = "Şef bilgisi", example = "TEST TEST2")
    private String sef;
    @Schema(description = "Müdür yardımcısı bilgisi", example = "TEST TEST3")
    private String muduryrd;
    @Schema(description = "Müdür (onaylayan) bilgisi", example = "TEST TEST4")
    private String mudur;

    @Hidden
    @JsonIgnore
    private long belgeHedefId;
}
