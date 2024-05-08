package com.temenos.dbx.product.commons.businessdelegate.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.dbputilities.util.DBPUtilitiesConstants;
import com.temenos.dbx.product.commons.businessdelegate.api.FeatureBusinessDelegate;
import com.temenos.dbx.product.commons.dto.FeatureDTO;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;

public class FeatureBusinessDelegateImpl implements FeatureBusinessDelegate {

	private static final Logger LOG = LogManager.getLogger(ApplicationBusinessDelegateImpl.class);

	@Override
	public List<FeatureDTO> fetchFeatures() {
		
		List<FeatureDTO> features = new ArrayList<FeatureDTO>();
		
		try {
			String response = DBPServiceExecutorBuilder.builder().
					withServiceId(ServiceId.DBPRBLOCALSERVICEDB).
					withObjectId(null).
					withOperationId(OperationName.DB_FEATURE_GET).
					withRequestParameters(null).
					build().getResponse();
			
			JSONObject res = new JSONObject(response);
			JSONArray actions = CommonUtils.getFirstOccuringArray(res);
			features = JSONUtils.parseAsList(actions.toString(), FeatureDTO.class);
		}
		catch (Exception e) {
			LOG.error("Exception caught while fetching application properties", e);
		}
		return features;
	}
	
	@Override
	public FeatureDTO fetchFeatureById(String featureId) {
		
		FeatureDTO feature = null;
		Map<String, Object> requestParameters = new HashMap<String, Object>();
		String filter = "";
		if(StringUtils.isNotEmpty(featureId)) {
			filter = "id" + DBPUtilitiesConstants.EQUAL + featureId;
		}
		requestParameters.put(DBPUtilitiesConstants.FILTER, filter);
		
		try {
			String response = DBPServiceExecutorBuilder.builder().
					withServiceId(ServiceId.DBPRBLOCALSERVICEDB).
					withObjectId(null).
					withOperationId(OperationName.DB_FEATURE_GET).
					withRequestParameters(requestParameters).
					build().getResponse();
			
			JSONObject res = new JSONObject(response);
			JSONArray actions = CommonUtils.getFirstOccuringArray(res);
			feature = JSONUtils.parse(actions.getJSONObject(0).toString(), FeatureDTO.class);
		}
		catch (Exception e) {
			LOG.error("Exception caught while fetching application properties", e);
		}
		return feature;
	}
	
}
