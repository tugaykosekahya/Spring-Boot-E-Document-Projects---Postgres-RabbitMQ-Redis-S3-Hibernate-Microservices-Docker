package tr.gov.gib.evdbelge.evdbelgepdfolusturma.service;

import com.google.gson.JsonObject;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.entity.Belge;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.object.request.CreateBelgeInput;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

public interface PdfService {
    byte[] pdfOlustur(CreateBelgeInput createBelgeInput) throws GibException;
}
