package com.temenos.dbx.product.security.backenddelegate.impl;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;

import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.temenos.dbx.product.dto.CaptchaDTO;
import com.temenos.dbx.product.security.backenddelegate.api.CaptchaBackendDelegate;

import nl.captcha.Captcha;
import nl.captcha.text.producer.DefaultTextProducer;

public class CaptchaBackendDelegateImpl implements CaptchaBackendDelegate {
    private static final String IMAGEFORMAT = "png";

    LoggerUtil logger = new LoggerUtil(CaptchaBackendDelegateImpl.class);

    @Override
    public CaptchaDTO getEncodedImage(int captchaLength) throws ApplicationException {

        char[] captchaCharacters = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j',
                'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                'u', 'v', 'w', 'x', 'y', 'z',
                'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
                'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'

        };

        if (captchaLength <= 0) {
            captchaLength = DBPUtilitiesConstants.DEFAULT_CAPTCHA_LENGTH;
        }
        try {
            CaptchaDTO dto = new CaptchaDTO();
            Captcha captcha = new Captcha.Builder(200, 50)
                    .addText(new DefaultTextProducer(captchaLength, captchaCharacters))
                    .addBackground()
                    .addBorder()
                    .gimp()
                    .build();
            dto.setCaptchaValue(captcha.getAnswer());
            byte[] imageBytes = getImageHexCode(captcha);
            dto.setEncodedCaptcha(Base64.getEncoder().encodeToString(imageBytes));
            return dto;
        } catch (Exception e) {
            logger.error("Caught exception while generating captcha using nl.captcha: ", e);
            throw new ApplicationException(ErrorCodeEnum.ERR_10342);
        }
    }

    private byte[] getImageHexCode(Captcha captcha) throws ApplicationException {
        byte[] imageInByte = null;
        BufferedImage buffImage = captcha.getImage();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            ImageIO.write(buffImage, IMAGEFORMAT, baos);
            baos.flush();
            imageInByte = baos.toByteArray();
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new ApplicationException(ErrorCodeEnum.ERR_10342);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error("Caught exception while generating image hexa: ", e);
            throw new ApplicationException(ErrorCodeEnum.ERR_10342);
        }

        return imageInByte;
    }

    @Override
    public boolean verifyCaptchaValue(String inputCaptchaValue, String generatedCaptchaValue, boolean isCaseSensitive)
            throws ApplicationException {
        if (StringUtils.isBlank(inputCaptchaValue) || StringUtils.isBlank(generatedCaptchaValue)) {
            throw new ApplicationException(ErrorCodeEnum.ERR_10347);
        }

        return isCaseSensitive ? inputCaptchaValue.equals(generatedCaptchaValue)
                : inputCaptchaValue.equalsIgnoreCase(generatedCaptchaValue);
    }

}
