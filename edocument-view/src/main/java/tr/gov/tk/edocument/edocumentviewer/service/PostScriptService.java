package tr.gov.gib.evdbelge.evdbelgegoruntuleme.service;

import tr.gov.gib.tahsilat.thsexception.custom.GibBusinessException;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;

public interface PostScriptService {

    byte[] makePSFile(String session) throws GibBusinessException, GibException;

}
