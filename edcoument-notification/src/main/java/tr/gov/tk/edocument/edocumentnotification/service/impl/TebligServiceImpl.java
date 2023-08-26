package tr.gov.gib.evdbelge.evdbelgeteblig.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ws.soap.client.core.SoapActionCallback;
import tr.gov.gib.evdbelge.evdbelgeteblig.client.codes.Deliver;
import tr.gov.gib.evdbelge.evdbelgeteblig.client.codes.DeliverResponse;
import tr.gov.gib.evdbelge.evdbelgeteblig.client.codes.ObjectFactory;
import tr.gov.gib.evdbelge.evdbelgeteblig.client.codes.TebligatEntry;
import tr.gov.gib.evdbelge.evdbelgeteblig.config.SOAPConnectorInquriy;
import tr.gov.gib.evdbelge.evdbelgeteblig.dao.BelgeDaoJpa;
import tr.gov.gib.evdbelge.evdbelgeteblig.entity.Belge;
import tr.gov.gib.evdbelge.evdbelgeteblig.entity.BelgeHedef;
import tr.gov.gib.evdbelge.evdbelgeteblig.entity.BelgeHedefSonuc;
import tr.gov.gib.evdbelge.evdbelgeteblig.message.Messages;
import tr.gov.gib.evdbelge.evdbelgeteblig.object.request.BelgeHedefDto;
import tr.gov.gib.evdbelge.evdbelgeteblig.service.BelgeHedefService;
import tr.gov.gib.evdbelge.evdbelgeteblig.service.BelgeHedefSonucService;
import tr.gov.gib.evdbelge.evdbelgeteblig.service.TebligService;
import tr.gov.gib.tahsilat.thsbaseobject.GibObjectMapper;
import tr.gov.gib.tahsilat.thsexception.custom.GibBusinessException;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;
import tr.gov.gib.tahsilat.thsutils.DateUtils;

import javax.annotation.Resource;
import javax.xml.bind.JAXBElement;
import java.util.Iterator;
import java.util.List;

@Service
public class TebligServiceImpl implements TebligService {
    private static final String BELGE_HEDEF_MAP = "belgeHedef";
    private static final Gson GSON = new Gson();
    private final BelgeHedefService belgeHedefService;
    private final BelgeHedefSonucService belgeHedefSonucService;
    private final BelgeDaoJpa belgeDaoJpa;
    private final SOAPConnectorInquriy soapConnectorInquriy;
    private final GibLogger LOGGER = GibLoggerFactory.getLogger();
    private final GibObjectMapper gibObjectMapper;

    @Value("${etebligat.server.url}")
    private String etebligatUrl;

    @Resource(name="redisTemplate")
    private HashOperations<String, String, String> hashOperations;
    @Resource(name="readOnlyRedisTemplate")
    private HashOperations<String, String, String> readOnlyHashOperations;

    public TebligServiceImpl(BelgeHedefService belgeHedefService, BelgeHedefSonucService belgeHedefSonucService, BelgeDaoJpa belgeDaoJpa, SOAPConnectorInquriy soapConnectorInquriy, GibObjectMapper gibObjectMapper) {
        this.belgeHedefService = belgeHedefService;
        this.belgeHedefSonucService = belgeHedefSonucService;
        this.belgeDaoJpa = belgeDaoJpa;
        this.soapConnectorInquriy = soapConnectorInquriy;
        this.gibObjectMapper = gibObjectMapper;
    }

    @Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRES_NEW)
    public void etebligataGonder(BelgeHedefDto belgeHedefDto, String belgeHedefKey) throws GibException {
        JsonObject json = getBelgeFromRedis(belgeHedefKey);
        if(json.size() == 0) {
            Belge belge = new Belge();
            belge.setId(belgeHedefDto.getBelgeid());
            if(belgeHedefService.belgeHedefVarMi(belge)) {
                json.addProperty("status", 200); //
                json.addProperty("optime", DateUtils.getDateTime());
                putBelgeToRedis(belgeHedefKey, json.toString());
            } else {
                LOGGER.error("Belge rediste ve vtde yok.");
                throw new GibBusinessException("Belge rediste ve vtde yok.", "");
            }
        }
        Deliver deliver = new Deliver();
        List<TebligatEntry> tebligatEntryList = deliver.getArg1();
        TebligatEntry tebligatEntry;
        JsonObject metaJson = belgeHedefDto.getMetadata();
        Iterator<String> it = metaJson.keySet().iterator();
        while (it.hasNext()) {
            tebligatEntry = new TebligatEntry();
            String key = it.next();
            tebligatEntry.setKey(key);
            tebligatEntry.setValue(metaJson.get(key).getAsString());
            tebligatEntryList.add(tebligatEntry);
        }
        /*tebligatEntry.setKey("uygulamaAdi");
        tebligatEntry.setValue("EVDO");
        tebligatEntryList.add(tebligatEntry);
        tebligatEntry.setKey("orgOid");
        tebligatEntry.setValue("00000000002703");
        tebligatEntryList.add(tebligatEntry);
        tebligatEntry.setKey("orgAdi");
        tebligatEntry.setValue("Eskişehir");
        tebligatEntryList.add(tebligatEntry);
        tebligatEntry.setKey("evrakNo");
        tebligatEntry.setValue("2022112166A0809000");
        tebligatEntryList.add(tebligatEntry);
        tebligatEntry.setKey("kaynakBelgeOid");
        tebligatEntry.setValue("0qjto26wz614sle");
        tebligatEntryList.add(tebligatEntry);
        tebligatEntry.setKey("belgeTuru");
        tebligatEntry.setValue("66");
        tebligatEntryList.add(tebligatEntry);
        tebligatEntry.setKey("tebligeHazirlayan");
        tebligatEntry.setValue("EREN GENÇ");
        tebligatEntryList.add(tebligatEntry);
        tebligatEntry.setKey("tebligeHazirlayanIp");
        tebligatEntry.setValue("DENEMEIP");
        tebligatEntryList.add(tebligatEntry);
        tebligatEntry.setKey("tebligeHazirlamaOptime");
        tebligatEntry.setValue("20221123140000");
        tebligatEntryList.add(tebligatEntry);
        tebligatEntry.setKey("vergiNo");
        tebligatEntry.setValue("0680076916");
        tebligatEntryList.add(tebligatEntry);
        tebligatEntry.setKey("tckn");
        tebligatEntry.setValue("11111111111");
        tebligatEntryList.add(tebligatEntry);
        tebligatEntry.setKey("mukellefUnvan");
        tebligatEntry.setValue("EVDO_WS TEST");
        tebligatEntryList.add(tebligatEntry);
        tebligatEntry.setKey("olusturan");
        tebligatEntry.setValue("SULEYMANB");
        tebligatEntryList.add(tebligatEntry);
        tebligatEntry.setKey("olusturmaOptime");
        tebligatEntry.setValue("20221123111608");
        tebligatEntryList.add(tebligatEntry);
        tebligatEntry.setKey("olusturanIp");
        tebligatEntry.setValue("0.6.98.107");
        tebligatEntryList.add(tebligatEntry);
        tebligatEntry.setKey("dizin");
        tebligatEntry.setValue("2022/11/23/0qlakstx4k100d/");
        tebligatEntryList.add(tebligatEntry);
        tebligatEntry.setKey("dosyaadi");
        tebligatEntry.setValue("ODEME EMRI");
        tebligatEntryList.add(tebligatEntry);
        tebligatEntry.setKey("imzalayan");
        tebligatEntry.setValue("EREN GENÇ");
        tebligatEntryList.add(tebligatEntry);
        tebligatEntry.setKey("imzalamaOptime");
        tebligatEntry.setValue("20221123150000");
        tebligatEntryList.add(tebligatEntry);
        tebligatEntry.setKey("imzalayanIp");
        tebligatEntry.setValue("10.251.55.22");
        tebligatEntryList.add(tebligatEntry);
        tebligatEntry.setKey("konu");
        tebligatEntry.setValue("EVDO_WS TEST KONU");
        tebligatEntryList.add(tebligatEntry);
        tebligatEntry.setKey("aciklama");
        tebligatEntry.setValue("EVDO_WS TEST ACIKLAMA");*/

        JAXBElement<DeliverResponse> deliverResponse;

        try {
            deliverResponse = ((JAXBElement<DeliverResponse>) soapConnectorInquriy.callWebService(etebligatUrl, new ObjectFactory().createDeliver(deliver), new SoapActionCallback("")));
        } catch (Exception e) {
            LOGGER.error("evrakNo: " + belgeHedefDto.getBelgeno() + " deliver servisinde beklenmeyen hata.", e);
            throw new GibException("evrakNo: " + belgeHedefDto.getBelgeno() + " deliver servisinde beklenmeyen hata.","");
        }
        if(deliverResponse != null && deliverResponse.getValue() == null) {
            LOGGER.info("evrakNo: " + belgeHedefDto.getBelgeno() + " sonuç null.");
            throw new GibException("evrakNo: " + belgeHedefDto.getBelgeno() + " sonuç null.","");
        }
        else if(deliverResponse == null && deliverResponse.getValue() == null) {
            LOGGER.info("evrakNo: " + belgeHedefDto.getBelgeno() + " sonuç null.");
            throw new GibException("evrakNo: " + belgeHedefDto.getBelgeno() + " sonuç null.","");
        }
        String returnSonuc = deliverResponse.getValue().getReturn();
        LOGGER.info("sonuc: " + returnSonuc);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("return", returnSonuc);
        BelgeHedefSonuc belgeHedefSonuc = new BelgeHedefSonuc();
        String sonucOid;
        if(returnSonuc.startsWith("EX")) {
            sonucOid = returnSonuc.substring(returnSonuc.indexOf("OID") + 4, returnSonuc.indexOf("OID") + 18).trim();
            belgeGuncelle(belgeHedefDto.getBelgeid(), (short) 211);
        } else {
            sonucOid = returnSonuc;
            belgeGuncelle(belgeHedefDto.getBelgeid(), (short) 210);
        }
        BelgeHedef belgeHedef = new BelgeHedef();
        belgeHedef.setId(belgeHedefDto.getBelgeHedefid());
        belgeHedefSonuc.setBelgeHedef(belgeHedef);
        belgeHedefSonuc.setSonucOid(sonucOid);
        belgeHedefSonuc.setSonuc(jsonObject);
        belgeHedefSonucService.belgeHedefSonucKaydet(belgeHedefSonuc);
        deleteBelgeFromRedis(belgeHedefKey);
    }

    public void belgeGuncelle(long belgeId, short durum) throws GibException {
        try {
            belgeDaoJpa.belgeUpdate(belgeId, durum);
        } catch (GibRemoteException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(Messages.VERI_TABANI_KAYIT_HATASI.message("belge güncellenirken hata oluştu") , e);
            throw new GibException(Messages.VERI_TABANI_KAYIT_HATASI.message("belge güncellenirken hata oluştu"), "");
        }
    }

    public void putBelgeToRedis(String key, String json) throws GibRemoteException {
        try {
            hashOperations.put(BELGE_HEDEF_MAP, key, json);
        } catch (Exception e) {
            LOGGER.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(),"");
        }
    }

    public void deleteBelgeFromRedis(String key) throws GibRemoteException {
        try {
            hashOperations.delete(BELGE_HEDEF_MAP, key);
        } catch (Exception e) {
            LOGGER.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(),"");
        }
    }

    public JsonObject getBelgeFromRedis(String key) throws GibRemoteException {
        try {
            String result = readOnlyHashOperations.get(BELGE_HEDEF_MAP, key);
            if(StringUtils.isNotBlank(result))
                return GSON.fromJson(result, JsonObject.class);
            else
                return new JsonObject();
        } catch (Exception e) {
            LOGGER.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(),"");
        }
    }
}
