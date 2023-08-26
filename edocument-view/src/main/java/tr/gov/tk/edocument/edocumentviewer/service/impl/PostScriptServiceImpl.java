package tr.gov.gib.evdbelge.evdbelgegoruntuleme.service.impl;

import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.printing.PDFPrintable;
import org.apache.pdfbox.printing.Scaling;
import org.springframework.stereotype.Service;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.object.request.CreateBelgeInput;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.service.GoruntulemeService;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.service.PostScriptService;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.service.RedisService;
import tr.gov.gib.evdbelge.evdbelgegoruntuleme.tripledes.TripleDes;
import tr.gov.gib.tahsilat.thsexception.custom.GibBusinessException;
import tr.gov.gib.tahsilat.thsexception.custom.GibException;
import tr.gov.gib.tahsilat.thslogging.GibLogger;
import tr.gov.gib.tahsilat.thslogging.GibLoggerFactory;

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import java.io.*;

@Service
public class PostScriptServiceImpl implements PostScriptService {
    private final GibLogger logger = GibLoggerFactory.getLogger();
    private final TripleDes tripleDes;
    private final GoruntulemeService goruntulemeService;
    private final RedisService redisService;

    public PostScriptServiceImpl(TripleDes tripleDes, GoruntulemeService goruntulemeService, RedisService redisService) {
        this.tripleDes = tripleDes;
        this.goruntulemeService = goruntulemeService;
        this.redisService = redisService;
    }

    public byte[] makePSFile(String session) throws GibException {
        byte[] psByteArray;
        File pdfFile = null;
        File psFile = null;
        try {
            String key = tripleDes.decrypt(session);
            goruntulemeService.sessionControl(key);
            CreateBelgeInput createBelgeInput = goruntulemeService.convertKeyToCreateBelgeInput(key);
            byte[] report = goruntulemeService.belgeBul(createBelgeInput, goruntulemeService.createFileNameSignedPdf(key));
            if (report == null)
                throw new GibBusinessException("İmzalanmamış belgenin PostScript dönüşümünü yapamazsınız.", null);

            report = goruntulemeService.exportReport(key);
            redisService.deleteSessionFromRedis(key);

//            FileOutputStream output = new FileOutputStream(new File("src/main/resources/tempDocuments/odemeEmriSablon.pdf"));
//            IOUtils.write(report, output);
//            pdfFile = new File("src/main/resources/tempDocuments/" + goruntulemeService.createFileNameSignedPdf(key));
            pdfFile = File.createTempFile(key,".pdf");
            try (FileOutputStream outputStream = new FileOutputStream(pdfFile)) {
                outputStream.write(report);
            }

            DocFlavor flavor = DocFlavor.SERVICE_FORMATTED.PRINTABLE;
            String psMimeType = DocFlavor.BYTE_ARRAY.POSTSCRIPT.getMimeType();
            StreamPrintServiceFactory[] factories = StreamPrintServiceFactory.lookupStreamPrintServiceFactories(flavor, psMimeType);

//            FileOutputStream outStream = new FileOutputStream("src/main/resources/tempDocuments/odemeEmriSablon.ps");

            psFile = File.createTempFile(key,".ps");
//            FileOutputStream outStream = new FileOutputStream("src/main/resources/tempDocuments/" + createFileNamePs(key));
            FileOutputStream outStream = new FileOutputStream(psFile);

            StreamPrintService printService = factories[0].getPrintService(outStream);

            PrintRequestAttributeSet aset = new HashPrintRequestAttributeSet();
            aset.add(MediaSizeName.NA_LETTER);

//            PDDocument doc = PDDocument.load(new File("src/main/resources/tempDocuments/odemeEmriSablon.pdf"));
            PDDocument doc = PDDocument.load(pdfFile);
            SimpleDoc pdfDoc = new SimpleDoc(new PDFPrintable(doc, Scaling.SCALE_TO_FIT, false), flavor, null);

            DocPrintJob newJob = printService.createPrintJob();
            newJob.print(pdfDoc, aset);
            doc.close();

//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            try(FileOutputStream outputStream = new FileOutputStream(psFile)) {
//                byteArrayOutputStream.writeTo(outputStream);
//            }
//
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            doc.save(byteArrayOutputStream);

            outStream.close();

//            psFile = new File("src/main/resources/tempDocuments/" + createFileNamePs(key));
            FileInputStream fileInputStream = new FileInputStream(psFile);

            psByteArray = goruntulemeService.readByte(fileInputStream);
            fileInputStream.close();
            //Localde yazdırma işlemini denemek için kullanılır. Networkde bağlı olan yazıcı adı serviceName a yazılarak yazıcı bulunur sonra da çıktı alınır.
//            PrintService myPrinter = null;
//            PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
//            for (int i = 0; i < services.length; i++) {
//                String svcName = services[i].toString();
//                if (svcName.toUpperCase().contains("ET0021B70E905F")) {
//                    myPrinter = services[i];
//                    logger.info("Printer selected: " + svcName);
//                    break;
//                }
//            }
//
//            DocPrintJob newJob2 = myPrinter.createPrintJob();
//            newJob2.print(pdfDoc, aset);

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw new GibBusinessException(ex.getMessage(), null);
        } finally {
            if (pdfFile != null && pdfFile.exists()) {
                pdfFile.delete();
            }
            if (psFile != null && psFile.exists()) {
                psFile.delete();
            }
        }

        return psByteArray;
    }

    public String createFileNamePs(String key) {
        return key + ".ps";
    }

}
