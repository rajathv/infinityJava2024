package com.temenos.infinity.api.checkdep.resource.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.dbp.core.util.JSONUtils;
import com.temenos.infinity.api.checkdep.dto.RDCDTO;
import com.temenos.infinity.api.checkdep.resource.api.RDCResource;
import com.temenos.infinity.api.checkdep.businessdelegate.api.RDCBusinessDelegate;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;

public class RDCResourceImpl implements RDCResource{

	@Override
	public Result createRDC(String methodID, Object[] inputArray, DataControllerRequest request,
            DataControllerResponse response) {
		
		Logger LOG = LogManager.getLogger(RDCResourceImpl.class);
		Result result = null;
		
		try {
			
			RDCBusinessDelegate rdcDelegate = DBPAPIAbstractFactoryImpl.getInstance()
					.getFactoryInstance(BusinessDelegateFactory.class).getBusinessDelegate(RDCBusinessDelegate.class);
			
			@SuppressWarnings("unchecked")
			Map<String, Object> requestParams = (HashMap<String, Object>) inputArray[1];
			
			JSONObject requestObj = new JSONObject(requestParams);
			RDCDTO createDTO = JSONUtils.parse(requestObj.toString(), RDCDTO.class);
			
			RDCDTO resultDTO = rdcDelegate.createRDC(createDTO, request);
			
			if (resultDTO == null) {
				LOG.error("Error occured while Creating RDC");
				return ErrorCodeEnum.ERR_12611.setErrorCode(new Result());
			}

			if (StringUtils.isNotBlank(resultDTO.getDbpErrMsg())) {
				LOG.error("Error occurred while Creating RDC");
				return ErrorCodeEnum.ERR_00000.setErrorCode(new Result(), resultDTO.getDbpErrMsg());
			}
			
			if (StringUtils.isNotBlank(resultDTO.getErrmsg())) {
				LOG.error("Error occurred while Creating RDC");
				return ErrorCodeEnum.ERR_00000.setErrorCode(new Result(), resultDTO.getErrmsg());
			}
			
			JSONObject responseDTO = new JSONObject(resultDTO);
			result = JSONToResult.convert(responseDTO.toString());
		}
		catch(Exception exp) {
			LOG.error("Exception occurred while defining resource to create RDC", exp);
			return ErrorCodeEnum.ERR_12611.setErrorCode(new Result());
		}
		
		return result;

	}

}
