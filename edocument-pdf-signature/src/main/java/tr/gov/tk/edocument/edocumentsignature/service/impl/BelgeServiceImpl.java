package tr.gov.gib.evdbelge.evdbelgepdfimzalama.service.impl;

import org.springframework.stereotype.Service;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.dao.BelgeDao;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.entity.Belge;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.entity.BelgeAkis;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.message.Messages;
import tr.gov.gib.evdbelge.evdbelgepdfimzalama.service.BelgeService;
import tr.gov.gib.tahsilat.thsbasedao.GibJPABaseDao;
import tr.gov.gib.tahsilat.thsbaseobject.GibObjectMapper;
import tr.gov.gib.tahsilat.thsbaseservice.impl.AbstractJPABaseServiceImpl;
import tr.gov.gib.tahsilat.thsexception.custom.GibBusinessException;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;

import java.util.ArrayList;
import java.util.Collections;

@Service
public class BelgeServiceImpl extends AbstractJPABaseServiceImpl<Belge> implements BelgeService {
    private final BelgeDao belgeDao;
    private final GibObjectMapper gibObjectMapper;
    private final GibLogger LOGGER = GibLoggerFactory.getLogger();

    public BelgeServiceImpl(BelgeDao belgeDao, GibObjectMapper gibObjectMapper) {
        this.belgeDao = belgeDao;
        this.gibObjectMapper = gibObjectMapper;
    }

    @Override
    protected GibJPABaseDao getDao() {
        return belgeDao;
    }

    @Override
    protected void preInsertValid(Belge entity) throws GibBusinessException {

    }

    @Override
    protected void preUpdateValid(Belge entity) throws GibBusinessException {

    }

    @Override
    protected void preDeleteValid(Belge entity) throws GibBusinessException {

    }

    @Override
    protected void validate(Belge entity) throws GibBusinessException {

    }

    @Override
    public void belgeKaydet(Belge belge) throws GibException {
        try {
            BelgeAkis belgeAkis = new BelgeAkis();
            belgeAkis.setKullaniciKodu(belge.getKullanicikodu());
            belgeAkis.setNextnodeuserid(belge.getNextnodeuserid());
            belgeAkis.setBelge(belge);
            belge.setBelgeAkislar(new ArrayList<>(Collections.singletonList(belgeAkis)));
            belgeDao.save(belge);
        } catch (Exception e) {
            String data;
            data = gibObjectMapper.toJsonString(belge);
            LOGGER.error(Messages.VERI_TABANI_KAYIT_HATASI.message(belge.getBelgeno() + ", " + e.getMessage() + "\n" + data) , e);
            throw new GibException(Messages.BEKLENMEYEN_HATA.message(), "");
        }
    }

    @Override
    public boolean belgeVarMi(String belgeno, String orgoid, int belgeturu) throws GibException {
        try {
            return belgeDao.existsBelgeByBelgenoAndOrgoidAndAndBelgeturu(belgeno, orgoid, belgeturu);
        } catch (Exception e) {
            LOGGER.error(Messages.VERI_TABANI_KAYIT_HATASI.message(belgeno + ", " + orgoid + ", " + belgeturu + ", " + e.getMessage() + "\n") , e);
            throw new GibException(Messages.BEKLENMEYEN_HATA.message(), "");
        }
    }
}
