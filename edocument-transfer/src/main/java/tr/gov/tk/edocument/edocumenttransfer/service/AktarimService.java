package tr.gov.gib.evdbelge.evdbelgeaktarma.service;

import com.google.gson.JsonObject;
import tr.gov.gib.evdbelge.evdbelgeaktarma.entity.Belge;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

public interface AktarimService {
    void belgeAktar(JsonObject jsonObject, Belge belge, String belgeIslemeKey) throws GibException;
    void islenmisBelgeBildir(Belge belge, String belgeIslemeKey) throws GibException;
}
