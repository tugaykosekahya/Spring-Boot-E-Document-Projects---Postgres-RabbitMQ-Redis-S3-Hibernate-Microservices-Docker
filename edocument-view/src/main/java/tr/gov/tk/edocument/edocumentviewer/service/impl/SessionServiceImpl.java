package tr.gov.gib.evdbelge.evdbelgegoruntuleme.service.impl;

import co.elastic.apm.api.CaptureSpan;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.object.request.CreateSession;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.service.RedisService;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.service.SessionService;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.tripledes.TripleDes;
import tr.gov.gib.tahsilat.thsutils.ObjectUtils;

@Service
public class SessionServiceImpl implements SessionService {
    @Value("${objectstorage.s3FileSeperator}")
    public String S3_FILE_SEPERATOR;

    private final RedisService redisService;
    private final TripleDes tripleDes;

    public SessionServiceImpl(RedisService redisService, TripleDes tripleDes) {
        this.redisService = redisService;
        this.tripleDes = tripleDes;
    }

    @Override
    @CaptureSpan(value = "createSession", type = "createSession")
    public String createSession(CreateSession body) {
        String session = null;
        String key = getKey(body);
        try {
            session = tripleDes.encrypt(key);
            redisService.putSessionToRedis(key, session);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return session;
    }

    private String getKey(CreateSession body) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(body.getOrgOid() + S3_FILE_SEPERATOR + body.getBelgeTuru() + S3_FILE_SEPERATOR + body.getBelgeNo() + S3_FILE_SEPERATOR);
        stringBuilder.append(getKeyValue(body.getTaslak()) + S3_FILE_SEPERATOR);
        stringBuilder.append(getKeyValue(body.getMemur()) + S3_FILE_SEPERATOR);
        stringBuilder.append(getKeyValue(body.getSef()) + S3_FILE_SEPERATOR);
        stringBuilder.append(getKeyValue(body.getMuduryrd()) + S3_FILE_SEPERATOR);
        stringBuilder.append(getKeyValue(body.getMudur()));

        return stringBuilder.toString();
    }

    private String getKeyValue(String deger){
       return deger != null && !deger.isEmpty() ? deger : "NULL";
    }

}
