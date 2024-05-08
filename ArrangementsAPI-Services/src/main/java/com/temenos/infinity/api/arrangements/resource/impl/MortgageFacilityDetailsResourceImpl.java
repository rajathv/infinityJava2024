package com.temenos.infinity.api.arrangements.resource.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;
import com.temenos.infinity.api.arrangements.businessdelegate.api.MortgageFacilityDetailsBusinessDelegate;
import com.temenos.infinity.api.arrangements.businessdelegate.api.UserManagementBusinessDelegate;
import com.temenos.infinity.api.arrangements.constants.ErrorCodeEnum;
import com.temenos.infinity.api.arrangements.dto.CustomerDetailsDTO;
import com.temenos.infinity.api.arrangements.dto.UserAccountSettingsDTO;
import com.temenos.infinity.api.arrangements.resource.api.MortgageFacilityDetailsResource;
import com.temenos.infinity.api.arrangements.resource.api.UserManagementResource;
import com.temenos.infinity.api.arrangements.utils.CustomerSession;
import com.temenos.infinity.api.commons.exception.ApplicationException;
import com.temenos.infinity.transact.tokenmanager.exception.CertificateNotRegisteredException;

public class MortgageFacilityDetailsResourceImpl implements MortgageFacilityDetailsResource {

	private static final Logger LOG = LogManager.getLogger(MortgageFacilityDetailsResourceImpl.class);

	@Override
	public Result getMortageFacilityDetails(String methodId, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) throws Exception {
		// TODO Auto-generated method stub
		Result result = new Result();
		try {
			MortgageFacilityDetailsBusinessDelegate mortgageFacilityDelegateInstance = DBPAPIAbstractFactoryImpl
                    .getBusinessDelegate(MortgageFacilityDetailsBusinessDelegate.class);
			result = mortgageFacilityDelegateInstance.getMortageFacilityDetails(methodId,inputArray,request,response);
        } catch (Exception e) {
            ErrorCodeEnum.ERR_20057.setErrorCode(result);
        }
        return result;
	}

}
