package tr.gov.gib.evdbelge.evdbelgesorgulama.dao;

import com.google.gson.JsonObject;

public interface QueueDao {
    void pushPdfQueue(JsonObject var1);
    void pushHedefQueue(JsonObject var1);
    //void pushPdfImzaliQueue(JsonObject var1);
}
