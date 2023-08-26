package tr.gov.gib.evdbelge.evdbelgehazirlama.external.service;

import com.google.gson.JsonObject;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

public interface YevdoExternalService {
    JsonObject islenecekBelgeleriGetir(int limit) throws GibException;
}
