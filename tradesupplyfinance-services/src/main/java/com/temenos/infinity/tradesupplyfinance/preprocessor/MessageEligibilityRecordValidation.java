/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2023. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradesupplyfinance.preprocessor;

import com.kony.memorymgmt.CorporateManager;
import com.konylabs.middleware.common.DataPreProcessor2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradesupplyfinance.constants.ErrorCodeEnum;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author k.meiyazhagan
 */
public class MessageEligibilityRecordValidation implements DataPreProcessor2 {
    @Override
    public boolean execute(HashMap hashMap, DataControllerRequest request, DataControllerResponse response, Result result) throws Exception {
        try {
            String[] subjectAndRecord = request.getParameter("requestsubject").split("-");
            if (subjectAndRecord.length != 2
                    || !Arrays.asList("RCID_TSF_BILLS").contains(request.getParameter("requestcategory_id"))) {
                ErrorCodeEnum.ERR_30001.setErrorCode(result);
                return false;
            }

            CorporateManager corpManager = new CorporateManager(request);
            if (!corpManager.isRecordMessageEligible(subjectAndRecord[1].trim())) {
                ErrorCodeEnum.ERR_30008.setErrorCode(result);
                return false;
            }
        } catch (Exception e) {
            ErrorCodeEnum.ERR_30025.setErrorCode(result);
            return false;
        }
        return true;
    }
}
