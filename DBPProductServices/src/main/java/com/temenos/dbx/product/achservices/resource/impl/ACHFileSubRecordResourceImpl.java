package com.temenos.dbx.product.achservices.resource.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.temenos.dbx.product.achservices.businessdelegate.api.ACHFileSubrecordBusinessDelegate;
import com.temenos.dbx.product.achservices.dto.ACHFileSubrecordDTO;
import com.temenos.dbx.product.achservices.resource.api.ACHFileSubRecordResource;
import com.temenos.dbx.product.commonsutils.CustomerSession;
import com.temenos.dbx.product.constants.ACHConstants;
import com.temenos.dbx.product.constants.FeatureAction;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;

public class ACHFileSubRecordResourceImpl implements ACHFileSubRecordResource {
	
	private static final Logger LOG = LogManager.getLogger(ACHFileSubRecordResource.class);
	
	ACHFileSubrecordBusinessDelegate achFileSubrecordBusinessDelegate = DBPAPIAbstractFactoryImpl.getBusinessDelegate(ACHFileSubrecordBusinessDelegate.class);
	
	@Override
	public Result fetchACHFileSubrecords(String methodId, Object[] inputArray, DataControllerRequest dcRequest, DataControllerResponse dcResponse) {
		
		Result result = new Result();
        
		try {
            @SuppressWarnings("unchecked")
			HashMap<String, Object> requestMap = (HashMap<String, Object>) inputArray[1];
            ACHFileSubrecordDTO requestDTO = JSONUtils.parse(new JSONObject(requestMap).toString(), ACHFileSubrecordDTO.class);
            
            String achFileRecordId = requestDTO.getAchFileRecordId();
            if(achFileRecordId == null || achFileRecordId.isEmpty()) {
                return ErrorCodeEnum.ERR_12041.setErrorCode(new Result());
            }
            
            List<String> requiredActionIds = Arrays.asList(FeatureAction.ACH_FILE_VIEW);
			String featureActionId = CustomerSession.getPermittedActionIds(dcRequest, requiredActionIds);
			
			if(featureActionId == null) {
	     		return ErrorCodeEnum.ERR_12040.setErrorCode(new Result());
			}
			
			List<ACHFileSubrecordDTO> responseDTO = achFileSubrecordBusinessDelegate.fetchACHFileSubrecords(achFileRecordId);
            JSONArray subrecords = new JSONArray(responseDTO);
            JSONObject resultObject = new JSONObject();
            resultObject.put(ACHConstants.ACHFILESUBRECORDS,subrecords);
            result = JSONToResult.convert(resultObject.toString());
        }
        catch(Exception exp) {
            LOG.error("Exception occurred while fetching ACH File Sub Records",exp);
            return ErrorCodeEnum.ERR_12049.setErrorCode(new Result());
        }
		
		return result;
	}
}