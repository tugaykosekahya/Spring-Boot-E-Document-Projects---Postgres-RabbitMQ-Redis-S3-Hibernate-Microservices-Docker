package tr.gov.gib.evdbelge.evdbelgepdfimzalama.service;

import com.google.gson.JsonObject;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.entity.Belge;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.object.request.CreateBelgeInput;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

public interface PdfImzaService {
    byte[] pdfImzala(CreateBelgeInput createBelgeInput) throws GibException;
}
