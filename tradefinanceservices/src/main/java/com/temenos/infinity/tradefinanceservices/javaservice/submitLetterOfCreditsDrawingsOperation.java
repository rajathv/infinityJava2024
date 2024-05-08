/*******************************************************************************
 * Copyright © Temenos Headquarters SA 2022. All rights reserved.
 ******************************************************************************/
package com.temenos.infinity.tradefinanceservices.javaservice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.common.JavaService2;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.temenos.infinity.tradefinanceservices.constants.ErrorCodeEnum;
import com.temenos.infinity.tradefinanceservices.dto.DrawingsDTO;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.tradefinanceservices.resource.api.LetterOfCreditDrawingsResource;

public class submitLetterOfCreditsDrawingsOperation implements JavaService2{
	private static final Logger LOG = LogManager.getLogger(submitLetterOfCreditsDrawingsOperation.class);

	public Object invoke(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		LetterOfCreditDrawingsResource drawingsCreateOrder = DBPAPIAbstractFactoryImpl
                .getResource(LetterOfCreditDrawingsResource.class);
        DrawingsDTO drawingsOrder = constructPayload(request);                  
        try {
        Result result = drawingsCreateOrder.submitLetterOfCreditDrawings(drawingsOrder, request);
        return result;
        }
        catch(Exception e) {
        	LOG.error("Error" + e);
        	return ErrorCodeEnum.ERRTF_29066.setErrorCode(new Result());
        }
	}
	private DrawingsDTO constructPayload(DataControllerRequest request) {
		 DrawingsDTO drawings = new DrawingsDTO();

		 String acceptance = request.getParameter("acceptance") != null ? request.getParameter("acceptance") : "";
		 String accountToBeDebited = request.getParameter("accountToBeDebited") != null ? request.getParameter("accountToBeDebited") : "";
		 String messageToBank = request.getParameter("messageToBank") != null ? request.getParameter("messageToBank") : "";
		 String drawingsSRMSID = request.getParameter("drawingsSRMSID") != null ? request.getParameter("drawingsSRMSID") : "";
		 String reasonForRejection =request.getParameter("reasonForRejection") != null ? request.getParameter("reasonForRejection") : "";
		 drawings.setReasonForRejection(reasonForRejection);
		 drawings.setAccountToBeDebited(accountToBeDebited);
		 drawings.setMessageToBank(messageToBank);
		 drawings.setAcceptance(acceptance);
		 drawings.setMessageToBank(messageToBank);
		 drawings.setDrawingsSrmsReqOrderID(drawingsSRMSID);

		 return drawings;
	}
}
