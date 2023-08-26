package tr.gov.gib.evdbelge.evdbelgeaktarma.external.service;

import com.google.gson.JsonObject;
import tr.gov.gib.evdbelge.evdbelgeaktarma.entity.Belge;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

public interface YevdoExternalService {
    JsonObject islenmisBelgeBildir(Belge belge) throws GibException;
}
