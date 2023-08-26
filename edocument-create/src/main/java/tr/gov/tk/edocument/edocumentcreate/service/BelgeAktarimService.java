package tr.gov.gib.evdbelge.evdbelgehazirlama.service;

import com.google.gson.JsonObject;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

public interface BelgeAktarimService {
    void kuyrukVeRediseAt(JsonObject json) throws GibException;
    String queueRequest() throws GibException;
}
