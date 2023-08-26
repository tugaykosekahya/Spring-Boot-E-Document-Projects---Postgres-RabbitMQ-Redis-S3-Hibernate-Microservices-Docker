package tr.gov.gib.evdbelge.evdbelgepdfolusturma.external.service;

import com.google.gson.JsonObject;
import tr.gov.gib.evdbelge.evdbelgepdfolusturma.entity.Belge;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

public interface YevdoExternalService {
    JsonObject islenmisBelgeBildir(Belge belge) throws GibException;
}
