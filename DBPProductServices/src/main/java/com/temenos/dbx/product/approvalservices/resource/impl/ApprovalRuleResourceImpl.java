package com.temenos.dbx.product.approvalservices.resource.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.api.factory.BusinessDelegateFactory;
import com.dbp.core.api.factory.impl.DBPAPIAbstractFactoryImpl;
import com.temenos.dbx.product.approvalservices.businessdelegate.api.ApprovalRuleBusinessDelegate;
import com.temenos.dbx.product.approvalservices.dto.ApprovalRuleDTO;
import com.temenos.dbx.product.approvalservices.resource.api.ApprovalRuleResource;
import com.kony.dbputilities.util.ErrorCodeEnum;
import com.konylabs.middleware.controller.DataControllerRequest;
import com.konylabs.middleware.controller.DataControllerResponse;
import com.konylabs.middleware.dataobject.JSONToResult;
import com.konylabs.middleware.dataobject.Result;

/**
 * 
 * @author KH2174
 * @version 1.0 Extends the {@link ApprovalRuleResource}
 */
public class ApprovalRuleResourceImpl implements ApprovalRuleResource {
	private static final Logger LOG = LogManager.getLogger(ApprovalRuleResourceImpl.class);

	@Override
	public Result getRules(String methodID, Object[] inputArray, DataControllerRequest request,
			DataControllerResponse response) {
		Result result = new Result();

		// Initialization of business Delegate Class
		ApprovalRuleBusinessDelegate ruleBusinessDelegate = DBPAPIAbstractFactoryImpl.getInstance()
				.getFactoryInstance(BusinessDelegateFactory.class)
				.getBusinessDelegate(ApprovalRuleBusinessDelegate.class);

		try {
			List<ApprovalRuleDTO> ruleDTOs = (ArrayList<ApprovalRuleDTO>) ruleBusinessDelegate.getAllRules();
			if (ruleDTOs == null) {
				return ErrorCodeEnum.ERR_12000.setErrorCode(new Result());
			}
			//for JSON Object use directly JSONToResult.convert(JSONObject) to get result
			JSONArray rulesJSONArr = new JSONArray(ruleDTOs);
			JSONObject responseObj = new JSONObject();
			responseObj.put("approvalrule", rulesJSONArr);
			result = JSONToResult.convert(responseObj.toString());

		} catch (Exception e) {
			LOG.error("Caught exception at getRules method: " + e);
		}

		return result;
	}

}
