package com.kony.dbp.holidayservices.businessdelegate.impl;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dbp.core.fabric.extn.DBPServiceInvocationWrapper;
import com.dbp.core.util.JSONUtils;
import com.kony.dbp.holidayservices.businessdelegate.api.HolidayServicesBusinessDelegate;
import com.kony.dbp.holidayservices.businessdelegate.impl.HolidayServicesBusinessDelegateImpl;
import com.kony.dbp.holidayservices.dto.HolidaysDTO;
import com.temenos.dbx.product.constants.OperationName;
import com.temenos.dbx.product.constants.ServiceId;

/**
 * 
 * @author KH2394
 * @version 1.0 Extends the {@link HolidayServicesBusinessDelegate}
 */
public class HolidayServicesBusinessDelegateImpl implements HolidayServicesBusinessDelegate {
	private static final Logger LOG = LogManager.getLogger(HolidayServicesBusinessDelegateImpl.class);

	@Override
	public List<HolidaysDTO> getHolidays() {

		List<HolidaysDTO> holidaysDTOs = null;

		String serviceName = ServiceId.DBPRBLOCALSERVICEDB;
		String operationName = OperationName.HOLIDAYS_GET;

		Map<String, Object> requestParams = new HashMap<String, Object>();
		requestParams.put("", "");

		try {
			String fetchResponse = DBPServiceInvocationWrapper.invokeServiceAndGetJSON(serviceName, null,
					operationName, requestParams, null, "");

			JSONObject jsonRsponse = new JSONObject(fetchResponse);
			JSONArray countJsonArray = jsonRsponse.getJSONArray("holidays");
			holidaysDTOs = JSONUtils.parseAsList(countJsonArray.toString(), HolidaysDTO.class);
		}

		catch(JSONException jsonExp) {
			LOG.error("JSONExcpetion occured while fetching the holidays",jsonExp);
			return null;
		}
		catch(Exception exp) {
			LOG.error("Exception occured while fetching the holidays",exp);
			return null;
		}

		return holidaysDTOs;
	}


}
