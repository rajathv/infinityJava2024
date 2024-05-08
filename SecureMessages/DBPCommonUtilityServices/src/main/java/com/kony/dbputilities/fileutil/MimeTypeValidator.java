package com.kony.dbputilities.fileutil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

public class MimeTypeValidator {

	private MagicMatch magicMatch = null;
	private static final Logger LOG = LogManager.getLogger(MimeTypeValidator.class);
	
	public MimeTypeValidator(byte[] fileInBytes) throws MagicParseException,
				MagicMatchNotFoundException, MagicException {
		magicMatch = Magic.getMagicMatch(fileInBytes);
	}
	
	public boolean hasValidImageMimeType() {
		String[] imageMimeTypeList = new String[] {"image/gif", "image/png",
				"image/tiff", "image/jpeg"};
		if(magicMatch != null) {
			for(String mimeType : imageMimeTypeList) {
				LOG.debug("comparing file mime type {} with list mime type {}", mimeType, magicMatch.getMimeType());
				if(mimeType.equalsIgnoreCase(magicMatch.getMimeType())) {
					return true;
				}
			}
		}
		return false;
	}
}
