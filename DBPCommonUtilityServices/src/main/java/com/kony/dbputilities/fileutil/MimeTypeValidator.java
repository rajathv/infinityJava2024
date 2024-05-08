package com.kony.dbputilities.fileutil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

public class MimeTypeValidator {

	private String mimeType;
	private static final Logger LOG = LogManager.getLogger(MimeTypeValidator.class);
	
	public MimeTypeValidator(byte[] fileInBytes) {
		InputStream is = new BufferedInputStream(new ByteArrayInputStream(fileInBytes));
		try {
			mimeType = URLConnection.guessContentTypeFromStream(is);
		} catch (IOException e) {
			LOG.error("Error occurred while parsing content / invalid file");
			mimeType = null;
		}
	}
	
	public boolean hasValidImageMimeType() {
		Set<String> imageMimeTypeSet = new HashSet<>();
		imageMimeTypeSet.add("image/gif");
		imageMimeTypeSet.add("image/png");
		imageMimeTypeSet.add("image/jpeg");
		if(mimeType != null) {
			return imageMimeTypeSet.contains(mimeType);
		}
		return false;
	}
}
