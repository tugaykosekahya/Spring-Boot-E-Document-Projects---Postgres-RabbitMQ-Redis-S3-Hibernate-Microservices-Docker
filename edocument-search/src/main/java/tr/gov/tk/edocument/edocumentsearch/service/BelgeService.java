package tr.gov.gib.evdbelge.evdbelgesorgulama.service;

import tr.gov.gib.evdbelge.evdbelgesorgulama.entity.Belge;
import tr.gov.gib.tahsilat.thsbaseservice.AbstractBaseService;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

import java.util.List;

public interface BelgeService extends AbstractBaseService<Belge> {
    List belgeSorgula(String query) throws GibException;
    long nativeInsert(String query, boolean idDonulecek) throws GibException;
}
