package tr.gov.gib.evdbelge.evdbelgeteblig.service;

import tr.gov.gib.evdbelge.evdbelgeteblig.object.request.BelgeHedefDto;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

public interface TebligService {
    void etebligataGonder(BelgeHedefDto belgeHedefDto, String belgeHedefKey) throws GibException;
}
