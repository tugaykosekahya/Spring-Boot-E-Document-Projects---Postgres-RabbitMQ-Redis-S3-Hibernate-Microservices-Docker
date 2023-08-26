package tr.gov.gib.evdbelge.evdbelgesorgulama.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tr.gov.gib.evdbelge.evdbelgesorgulama.dao.BelgeAkisDao;
import tr.gov.gib.evdbelge.evdbelgesorgulama.dao.BelgeHedefDao;
import tr.gov.gib.evdbelge.evdbelgesorgulama.dao.QueueDao;
import tr.gov.gib.evdbelge.evdbelgesorgulama.dao.impl.BelgeDaoJpaImpl;
import tr.gov.gib.evdbelge.evdbelgesorgulama.entity.Belge;
import tr.gov.gib.evdbelge.evdbelgesorgulama.entity.BelgeAkis;
import tr.gov.gib.evdbelge.evdbelgesorgulama.entity.BelgeHedef;
import tr.gov.gib.evdbelge.evdbelgesorgulama.message.Messages;
import tr.gov.gib.evdbelge.evdbelgesorgulama.object.request.CreateBelgeAkisGuncelle;
import tr.gov.gib.evdbelge.evdbelgesorgulama.service.BelgeAkisService;
import tr.gov.gib.tahsilat.thsbasedao.GibJPABaseDao;
import tr.gov.gib.tahsilat.thsbaseservice.impl.AbstractJPABaseServiceImpl;
import tr.gov.gib.tahsilat.thsexception.custom.GibBusinessException;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;
import tr.gov.gib.tahsilat.thsexception.custom.GibValidationException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;
import tr.gov.gib.tahsilat.thsutils.DateUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;

@Component
public class BelgeAkisServiceImpl extends AbstractJPABaseServiceImpl<BelgeAkis> implements BelgeAkisService {
    private static final String BELGE_PDF_MAP = "belgePdf";
    private static final String BELGE_HEDEF_MAP = "belgeHedef";
    private static final Gson GSON = new Gson();
    private final BelgeAkisDao belgeAkisDao;
    private final BelgeHedefDao belgeHedefDao;
    private final BelgeServiceImpl belgeService;
    private final BelgeDaoJpaImpl belgeDaoJpa;
    private final QueueDao queueDao;
    private final GibLogger LOGGER = GibLoggerFactory.getLogger();

    @Resource(name="redisTemplate")
    private HashOperations<String, String, String> hashOperations;
    @Resource(name="readOnlyRedisTemplate")
    private HashOperations<String, String, String> readOnlyHashOperations;

    public BelgeAkisServiceImpl(BelgeAkisDao belgeAkisDao, BelgeHedefDao belgeHedefDao, BelgeServiceImpl belgeService, BelgeDaoJpaImpl belgeDaoJpa, QueueDao queueDao) {
        this.belgeAkisDao = belgeAkisDao;
        this.belgeHedefDao = belgeHedefDao;
        this.belgeService = belgeService;
        this.belgeDaoJpa = belgeDaoJpa;
        this.queueDao = queueDao;
    }

    @Override
    protected GibJPABaseDao getDao() {
        return belgeAkisDao;
    }

    @Override
    protected void preInsertValid(BelgeAkis entity) throws GibBusinessException {

    }

    @Override
    protected void preUpdateValid(BelgeAkis entity) throws GibBusinessException {

    }

    @Override
    protected void preDeleteValid(BelgeAkis entity) throws GibBusinessException {

    }

    @Override
    protected void validate(BelgeAkis entity) throws GibBusinessException {

    }

    @Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRES_NEW)
    public void akisGuncelle(CreateBelgeAkisGuncelle createBelgeAkisGuncelle) throws GibException {
        if(createBelgeAkisGuncelle.getDurum() == 200 && createBelgeAkisGuncelle.getHedef() == 1 && createBelgeAkisGuncelle.getMetadata() == null) {
            throw new GibValidationException("", "e-Tebligat hedefi için metadata boş olamaz.");
        }
        BelgeAkis belgeAkis = new BelgeAkis();
        belgeAkis.setKullaniciKodu(createBelgeAkisGuncelle.getKullaniciKodu());
        belgeAkis.setNextnodeuserid(createBelgeAkisGuncelle.getUsernodeid());
        belgeAkis.setUserip(createBelgeAkisGuncelle.getUserip());
        belgeAkis.setAciklama(createBelgeAkisGuncelle.getAciklama());
        Belge b = (Belge) belgeService.getDao().getReferenceById(createBelgeAkisGuncelle.getBelgeid());
        /*Belge b = new Belge();
        b.setId(1);*/
        belgeAkis.setBelge(b);
        akisKaydet(belgeAkis);
        belgeGuncelle(createBelgeAkisGuncelle);
        if(createBelgeAkisGuncelle.getDurum() == 100) {//Onaylandi. bundan sonra imzasiz pdf olusacak 101 olacak. Sonra imzali pdf olusacak 102 olacak ve akis tamamlanacak.
            kuyrukVeRediseAt(createBelgeAkisGuncelle);
        }
        if(createBelgeAkisGuncelle.getDurum() == 200) {//Imzalandi. hedef belirlenecek. etebligata gidebilir, yazdirilabilir, eptt ye gidebilir.
            BelgeHedef belgeHedef = new BelgeHedef();
            belgeHedef.setBelge(b);
            belgeHedef.setHedef(createBelgeAkisGuncelle.getHedef());
            belgeHedef.setBelgeAkis(belgeAkis);
            if(createBelgeAkisGuncelle.getHedef() == 1) {
                belgeHedef.setMetadata(GSON.fromJson(GSON.toJson(createBelgeAkisGuncelle.getMetadata()), JsonObject.class));
            }
            long belgeHedefId = belgeHedefDao.save(belgeHedef).getId();
            createBelgeAkisGuncelle.setBelgeHedefId(belgeHedefId);
            belgeAkis.setBelgeHedefler(new ArrayList<>(Collections.singletonList(belgeHedef)));
            if(createBelgeAkisGuncelle.getHedef() == 1) {
                kuyrukVeRediseAt(createBelgeAkisGuncelle);
            }
        }
    }

    public void akisKaydet(BelgeAkis belgeAkis) throws GibException {
        try {
            save(belgeAkis);
        } catch (Exception e) {
            LOGGER.error(Messages.VERI_TABANI_KAYIT_HATASI.message("belge_akis kaydedilirken hata oluştu") , e);
            throw new GibException(Messages.VERI_TABANI_KAYIT_HATASI.message("belge_akis kaydedilirken hata oluştu"), "");
        }
    }

    public void belgeGuncelle(CreateBelgeAkisGuncelle createBelgeAkisGuncelle) throws GibException {
        try {
            belgeDaoJpa.belgeUpdate(createBelgeAkisGuncelle.getBelgeid(), createBelgeAkisGuncelle.getDurum(), createBelgeAkisGuncelle.getUsernodeid());
        } catch (GibRemoteException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error(Messages.VERI_TABANI_KAYIT_HATASI.message("belge güncellenirken hata oluştu") , e);
            throw new GibException(Messages.VERI_TABANI_KAYIT_HATASI.message("belge güncellenirken hata oluştu"), "");
        }
    }

    public void kuyrukVeRediseAt(CreateBelgeAkisGuncelle createBelgeAkisGuncelle) throws GibException {
        String key = createBelgeAkisGuncelle.getOrgoid() + ";" + createBelgeAkisGuncelle.getBelgeturu() + ";" + createBelgeAkisGuncelle.getBelgeno();

        if(createBelgeAkisGuncelle.getDurum() == 100) {
            if(hasBelgeInRedis(BELGE_PDF_MAP, key))
                throw new GibException("Belge pdf kuyruğunda", "");
            pushPdfQueue(createBelgeAkisGuncelle);
            JsonObject jsonRedis = new JsonObject();
            jsonRedis.addProperty("durum", createBelgeAkisGuncelle.getDurum());
            jsonRedis.addProperty("optime", DateUtils.getDateTime());
            putBelgeToRedis(BELGE_PDF_MAP, key, jsonRedis.toString());
        } else {
            if(hasBelgeInRedis(BELGE_HEDEF_MAP, key))
                throw new GibException("Belge hedef kuyruğunda", "");
            pushHedefQueue(createBelgeAkisGuncelle);
            JsonObject jsonRedis = new JsonObject();
            jsonRedis.addProperty("durum", createBelgeAkisGuncelle.getDurum());
            jsonRedis.addProperty("optime", DateUtils.getDateTime());
            putBelgeToRedis(BELGE_HEDEF_MAP, key, jsonRedis.toString());
        }
    }

    public boolean hasBelgeInRedis(String map, String key) throws GibRemoteException {
        try {
            return readOnlyHashOperations.hasKey(map, key);
        } catch (Exception e) {
            LOGGER.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(e.getMessage()),"");
        }
    }

    public void putBelgeToRedis(String map, String key, String json) throws GibRemoteException {
        try {
            hashOperations.put(map, key, json);
        } catch (Exception e) {
            LOGGER.error(Messages.REDIS_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.REDIS_ERISIM_HATASI.message(e.getMessage()),"");
        }
    }

    public void pushPdfQueue(CreateBelgeAkisGuncelle createBelgeAkisGuncelle) throws GibRemoteException {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("belgeid",createBelgeAkisGuncelle.getBelgeid());
            json.addProperty("orgoid",createBelgeAkisGuncelle.getOrgoid());
            json.addProperty("belgeturu",createBelgeAkisGuncelle.getBelgeturu());
            json.addProperty("belgeno",createBelgeAkisGuncelle.getBelgeno());
            json.addProperty("memur",createBelgeAkisGuncelle.getMemur());
            json.addProperty("sef",createBelgeAkisGuncelle.getSef());
            json.addProperty("muduryrd",createBelgeAkisGuncelle.getMuduryrd());
            json.addProperty("mudur",createBelgeAkisGuncelle.getMudur());
            queueDao.pushPdfQueue(json);
        } catch (Exception e) {
            LOGGER.error(Messages.KUYRUK_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.KUYRUK_ERISIM_HATASI.message(e.getMessage()),"");
        }
    }

    public void pushHedefQueue(CreateBelgeAkisGuncelle createBelgeAkisGuncelle) throws GibRemoteException {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("belgeHedefid", createBelgeAkisGuncelle.getBelgeHedefId());
            json.addProperty("belgeid", createBelgeAkisGuncelle.getBelgeid());
            json.addProperty("orgoid", createBelgeAkisGuncelle.getOrgoid());
            json.addProperty("belgeturu", createBelgeAkisGuncelle.getBelgeturu());
            json.addProperty("belgeno", createBelgeAkisGuncelle.getBelgeno());
            json.add("metadata", GSON.fromJson(GSON.toJson(createBelgeAkisGuncelle.getMetadata()), JsonObject.class));
            queueDao.pushHedefQueue(json);
        } catch (Exception e) {
            LOGGER.error(Messages.KUYRUK_ERISIM_HATASI.message(), e);
            throw new GibRemoteException(Messages.KUYRUK_ERISIM_HATASI.message(e.getMessage()),"");
        }
    }
}
