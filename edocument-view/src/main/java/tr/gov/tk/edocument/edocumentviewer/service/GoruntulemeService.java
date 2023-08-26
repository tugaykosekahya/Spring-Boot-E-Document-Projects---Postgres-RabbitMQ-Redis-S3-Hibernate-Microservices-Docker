package tr.gov.gib.evdbelge.evdbelgegoruntuleme.service;


import com.lowagie.text.DocumentException;
import net.sf.jasperreports.engine.JRException;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.object.ReportJson;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.object.request.CreateBelgeInput;
import tr.gov.gib.tahsilat.thsexception.custom.GibRemoteException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

public interface GoruntulemeService {
    byte[] pdfGoruntule(String session, HttpServletResponse response);

    String createFileNamePdf(String key);

    String createFileNameSignedPdf(String key);

    void sessionControl(String key) throws GibRemoteException;

    String createBucketName();

    String createKey(CreateBelgeInput createBelgeInput);

    byte[] belgeBul(CreateBelgeInput createBelgeInput, String key);

    byte[] exportReport(String key) throws GibRemoteException, JRException;

    byte[] exportReport(CreateBelgeInput createBelgeInput, ReportJson reportJson) throws JRException, GibRemoteException, IOException, DocumentException;

    byte[] readByte(InputStream inputStream) throws IOException;

    byte[] pdfOlustur(CreateBelgeInput body);

    CreateBelgeInput convertKeyToCreateBelgeInput(String key);
}
