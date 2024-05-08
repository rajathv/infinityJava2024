package com.temenos.dbx.product.security.businessdelegate.impl;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.google.gson.JsonObject;
import com.kony.dbp.exception.ApplicationException;
import com.kony.dbputilities.util.CryptoText;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.kony.dbputilities.util.logger.LoggerUtil;
import com.temenos.dbx.product.dto.CaptchaDTO;
import com.temenos.dbx.product.security.backenddelegate.api.CaptchaBackendDelegate;
import com.temenos.dbx.product.security.businessdelegate.api.CaptchaBusinessDelegate;

public class CaptchaBusinessDelegateImpl implements CaptchaBusinessDelegate {

    LoggerUtil logger = new LoggerUtil(CaptchaBusinessDelegateImpl.class);

    @Override
    public CaptchaDTO getEncodedImage(int captchaLength) throws ApplicationException {
        CaptchaBackendDelegate captchaBD = DBPAPIAbstractFactoryImpl
                .getBackendDelegate(CaptchaBackendDelegate.class);

        return captchaBD.getEncodedImage(captchaLength);
    }

    @Override
    public String generateEncodedCaptchaPayload(CaptchaDTO dto) throws ApplicationException {
        String encryptedPayload;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("captchaValue", dto.getCaptchaValue());
        try {
            encryptedPayload = CryptoText.encrypt(jsonObject.toString());
        } catch (Exception e) {
            logger.error("Error occured while encrypting the captcha value");
            throw new ApplicationException(ErrorCodeEnum.ERR_10342);
        }

        return encryptedPayload;
    }

    @Override
    public boolean verifyCaptchaValue(String inputCaptchaValue, String generatedCaptchaValue, boolean isCaseSensitive)
            throws ApplicationException {
        CaptchaBackendDelegate captchaBD = DBPAPIAbstractFactoryImpl
                .getBackendDelegate(CaptchaBackendDelegate.class);

        return captchaBD.verifyCaptchaValue(inputCaptchaValue, generatedCaptchaValue, isCaseSensitive);
    }

    @Override
    public String generateEncodedCaptchaPayloadForRetailUserEnrollment(CaptchaDTO dto) throws ApplicationException {
        String encryptedPayload;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("captchaValue", dto.getCaptchaValue());
        jsonObject.addProperty(DBPUtilitiesConstants.IS_RETAILUSER_ENROLLEMENT, dto.getCaptchaValue());
        try {
            encryptedPayload = CryptoText.encrypt(jsonObject.toString());
        } catch (Exception e) {
            logger.error("Error occured while encrypting the captcha value");
            throw new ApplicationException(ErrorCodeEnum.ERR_10807);
        }

        return encryptedPayload;
    }

}
