package tr.gov.gib.evdbelge.evdbelgehazirlama.dao;

import com.google.gson.JsonObject;

public interface QueueDao {
    void pushAktarimQueue(JsonObject var1);
}
