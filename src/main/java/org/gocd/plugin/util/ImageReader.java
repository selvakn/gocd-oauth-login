package org.gocd.plugin.util;

import org.apache.commons.io.IOUtils;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

public class ImageReader {
    public static String readImage(String path){
        try {
            return "data:image/png;base64," + DatatypeConverter.printBase64Binary(IOUtils.toByteArray(ImageReader.class.getClassLoader().getResourceAsStream(path)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
