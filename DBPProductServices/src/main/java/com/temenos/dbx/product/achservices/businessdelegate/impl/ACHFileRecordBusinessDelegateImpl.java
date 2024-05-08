package com.temenos.dbx.product.achservices.businessdelegate.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceExecutorBuilder;
import com.dbp.core.util.JSONUtils;
import com.temenos.dbx.product.achservices.businessdelegate.api.ACHFileRecordBusinessDelegate;
import com.temenos.dbx.product.achservices.dto.ACHFileRecordDTO;
import com.temenos.dbx.product.constants.ACHConstants;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;
import com.kony.dbputilities.util.CommonUtils;
import com.kony.dbputilities.util.DBPUtilitiesConstants;

public class ACHFileRecordBusinessDelegateImpl implements ACHFileRecordBusinessDelegate {

	private static final Logger LOG = LogManager.getLogger(ACHFileRecordBusinessDelegateImpl.class);

	@Override
	public List<ACHFileRecordDTO> fetchACHFileRecords(String achFileId) {

		List<ACHFileRecordDTO> resultDTO = null;
		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.DB_ACHFILERECORD_GET;
		
		Map<String, Object> requestParams = new HashMap<String, Object>();
		String filter = ACHConstants.ACH_FILE_ID + DBPUtilitiesConstants.EQUAL + achFileId;
		requestParams.put(DBPUtilitiesConstants.FILTER, filter);

		try {
			String fetchResponse = DBPServiceExecutorBuilder.builder().
					withServiceId(serviceName).
					withObjectId(null).
					withOperationId(operationName).
					withRequestParameters(requestParams).
					build().getResponse();
			JSONObject responseObj = new JSONObject(fetchResponse);
			JSONArray jsonArray = CommonUtils.getFirstOccuringArray(responseObj);
			resultDTO = JSONUtils.parseAsList(jsonArray.toString(), ACHFileRecordDTO.class);
			
		} catch (Exception exp) {
			LOG.error("Exception occured in delegate while fetching ach file records", exp);
			return null;
		}

		return resultDTO;
	}

}
