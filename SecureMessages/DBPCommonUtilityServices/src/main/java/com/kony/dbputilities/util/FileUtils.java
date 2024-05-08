package com.kony.dbputilities.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileUtils {
    private static final Logger LOG = LogManager.getLogger(FileUtils.class);

    /**
     * @param fileObject
     * @return base64 encoded format of the file
     * @description reads a file input stream and converts to base64 code
     */
    public static String encodeFile(File fileObject) {
    	
    	if (!isValidFileName(fileObject) && !fileObject.exists() && !fileObject.isFile() && !(fileObject.length() > 0)) {
    		LOG.error("Invalid File");
    		return null;
        }
        
        try (FileInputStream fileInputStream = new FileInputStream(fileObject)) {
            String encode = "";
            byte bt[] = new byte[(int) fileObject.length()];
            fileInputStream.read(bt);
            encode = new String(Base64.encodeBase64(bt));
            fileInputStream.close();
            return encode;
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return null;
    }
    
    private static boolean isValidFileName(File f) {
	    try {
	       f.getCanonicalPath();
	       return true;
	    }
	    catch (IOException e) {
	       return false;
	    }
    }

}