package fr.esprit.usermanagement.docx;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class DocService {
    private  static  final  String basePathXML = "/home/yassine/IdeaProjects/user-management/src/main/resources/document.xml";
    private  static  final  String basePath = "/home/yassine/IdeaProjects/user-management/src/main/resources/";
   public static void createXml(){

        String docxFilePath = "/home/yassine/IdeaProjects/user-management/src/main/resources/attestation.docx";
        String outputXmlFilePath = basePathXML;

        try {
            extractDocumentXml(docxFilePath, outputXmlFilePath);
            System.out.println("document.xml file extracted successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            throw new RuntimeException(e);
        }
    }
    private static void extractDocumentXml(String docxFilePath, String outputXmlFilePath) throws IOException, TemplateException {
        byte[] buffer = new byte[1024];

        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(docxFilePath))) {
            ZipEntry entry = zipInputStream.getNextEntry();

            while (entry != null) {
                String entryName = entry.getName();

                if (entryName.equals("word/document.xml")) {
                    Files.createDirectories(Paths.get(outputXmlFilePath).getParent());
                    try (FileOutputStream outputStream = new FileOutputStream(outputXmlFilePath)) {
                        int len;
                        while ((len = zipInputStream.read(buffer)) > 0) {
                            outputStream.write(buffer, 0, len);
                        }
                    }
                    break;
                }

                entry =zipInputStream.getNextEntry();
            }
        }
    }

    public static  void makeWord() throws Exception{
        Configuration configuration = new Configuration();
        String fileDirectory = basePath;
        configuration.setDirectoryForTemplateLoading(new File(fileDirectory));
        Template template = configuration.getTemplate("document.xml");
        Map<String,String> dataMap = new HashMap<String, String>();
        dataMap.put("managerName","Mohamed Amine");
        dataMap.put("managerRole","manager/tech lead");
        dataMap.put("eventName","Team building");
        dataMap.put("eventDate","08/04/2024");
        dataMap.put("eventStartHours","08:00 AM");
        dataMap.put("eventEndHours","12:00 PM");
        dataMap.put("participantName","Safwen Soker");
        dataMap.put("location","Zaghouan");
        dataMap.put("signDate","07/4/2024");

        String outFilePath = basePath+"data.xml";
        File docFile = new File(outFilePath);
        FileOutputStream fos = new FileOutputStream(docFile);
        Writer out = new BufferedWriter(new OutputStreamWriter(fos),10240);
        template.process(dataMap,out);
        if(out != null){
            out.close();
        }
        try {
            ZipInputStream zipInputStream = ZipUtils.wrapZipInputStream(new FileInputStream(new File(basePath+"test.zip")));
            ZipOutputStream zipOutputStream = ZipUtils.wrapZipOutputStream(new FileOutputStream(new File(basePath+"test.docx")));
            String itemname = "word/document.xml";
            ZipUtils.replaceItem(zipInputStream, zipOutputStream, itemname, new FileInputStream(new File(basePath+"data.xml")));
            System.out.println("success");

        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static  void makePdfByXcode(){
        long startTime=System.currentTimeMillis();
        try {
            XWPFDocument document=new XWPFDocument(new FileInputStream(new File(basePath+"test.docx")));
            //    document.setParagraph(new Pa );
            File outFile=new File(basePath+"attestation.pdf");
            outFile.getParentFile().mkdirs();
            OutputStream out=new FileOutputStream(outFile);
            //    IFontProvider fontProvider = new AbstractFontRegistry();
            PdfOptions options= PdfOptions.create();  //gb2312
            PdfConverter.getInstance().convert(document,out,options);

        }
        catch (  Exception e) {
            e.printStackTrace();
        }
        System.out.println("Generate ooxml.pdf with " + (System.currentTimeMillis() - startTime) + " ms.");
    }
}
