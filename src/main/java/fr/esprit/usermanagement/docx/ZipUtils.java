package fr.esprit.usermanagement.docx;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class ZipUtils

{
    public static void replaceItem(ZipInputStream zipInputStream,
                                   ZipOutputStream zipOutputStream,
                                   String itemName,
                                   InputStream itemInputStream
    ){
        //
        if(null == zipInputStream){return;}
        if(null == zipOutputStream){return;}
        if(null == itemName){return;}
        if(null == itemInputStream){return;}
        //
        ZipEntry entryIn;
        try {
            while((entryIn = zipInputStream.getNextEntry())!=null)
            {
                String entryName =  entryIn.getName();
                ZipEntry entryOut = new ZipEntry(entryName);
                zipOutputStream.putNextEntry(entryOut);
                byte [] buf = new byte[8*1024];
                int len;

                if(entryName.equals(itemName)){
                    while((len = (itemInputStream.read(buf))) > 0) {
                        zipOutputStream.write(buf, 0, len);
                    }
                } else {
                    while((len = (zipInputStream.read(buf))) > 0) {
                        zipOutputStream.write(buf, 0, len);
                    }
                }
                zipOutputStream.closeEntry();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            close(itemInputStream);
            close(zipInputStream);
            close(zipOutputStream);
        }
    }

    /**
     * 包装输入流
     */
    public static ZipInputStream wrapZipInputStream(InputStream inputStream){
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);
        return zipInputStream;
    }

    /**
     * 包装输出流
     */
    public static ZipOutputStream wrapZipOutputStream(OutputStream outputStream){
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        return zipOutputStream;
    }
    private static void close(InputStream inputStream){
        if (null != inputStream){
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void close(OutputStream outputStream){
        if (null != outputStream){
            try {
                outputStream.flush();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
